package com.nicico.internal.sales.proforma.service.cash;

import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.service.GoodBucketService;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.proforma.dto.CashSaleCreateRequest;
import com.nicico.internal.sales.proforma.dto.CashSaleDetailGenerator;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.proforma.enums.*;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.proforma.service.ProformaContractService;
import com.nicico.internal.sales.proforma.service.ProformaSerialService;
import com.nicico.internal.sales.proforma.service.ProformaValidationService;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.service.ProformaProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.nicico.internal.sales.proforma.service.ProformaModelHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashSaleServiceImpl implements CashSaleService {

	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_PROFORMA_VALIDATION_ERROR = "خطا در داده های پیش فاکتور:";
	private static final String MSG_USER_NO_ACCESS = "شما دسترسی لازم برای این عملیات را ندارید";
	private static final String MSG_NET_WEIGHT_INVALID = "مقدار وزن خالص خشک اشتباه است. وزن خالص خشک باید از مقدار نقدی بیشتر باشد";
	private static final String MSG_BROKER_NOT_FOUND = "اطلاعات کارگزار وجود ندارد";
	private static final String MSG_ISSUING_PROFORMA_NOT_VALID = "اطلاعات پیش فاکتور معتبر نیست";
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private final CustomerRepository customerRepository;
	private final GoodBucketService goodBucketService;
	private final ProformaSerialService proformaSerialService;
	private final ProformaValidationService proformaValidationService;
	private final OfferTextProcess offerTextProcess;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaProcessService proformaProcessService;
	private final ProformaContractService proformaContractService;
	private final TradeExtractRepository tradeExtractRepository;
	private final BrokerRepository brokerRepository;
	private final GoodsService goodsService;

	// ==================== PUBLIC SERVICE METHODS ====================

	@Override
	@Transactional
	public String create(CashSaleCreateRequest requestDto) {
		log.debug("Creating cash sale proforma for tradeId: {}", requestDto.getTradeId());

		// اعتبارسنجی
		proformaValidationService.validateCashSaleData(requestDto);

		if (!proformaProcessService.canStartProcess()) {
			throw new InternalSaleCustomException.ValidationException(MSG_USER_NO_ACCESS);
		}

		// تشخیص نوع کالا و ایجاد پیش فاکتور مناسب
		boolean isPrecious = isPreciousMetal(requestDto.getTradeId());
		ProformaMasterModel model = isPrecious
				? createPreciousProformaMaster(requestDto)
				: createRegularProformaMaster(requestDto);

		// شروع فرآیند و ذخیره
		startProformaProcess(model);

		// تنظیم روابط با Helper
		setupFullRelationships(model, model.getProformaDetailModelLists());

		// ذخیره با Cascade
		proformaMasterRepository.saveAndFlush(model);

		log.info("Cash sale proforma created successfully with contractNo: {}", model.getContractNo());
		return model.getContractNo().toString();
	}

	// ==================== PROFORMA MASTER CREATION ====================

	private ProformaMasterModel createRegularProformaMaster(CashSaleCreateRequest requestDto) {
		log.debug("Creating regular proforma master");
		ProformaModelResponse contractDetail = getRegularContractDetail(requestDto);
		return saveProformaMaster(contractDetail);
	}

	private ProformaMasterModel createPreciousProformaMaster(CashSaleCreateRequest requestDto) {
		log.debug("Creating precious proforma master");
		ProformaModelResponse contractDetail = getPreciousContractDetail(requestDto);
		return saveProformaMaster(contractDetail);
	}

	private ProformaMasterModel saveProformaMaster(ProformaModelResponse contractDetail) {
		ProformaMasterModel ProformaMasterModel = contractDetail.getMasterModel();
		List<ProformaDetailModel> detailList = distinctDetails(contractDetail.getDetailModels());
		ProformaMasterModel.setProformaDetailModelLists(detailList);

		// تنظیم روابط با Helper
		setupFullRelationships(ProformaMasterModel, detailList);

		return proformaMasterRepository.saveAndFlush(ProformaMasterModel);
	}

	// ==================== CONTRACT DETAIL METHODS ====================

	private ProformaModelResponse getRegularContractDetail(CashSaleCreateRequest requestDto) {
		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
		validateRequest(requestDto, tradeExtract);

		CashSaleDetailGenerator params = buildDetailGenerator(requestDto, tradeExtract);
		List<ProformaDetailModel> detailDtoList = generateRegularPerformaDetailList(params);

		// محاسبه مجموع ها و درصدها با Helper
		Totals totals = calculateTotals(detailDtoList);
		CashCreditPercentages percentages = calculateCashCreditPercentages(
				params.goodsBucketModel(),
				requestDto.isCashPercentTotal()
		);

		ProformaMasterModel masterModel = buildRegularMasterModel(
				tradeExtract, params, totals, percentages
		);

		// تنظیم روابط با Helper
		setupFullRelationships(masterModel, detailDtoList);

		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	private ProformaModelResponse getPreciousContractDetail(CashSaleCreateRequest requestDto) {
		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
		validateRequest(requestDto, tradeExtract);

		CashSaleDetailGenerator params = buildDetailGenerator(requestDto, tradeExtract);
		List<ProformaDetailModel> detailDtoList = generatePreciousPerformaDetailList(params);

		// محاسبه مجموع ها با Helper
		Totals totals = calculateTotals(detailDtoList);
		BrokerModel brokerModel = findBrokerModel(params.tradeModel());

		ProformaMasterModel masterModel = buildPreciousMasterModel(
				tradeExtract, params, totals, brokerModel
		);

		// تنظیم روابط با Helper
		setupFullRelationships(masterModel, detailDtoList);

		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	// ==================== DETAIL GENERATION ====================

	private List<ProformaDetailModel> generateRegularPerformaDetailList(CashSaleDetailGenerator params) {
		CashSaleCreateRequest requestDto = params.requestDto();
		List<String> performaSerials = proformaSerialService.getProformaSerial(requestDto.getParts().size());

		return IntStream.range(0, requestDto.getParts().size())
				.mapToObj(i -> {
					List<ProformaGoodItemModel> goodItem = generateRegularGoodItem(params, i);
					DetailTotals detailTotals = calculateDetailTotals(goodItem);

					ProformaDetailModel detailModel = buildDetailModel(
							params,
							performaSerials.get(i),
							detailTotals,
							SettlementType.UNKNOWN.name()
					);

					goodItem.forEach(item -> item.setProformaDetailModel(detailModel));
					return detailModel;
				})
				.collect(Collectors.toCollection(LinkedHashSet::new))
				.stream()
				.toList();
	}

	private List<ProformaDetailModel> generatePreciousPerformaDetailList(CashSaleDetailGenerator params) {
		List<String> performaSerials = proformaSerialService.getProformaSerial(1);
		ProformaGoodItemModel goodItem = generatePreciousGoodItem(params);
		List<ProformaGoodItemModel> goodItems = List.of(goodItem);

		DetailTotals detailTotals = calculateDetailTotals(goodItems);

		ProformaDetailModel detailModel = buildDetailModel(
				params,
				performaSerials.get(0),
				detailTotals,
				SettlementType.CASH.name()
		);

		goodItem.setProformaDetailModel(detailModel);

		return List.of(detailModel);
	}

	private ProformaDetailModel buildDetailModel(
			CashSaleDetailGenerator params,
			String performaNo,
			DetailTotals detailTotals,
			String settlementType) {

		return ProformaDetailModel.builder()
				.proformaGoodItemModels(new ArrayList<>())
				.jalaaliYear(params.jalaliYear())
				.storageDeadline(params.saleConditionModel().getStorageDeadline())
				.storageCost(params.saleConditionModel().getStorageCost())
				.creditExpirePeriod(params.saleConditionModel().getCreditExpirePeriod())
				.shippingDeadline(params.saleConditionModel().getShippingDeadline())
				.paymentDeferral(params.saleConditionModel().getPaymentDeferral())
				.deadlineDays(params.requestDto().getDeadlineDays())
				.performaNo(performaNo)
				.performaDate(new Date())
				.totalAmount(detailTotals.totalAmount())
				.finalPrice(detailTotals.finalAmount())
				.vatAmount(detailTotals.vatAmount())
				.saleType(SaleType.EXWORKS)
				.settlementType(settlementType)
				.proformaIssueType(ProformaIssueType.FROM_CREDIT_FACILITIES)
				.orderDate(params.requestDto().getOrderDate())
				.contractDate(params.tradeModel().getContractDate())
				.proformaReversalStatus(ProformaReversalStatus.NORMAL)
				.extraBillOfPercent(BigDecimal.ZERO)
				.extraBillOfExchangeAmount(BigDecimal.ZERO)
				.build();
	}

	// ==================== GOOD ITEM GENERATION ====================

	private List<ProformaGoodItemModel> generateRegularGoodItem(CashSaleDetailGenerator params, int rank) {
		TradeExtractModel tradeExtract = findTradeExtract(params.requestDto().getTradeId());
		String description = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String lot = offerTextProcess.extractLotNumber(description);
		String cleanName = processGoodName(params.good(), description);

		double quantity = params.requestDto().getParts().get(rank).doubleValue();

		// استفاده از Helper برای محاسبات مالی
		CashGoodItemCalculation calc = calculateCashGoodItem(
				params.tradeModel(),
				params.goodsBucketModel(),
				params.vat(),
				quantity,
				params.requestDto().isCashPercentTotal(),
				params.good().getId(),
				cleanName,
				lot
		);

		return List.of(buildGoodItemModel(calc));
	}

	private ProformaGoodItemModel generatePreciousGoodItem(CashSaleDetailGenerator params) {
		TradeExtractModel tradeExtract = params.tradeExtract();

		double netWeight = params.requestDto().getNetWeight().doubleValue();
		double totalWeight = params.requestDto().getTotalWeight().doubleValue();

		// استفاده از Helper برای محاسبات مالی
		PreciousGoodItemCalculation calc = calculatePreciousGoodItem(
				params.tradeModel(),
				params.goodsBucketModel(),
				params.vat(),
				netWeight,
				totalWeight,
				params.requestDto().isCashPercentTotal(),
				params.good().getId(),
				params.good().getDescription(),
				null
		);

		// اعتبارسنجی وزن
		validateNetWeight(calc);

		String finalGoodName = buildPreciousGoodName(params.good(), tradeExtract);
		String lot = extractLotNumber(tradeExtract);

		return buildPreciousGoodItemModel(calc, finalGoodName, lot);
	}

	private ProformaGoodItemModel buildGoodItemModel(CashGoodItemCalculation calc) {
		return ProformaGoodItemModel.builder()
				.goodId(calc.goodId())
				.goodName(calc.goodName())
				.unitId(calc.unitId())
				.vatPercent(calc.vatPercent())
				.quantity(calc.quantity())
				.creditQuantity(calc.creditQuantity())
				.unitPriceCredit(calc.unitPriceCredit())
				.unitPriceCash(calc.unitPriceCash())
				.unitPrice(calc.unitPrice())
				.creditAmount(calc.creditAmount())
				.cashAmount(calc.cashAmount())
				.vatCashAmount(calc.vatCashAmount())
				.vatCreditAmount(calc.vatCreditAmount())
				.vatAmount(calc.vatAmount())
				.interestPercent(calc.interestPercent())
				.totalAmount(calc.totalAmount())
				.finalAmount(calc.finalAmount())
				.netQuantity(calc.netQuantity())
				.lotNumber(calc.lotNumber())
				.creditPercentage(calc.creditPercentage())
				.build();
	}

	private ProformaGoodItemModel buildPreciousGoodItemModel(
			PreciousGoodItemCalculation calc,
			String goodName,
			String lot) {

		return ProformaGoodItemModel.builder()
				.goodId(calc.goodId())
				.goodName(goodName)
				.unitId(calc.unitId())
				.lotNumber(lot)
				.quantity(calc.quantity())
				.creditQuantity(calc.creditQuantity())
				.unitPriceCredit(calc.unitPriceCredit())
				.unitPriceCash(calc.unitPriceCash())
				.unitPrice(calc.unitPrice())
				.creditAmount(calc.creditAmount())
				.cashAmount(calc.cashAmount())
				.vatCashAmount(calc.vatCashAmount())
				.vatCreditAmount(calc.vatCreditAmount())
				.vatAmount(calc.vatAmount())
				.vatPercent(calc.vatPercent())
				.interestPercent(calc.interestPercent())
				.totalAmount(calc.totalAmount())
				.finalAmount(calc.finalAmount())
				.creditPercentage(calc.creditPercentage())
				.netQuantity(calc.netQuantity())
				.build();
	}

	// ==================== MASTER MODEL BUILDERS ====================

	private ProformaMasterModel buildRegularMasterModel(
			TradeExtractModel tradeExtract,
			CashSaleDetailGenerator params,
			Totals totals,
			CashCreditPercentages percentages) {

		CustomerModel customerModel = params.customerModel();
		IMETradeModel tradeModel = params.tradeModel();

		return ProformaMasterModel.builder()
				.deadlineDays(params.requestDto().getDeadlineDays())
				.commissionPercentage(0.0)
				.cashPercentage(percentages.cashPercentage())
				.creditPercentage(percentages.creditPercentage())
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.customerId(customerModel.getId())
				.customerName(customerModel.getName())
				.nationalCode(customerModel.getNationalCode())
				.phone(customerModel.getPhone())
				.economicCode(customerModel.getEconomicCode())
				.registerNumber(customerModel.getRegisterNumber())
				.postCode(customerModel.getPostCode())
				.address(customerModel.getAddress())
				.totalCashAmount(totals.totalCashAmount())
				.totalQuantity(totals.totalQuantity())
				.totalCreditAmount(totals.totalCreditAmount())
				.totalVatAmount(totals.totalVatAmount())
				.totalFinalAmount(totals.totalFinalAmount())
				.workflowApproveStatus(WorkflowApproveStatus.DRAFT)
				.proformaIssueType(ProformaIssueType.FROM_CREDIT_FACILITIES)
				.goodId(params.good().getId())
				.goodName(params.good().getName())
				.isProcessFinal(false)
				.isReversalProcessFinal(false)
				.contractDate(params.tradeModel().getContractDate())
				.tradeId(params.requestDto().getTradeId())
				.settlementType(SettlementType.UNKNOWN.name())
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.build();
	}

	private ProformaMasterModel buildPreciousMasterModel(
			TradeExtractModel tradeExtract,
			CashSaleDetailGenerator params,
			Totals totals,
			BrokerModel brokerModel) {

		CustomerModel customerModel = params.customerModel();
		IMETradeModel tradeModel = params.tradeModel();
		GoodsBucketModel goodsBucketModel = params.goodsBucketModel();

		return ProformaMasterModel.builder()
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.cashPercentage(goodsBucketModel.getCashPercentage())
				.commissionPercentage(0.0)
				.deadlineDays(params.requestDto().getDeadlineDays())
				.creditPercentage(HUNDRED.subtract(goodsBucketModel.getCashPercentage()))
				.customerId(customerModel.getId())
				.customerName(customerModel.getName())
				.nationalCode(customerModel.getNationalCode())
				.phone(customerModel.getPhone())
				.economicCode(customerModel.getEconomicCode())
				.registerNumber(customerModel.getRegisterNumber())
				.postCode(customerModel.getPostCode())
				.address(customerModel.getAddress())
				.totalCashAmount(totals.totalCashAmount())
				.totalQuantity(totals.totalQuantity())
				.totalCreditAmount(totals.totalCreditAmount())
				.totalVatAmount(totals.totalVatAmount())
				.totalFinalAmount(totals.totalFinalAmount())
				.workflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS)
				.proformaIssueType(ProformaIssueType.FROM_CREDIT_FACILITIES)
				.goodId(params.good().getId())
				.goodName(params.good().getDescription())
				.contractDate(params.tradeModel().getContractDate())
				.tradeId(params.requestDto().getTradeId())
				.isProcessFinal(false)
				.isReversalProcessFinal(false)
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.settlementType(SettlementType.CASH.name())
				.build();
	}

	// ==================== HELPER METHODS ====================

	private CashSaleDetailGenerator buildDetailGenerator(
			CashSaleCreateRequest requestDto,
			TradeExtractModel tradeExtract) {

		int jalaliYear = DateUtility.getJalaliYear(requestDto.getOrderDate());
		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());
		GoodsModel goodsModel = proformaContractService.getGoodsModel(tradeExtract.getPaymentCode());
		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
		GoodsBucketModel goodsBucketModel = getGoodBucketModel(tradeExtract.getPaymentCode());
		CustomerModel customerModel = getCustomerModel(tradeExtract.getPaymentCode());

		return new CashSaleDetailGenerator(
				requestDto,
				tradeModel,
				proformaContractService.getVat(jalaliYear),
				goodsModel,
				jalaliYear,
				customerModel,
				goodsBucketModel,
				saleConditionModel,
				tradeExtract,
				requestDto.isCashPercentTotal()
		);
	}

	private void validateRequest(CashSaleCreateRequest requestDto, TradeExtractModel tradeExtract) {
		proformaValidationService.validateDate(requestDto);
		proformaValidationService.validateProformaData(requestDto);

		if (!proformaValidationService.validateCashSaleData(requestDto).isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_ISSUING_PROFORMA_NOT_VALID);
		}
	}

	private void startProformaProcess(ProformaMasterModel model) {
		ProformaVariablesInput input = ProformaVariablesInput.builder()
				.contractDate(model.getProformaDetailModelLists().get(0).getContractDate())
				.proformaMasterId(model.getId())
				.goodId(model.getGoodId())
				.contractNo(String.valueOf(model.getContractNo()))
				.customerName(model.getCustomerName())
				.goodName(model.getGoodName())
				.commission(model.getCommissionPercentage())
				.build();

		var process = proformaProcessService.startProformaProcess(input);
		model.setProcessId(process.getId());
		model.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
	}

	private void validateNetWeight(PreciousGoodItemCalculation calc) {
		List<String> errors = new ArrayList<>();

		if (calc.creditQuantity().compareTo(BigDecimal.ONE) < 0) {
			errors.add("مقدار اعتباری باید حداقل 1 باشد");
		}
		if (calc.creditAmount().compareTo(BigDecimal.ONE) < 0) {
			errors.add("مبلغ اعتباری باید حداقل 1 باشد");
		}
		if (calc.vatAmount().compareTo(BigDecimal.ONE) < 0) {
			errors.add("مبلغ مالیات باید حداقل 1 باشد");
		}
		if (calc.cashQuantity().compareTo(calc.netQuantity()) > 0) {
			errors.add("مقدار نقدی نمی تواند از وزن خالص بیشتر باشد");
		}

		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(
					MSG_NET_WEIGHT_INVALID + ": " + String.join(", ", errors)
			);
		}
	}

	private String buildPreciousGoodName(GoodsModel good, TradeExtractModel tradeExtract) {
		String cleanName = getCleanName(good);
		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String selenium = offerTextProcess.hasSelenium(rawDescription);
		return cleanName + " " + selenium;
	}

	private String extractLotNumber(TradeExtractModel tradeExtract) {
		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String lot = offerTextProcess.extractLotNumber(rawDescription);
		return lot != null ? lot : DEFAULT_PLACEHOLDER;
	}

	private boolean isPreciousMetal(Long tradeId) {
		try {
			TradeExtractModel tradeExtract = findTradeExtract(tradeId);
			GoodsModel good = proformaContractService.getGoodsModel(tradeExtract.getPaymentCode());
			return goodsService.isPreciousMetal(good.getId());
		} catch (Exception e) {
			log.warn("Error checking precious metal for tradeId: {}", tradeId, e);
			return false;
		}
	}

	private BrokerModel findBrokerModel(IMETradeModel tradeModel) {
		return brokerRepository.findById(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_BROKER_NOT_FOUND));
	}

	private TradeExtractModel findTradeExtract(Long tradeId) {
		return tradeExtractRepository.findById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	// ==================== PUBLIC SERVICE METHODS ====================

	public GoodsBucketModel getGoodBucketModel(String paymentCode) {
		return goodBucketService.findByPaymentCodeModel(paymentCode);
	}

	public CustomerModel getCustomerModel(String paymentCode) {
		List<String> err = new ArrayList<>();
		return customerRepository.findByNationalCode(
				proformaContractService.getTradeModel(paymentCode).getBuyerNationalCode()
		).orElseThrow(() -> new InternalSaleCustomException.ValidationException(
				MSG_PROFORMA_VALIDATION_ERROR, err));
	}

	public String getCleanName(GoodsModel goodsModel) {
		return goodsModel.getName()
				.replace(goodsModel.getImeCommoditySymbol(), "")
				.replace("_", "")
				.trim();
	}
}