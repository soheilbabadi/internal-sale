package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.goods.service.GoodBucketService;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.proforma.dto.PerfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.PerformaDetailGenerator;
import com.nicico.internal.sales.proforma.dto.PerformerCreateRevealRequest;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.SaleType;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.salecondition.service.SaleConditionService;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.vat.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.nicico.internal.sales.proforma.service.ProformaModelHelper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProformaContractServiceImpl implements ProformaContractService {

	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_GOOD_NOT_FOUND = "کالای مورد نظر وجود ندارد";
	private static final String MSG_VAT_NOT_FOUND = "مالیات بر ارزش افزوده تعریف نشده است";
	private static final String MSG_CUSTOMER_NOT_FOUND = "اطلاعات مشتری پیدا نشد";
	private static final String MSG_PROFORMA_VALIDATION_ERROR = "خطا در داده های پیش فاکتور:";
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final String SETTLEMENT_TYPE_DEFAULT = SettlementType.UNKNOWN.name();
	private static final BigDecimal EXTRA_BILL_PERCENT_DEFAULT = BigDecimal.ZERO;
//	private static final int GAM_CERTIFICATE_COUNT_DEFAULT = 0;

	private final IMETradeRepository imeTradeRepository;
	private final GoodsRepository goodsRepository;
	private final VatRepository vatRepository;
	private final CustomerRepository customerRepository;
	private final SaleConditionService saleConditionService;
	private final ProformaSerialService proformaSerialService;
	private final ProformaValidationService proformaValidationService;
	private final OfferTextProcess offerTextProcess;
	private final ProformaMasterRepository proformaMasterRepository;
	private final GoodBucketService goodBucketService;
	private final TradeExtractRepository tradeExtractRepository;

	// ==================== PUBLIC SERVICE METHODS ====================

	@Override
	@Transactional
	public ProformaCreationContext getProformaCreationData(Long tradeId, String paymentCode, Integer jalaliYear) {
		log.debug("Fetching proforma creation data for tradeId: {}, paymentCode: {}", tradeId, paymentCode);
		
		TradeExtractModel tradeExtract = findTradeExtract(tradeId);
		String actualPaymentCode = paymentCode != null ? paymentCode : tradeExtract.getPaymentCode();
		
		IMETradeModel tradeModel = getTradeModel(actualPaymentCode);
		GoodsModel goodsModel = getGoodsModel(actualPaymentCode);
		SaleConditionModel saleConditionModel = getSaleConditionModel(actualPaymentCode);
		GoodsBucketModel goodsBucketModel = getGoodBucketModel(actualPaymentCode);
		CustomerModel customerModel = getCustomerModel(tradeExtract.getBuyerNationalCode());
		
		return ProformaCreationContext.builder()
				.tradeExtract(tradeExtract)
				.tradeModel(tradeModel)
				.goodsModel(goodsModel)
				.saleConditionModel(saleConditionModel)
				.goodsBucketModel(goodsBucketModel)
				.customerModel(customerModel)
				.jalaliYear(jalaliYear)
				.build();
	}

	@Override
	@Transactional
	public ProformaModelResponse getContractDetail(PerfomaCreateRequest requestDto) {
		log.debug("Creating proforma contract detail for tradeId: {}", requestDto.getTradeId());

		int jalaliYear = DateUtility.getJalaliYear(requestDto.getOrderDate());
		ProformaCreationContext context = getProformaCreationData(
				requestDto.getTradeId(), 
				null, // Payment code will be fetched from TradeExtract
				jalaliYear
		);

		PerformaDetailGenerator params = createPerformaDetailGenerator(
				requestDto, context.getTradeModel(), context.getGoodsModel(), 
				jalaliYear, context.getGoodsBucketModel(), context.getSaleConditionModel()
		);

		List<ProformaDetailModel> detailDtoList = generatePerformaDetailList(params);

		// محاسبه مجموع‌ها و درصدها با استفاده از Helper
		Totals totals = calculateTotals(detailDtoList);
		CashCreditPercentages percentages = calculateCashCreditPercentages(context.getGoodsBucketModel(), false);

		ProformaMasterModel masterModel = buildMasterModel(
				context.getTradeExtract(), context.getTradeModel(), context.getGoodsModel(), 
				context.getCustomerModel(), context.getGoodsBucketModel(), 
				requestDto, totals, percentages
		);

		// تنظیم روابط با استفاده از Helper
		setupFullRelationships(masterModel, detailDtoList);

		log.info("Proforma contract detail created successfully");
		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	@Override
	@Transactional
	public ProformaModelResponse getContractDetailReversal(PerformerCreateRevealRequest requestDto) {
		log.debug("Creating reversal proforma contract for masterId: {}", requestDto.getMasterId());

		ProformaMasterModel existingMaster = findMasterModel(requestDto.getMasterId());
		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());

		validateReversal(existingMaster);

		// استخراج آیتم‌های ابطال شده
		List<ProformaDetailModel> canceledDetails = filterDetailsByStatus(
				existingMaster.getProformaDetailModelLists(),
				ProformaReversalStatus.CANCELED
		);

		if (canceledDetails.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(
					"هیچ آیتم ابطالی برای برگشت وجود ندارد",
					List.of("هیچ آیتم ابطالی برای برگشت وجود ندارد")
			);
		}

		int jalaliYear = canceledDetails.get(0).getJalaaliYear();
		IMETradeModel tradeModel = findTradeModelByTradeId(existingMaster.getTradeId());
		GoodsModel goodsModel = findGoodsModel(existingMaster.getGoodId());
		SaleConditionModel saleConditionModel = getSaleConditionModel(tradeExtract.getPaymentCode());
		GoodsBucketModel goodsBucketModel = findGoodsBucketModelOnDate(
				existingMaster.getGoodId(),
				DateUtility.toGregorianDate(existingMaster.getContractDate())
		);

		// تولید جزئیات جدید
		List<ProformaDetailModel> newDetails = generateReversalDetails(
				requestDto, tradeModel, goodsModel, saleConditionModel, goodsBucketModel, jalaliYear
		);

		// ساخت Master جدید
		ProformaMasterModel newMasterModel = buildReversalMasterModel(existingMaster, requestDto, tradeExtract);

		// کپی جزئیات موجود
		List<ProformaDetailModel> copiedDetails = copyDetailModel(canceledDetails, newMasterModel, requestDto);

		// ترکیب جزئیات
		List<ProformaDetailModel> allDetails = combineDetails(copiedDetails, newDetails);
		newMasterModel.setProformaDetailModelLists(allDetails);

		// به‌روزرسانی وضعیت‌ها
		updateReversalStatuses(allDetails, requestDto.getExitsProformaNo());

		// محاسبه مجموع‌ها با فیلتر
		Totals totals = calculateTotalsWithFilter(
				allDetails,
				detail -> detail.getProformaReversalStatus() != ProformaReversalStatus.CANCELED
		);
		applyTotalsToMaster(newMasterModel, totals);

		// تنظیم روابط با استفاده از Helper
		setupFullRelationships(newMasterModel, allDetails);

		log.info("Reversal proforma contract created successfully for masterId: {}", requestDto.getMasterId());
		return ProformaModelResponse.builder()
				.masterModel(newMasterModel)
				.detailModels(allDetails)
				.build();
	}

	// ==================== PRIVATE METHODS ====================

	private PerformaDetailGenerator createPerformaDetailGenerator(
			PerfomaCreateRequest requestDto,
			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			int jalaliYear,
			GoodsBucketModel goodsBucketModel,
			SaleConditionModel saleConditionModel) {

		PerfomaCreateRequest performaCreateRequest = new PerfomaCreateRequest();
		performaCreateRequest.setTradeId(requestDto.getTradeId());
		performaCreateRequest.setParts(requestDto.getParts());
		performaCreateRequest.setTotalWeight(requestDto.getTotalWeight());
		performaCreateRequest.setOrderDate(requestDto.getOrderDate());
		performaCreateRequest.setDeadlineDays(requestDto.getDeadlineDays());
		performaCreateRequest.setProformaIssueType(requestDto.getProformaIssueType());

		return new PerformaDetailGenerator(
				performaCreateRequest,
				tradeModel,
				getVat(jalaliYear),
				goodsModel,
				jalaliYear,
				goodsBucketModel,
				saleConditionModel
		);
	}

	private List<ProformaDetailModel> generateReversalDetails(
			PerformerCreateRevealRequest requestDto,
			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			SaleConditionModel saleConditionModel,
			GoodsBucketModel goodsBucketModel,
			int jalaliYear) {

		PerfomaCreateRequest performaCreateRequest = new PerfomaCreateRequest();
		performaCreateRequest.setTradeId(requestDto.getTradeId());
		performaCreateRequest.setParts(requestDto.getParts());
		performaCreateRequest.setTotalWeight(requestDto.getTotalWeight());
		performaCreateRequest.setOrderDate(requestDto.getOrderDate());
		performaCreateRequest.setDeadlineDays(requestDto.getDeadlineDays());
		performaCreateRequest.setProformaIssueType(requestDto.getProformaIssueType());

		PerformaDetailGenerator params = new PerformaDetailGenerator(
				performaCreateRequest,
				tradeModel,
				getVat(jalaliYear),
				goodsModel,
				jalaliYear,
				goodsBucketModel,
				saleConditionModel
		);

		List<ProformaDetailModel> details = generatePerformaDetailList(params);
		details.forEach(detail -> detail.setProformaReversalStatus(ProformaReversalStatus.EDITED));

		return details;
	}

	private ProformaMasterModel buildReversalMasterModel(
			ProformaMasterModel existingMaster,
			PerformerCreateRevealRequest requestDto,
			TradeExtractModel tradeExtract) {

		return ProformaMasterModel.builder()
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.deadlineDays(requestDto.getDeadlineDays())
				.workflowApproveStatus(WorkflowApproveStatus.DRAFT)
				.proformaIssueType(requestDto.getProformaIssueType())
				.tradeId(existingMaster.getTradeId())
				.creditPercentage(existingMaster.getCreditPercentage())
				.cashPercentage(existingMaster.getCashPercentage())
				.commissionPercentage(existingMaster.getCommissionPercentage())
				.customerId(existingMaster.getCustomerId())
				.customerName(existingMaster.getCustomerName())
				.nationalCode(existingMaster.getNationalCode())
				.phone(existingMaster.getPhone())
				.economicCode(existingMaster.getEconomicCode())
				.registerNumber(existingMaster.getRegisterNumber())
				.postCode(existingMaster.getPostCode())
				.address(existingMaster.getAddress())
				.goodId(existingMaster.getGoodId())
				.goodName(existingMaster.getGoodName())
				.contractDate(existingMaster.getContractDate())
				.brokerId(existingMaster.getBrokerId())
				.brokerName(existingMaster.getBrokerName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(existingMaster.getImeCommoditySymbol())
				.offerDescription(existingMaster.getOfferDescription())
				.settlementType(SETTLEMENT_TYPE_DEFAULT)
				.isProcessFinal(true)
				.isReversalProcessFinal(false)
				.build();
	}

	private ProformaMasterModel buildMasterModel(
			TradeExtractModel tradeExtract,
			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			CustomerModel customerModel,
			GoodsBucketModel goodsBucketModel,
			PerfomaCreateRequest requestDto,
			Totals totals,
			CashCreditPercentages percentages) {

		return ProformaMasterModel.builder()
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.cashPercentage(percentages.cashPercentage())
				.creditPercentage(percentages.creditPercentage())
				.commissionPercentage(goodsBucketModel.getCommission())
				.deadlineDays(requestDto.getDeadlineDays())
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
				.proformaIssueType(requestDto.getProformaIssueType())
				.goodId(goodsModel.getId())
				.goodName(goodsModel.getDescription())
				.isProcessFinal(false)
				.isReversalProcessFinal(false)
				.contractDate(tradeModel.getContractDate())
				.tradeId(requestDto.getTradeId())
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.settlementType(SETTLEMENT_TYPE_DEFAULT)
				.build();
	}

	private List<ProformaDetailModel> generatePerformaDetailList(PerformaDetailGenerator params) {
		PerfomaCreateRequest requestDto = params.requestDto();
		Integer jalaliYear = params.jalaliYear();
		List<String> serial = proformaSerialService.getProformaSerial(requestDto.getParts().size());

		List<ProformaDetailModel> detailDtoList = new ArrayList<>();

		for (int i = 0; i < requestDto.getParts().size(); i++) {
			ProformaGoodItemModel goodItem = generatePerformaGoodItem(params, i);

			// استفاده از Helper برای محاسبه مجموع‌های Detail
			DetailTotals detailTotals = calculateDetailTotals(List.of(goodItem));

			ProformaDetailModel detailModel = ProformaDetailModel.builder()
					.proformaGoodItemModels(List.of(goodItem))
					.jalaaliYear(jalaliYear)
					.storageDeadline(params.saleConditionModel().getStorageDeadline())
					.storageCost(params.saleConditionModel().getStorageCost())
					.creditExpirePeriod(params.saleConditionModel().getCreditExpirePeriod())
					.shippingDeadline(params.saleConditionModel().getShippingDeadline())
					.paymentDeferral(params.saleConditionModel().getPaymentDeferral())
					.deadlineDays(requestDto.getDeadlineDays())
					.performaNo(serial.get(i))
					.performaDate(new Date())
					.totalAmount(detailTotals.totalAmount())
					.finalPrice(detailTotals.finalAmount())
					.vatAmount(detailTotals.vatAmount())
					.saleType(SaleType.EXWORKS)
					.settlementType(SETTLEMENT_TYPE_DEFAULT)
					.proformaIssueType(requestDto.getProformaIssueType())
					.orderDate(requestDto.getOrderDate())
					.contractDate(params.tradeModel().getContractDate())
					.proformaReversalStatus(ProformaReversalStatus.NORMAL)
					.gamCertificateCount(0)
					.extraBillOfExchangeAmount(EXTRA_BILL_PERCENT_DEFAULT)
					.build();

			// تنظیم رابطه Detail -> GoodItem
			goodItem.setProformaDetailModel(detailModel);
			detailDtoList.add(detailModel);
		}

		return new ArrayList<>(detailDtoList).stream().toList();
	}

	private ProformaGoodItemModel generatePerformaGoodItem(PerformaDetailGenerator params, int rank) {
		TradeExtractModel tradeExtract = findTradeExtract(params.requestDto().getTradeId());
		String description = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String lot = offerTextProcess.extractLotNumber(description);
		String cleanName = getCleanGoodName(params.good(), description);

		Long quantity = params.requestDto().getParts().get(rank).longValue();

		// استفاده از Helper برای محاسبات مالی
		CashGoodItemCalculation calc = calculateCashGoodItem(
				params.tradeModel(),
				params.goodsBucketModel(),
				params.vat(),
				quantity.doubleValue(),
				false,
				params.good().getId(),
				cleanName,
				lot
		);

		return buildGoodItemFromCalculation(calc);
	}

	private ProformaGoodItemModel buildGoodItemFromCalculation(CashGoodItemCalculation calc) {
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

	private List<ProformaDetailModel> copyDetailModel(
			List<ProformaDetailModel> detailModelList,
			ProformaMasterModel newMasterModel,
			PerformerCreateRevealRequest requestDto) {

		return detailModelList.stream()
				.map(detailModel -> copySingleDetail(detailModel, newMasterModel, requestDto))
				.collect(Collectors.toList());
	}

	private ProformaDetailModel copySingleDetail(
			ProformaDetailModel detailModel,
			ProformaMasterModel newMasterModel,
			PerformerCreateRevealRequest requestDto) {

		ProformaGoodItemModel goodItem = detailModel.getProformaGoodItemModels().get(0);
		ProformaGoodItemModel copiedGoodItem = copyGoodItem(goodItem);

		ProformaReversalStatus reversalStatus = requestDto.getExitsProformaNo()
				.contains(detailModel.getPerformaNo())
				? ProformaReversalStatus.EDITED
				: ProformaReversalStatus.CANCELED;

		ProformaDetailModel copiedDetailModel = ProformaDetailModel.builder()
				.performaNo(detailModel.getPerformaNo())
				.performaDate(detailModel.getPerformaDate())
				.orderDate(detailModel.getOrderDate())
				.contractDate(detailModel.getContractDate())
				.totalAmount(detailModel.getTotalAmount())
				.finalPrice(detailModel.getFinalPrice())
				.vatAmount(detailModel.getVatAmount())
				.saleType(detailModel.getSaleType())
				.settlementType(SETTLEMENT_TYPE_DEFAULT)
				.deadlineDays(detailModel.getDeadlineDays())
				.proformaIssueType(detailModel.getProformaIssueType())
				.jalaaliYear(detailModel.getJalaaliYear())
				.storageDeadline(detailModel.getStorageDeadline())
				.storageCost(detailModel.getStorageCost())
				.creditExpirePeriod(detailModel.getCreditExpirePeriod())
				.shippingDeadline(detailModel.getShippingDeadline())
				.paymentDeferral(detailModel.getPaymentDeferral())
				.proformaReversalStatus(reversalStatus)
				.gamCertificateCount(detailModel.getGamCertificateCount())
				.extraBillOfExchangeAmount(detailModel.getExtraBillOfExchangeAmount())
				.ProformaMasterModel(newMasterModel)
				.build();

		copiedGoodItem.setProformaDetailModel(copiedDetailModel);
		copiedDetailModel.setProformaGoodItemModels(new ArrayList<>(List.of(copiedGoodItem)));

		return copiedDetailModel;
	}

	private ProformaGoodItemModel copyGoodItem(ProformaGoodItemModel goodItem) {
		return ProformaGoodItemModel.builder()
				.goodId(goodItem.getGoodId())
				.goodName(goodItem.getGoodName())
				.unitId(goodItem.getUnitId())
				.quantity(goodItem.getQuantity())
				.creditQuantity(goodItem.getCreditQuantity())
				.unitPriceCredit(goodItem.getUnitPriceCredit())
				.unitPriceCash(goodItem.getUnitPriceCash())
				.unitPrice(goodItem.getUnitPrice())
				.creditAmount(goodItem.getCreditAmount())
				.cashAmount(goodItem.getCashAmount())
				.vatCashAmount(goodItem.getVatCashAmount())
				.vatCreditAmount(goodItem.getVatCreditAmount())
				.vatAmount(goodItem.getVatAmount())
				.vatPercent(goodItem.getVatPercent())
				.interestPercent(goodItem.getInterestPercent())
				.totalAmount(goodItem.getTotalAmount())
				.finalAmount(goodItem.getFinalAmount())
				.netQuantity(goodItem.getNetQuantity())
				.lotNumber(goodItem.getLotNumber())
				.creditPercentage(goodItem.getCreditPercentage())
				.build();
	}

	private String getCleanGoodName(GoodsModel goodsModel, String description) {
		String cleanName = goodsModel.getDescription()
				.replace(goodsModel.getImeCommoditySymbol(), "")
				.replace("_", "")
				.trim();

		if (description.contains("مولیبدن سونگون")) {
			return "سولفور مولیبدن سونگون";
		} else if (description.contains("مولیبدن سرچشمه")) {
			return "سولفور مولیبدن";
		}
		return cleanName;
	}

	private void applyTotalsToMaster(ProformaMasterModel masterModel, Totals totals) {
		masterModel.setTotalCashAmount(totals.totalCashAmount());
		masterModel.setTotalQuantity(totals.totalQuantity());
		masterModel.setTotalCreditAmount(totals.totalCreditAmount());
		masterModel.setTotalVatAmount(totals.totalVatAmount());
		masterModel.setTotalFinalAmount(totals.totalFinalAmount());
	}

	private void updateReversalStatuses(List<ProformaDetailModel> details, List<String> exitProformaNos) {
		details.forEach(detail -> {
			if (exitProformaNos.contains(detail.getPerformaNo())) {
				detail.setProformaReversalStatus(ProformaReversalStatus.NORMAL);
			}
		});
	}

	private List<ProformaDetailModel> combineDetails(
			List<ProformaDetailModel> copiedDetails,
			List<ProformaDetailModel> newDetails) {

		List<ProformaDetailModel> combined = new ArrayList<>(copiedDetails);
		combined.addAll(newDetails);
		return combined;
	}

	private void validateReversal(ProformaMasterModel masterModel) {
		masterModel.setIsProcessFinal(true);
		masterModel.setIsReversalProcessFinal(false);
		proformaValidationService.validateReversal(masterModel.getId());
	}

	// ==================== REPOSITORY FINDER METHODS ====================

	private TradeExtractModel findTradeExtract(Long tradeId) {
		return tradeExtractRepository.findById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	private ProformaMasterModel findMasterModel(Long masterId) {
		return proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PROFORMA_NOT_FOUND));
	}

	private IMETradeModel findTradeModelByTradeId(Long tradeId) {
		return imeTradeRepository.findFirstByIdOrderByIdDesc(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	private GoodsModel findGoodsModel(Long goodId) {
		return goodsRepository.findById(goodId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_GOOD_NOT_FOUND));
	}

	private GoodsBucketModel findGoodsBucketModelOnDate(Long goodId, Date date) {
		return goodBucketService.getOnSpecificDateModel(goodId, date);
	}

	// ==================== PUBLIC SERVICE METHODS (Override) ====================

	@Override
	public IMETradeModel getTradeModel(String paymentCode) {
		return imeTradeRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	@Override
	public IMETradeModel getTradeModel(Long tradeId) {
		return imeTradeRepository.findFirstByIdOrderByIdDesc(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	@Override
	public GoodsModel getGoodsModel(String paymentCode) {
		return goodsRepository.findByImeCommodityId(Long.valueOf(getTradeModel(paymentCode).getCommodityCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_GOOD_NOT_FOUND));
	}

	@Override
	public SaleConditionModel getSaleConditionModel(String paymentCode) {
		return saleConditionService.getSaleConditionByPaymentCode(paymentCode);
	}

	@Override
	public BigDecimal getVat(Integer jalaliYear) {
		return vatRepository.findByJalaliYear(jalaliYear)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_VAT_NOT_FOUND))
				.getVatCoefficient();
	}

	@Override
	public GoodsBucketModel getGoodBucketModel(String paymentCode) {
		return goodBucketService.findByPaymentCodeModel(paymentCode);
	}

	@Override
	public CustomerModel getCustomerModel(String nationalCode) {
		return customerRepository.findByNationalCode(nationalCode)
				.orElseThrow(() -> {
					List<String> err = List.of(MSG_CUSTOMER_NOT_FOUND);
					return new InternalSaleCustomException.ValidationException(MSG_PROFORMA_VALIDATION_ERROR, err);
				});
	}

	@Override
	public String getCleanName(GoodsModel goodsModel) {
		return goodsModel.getDescription()
				.replace(goodsModel.getImeCommoditySymbol(), "")
				.replace("_", "")
				.trim();
	}
}