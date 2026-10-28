package com.nicico.internal.sales.proforma.service.cash;

import com.nicico.internal.sales.customer.model.CustomerModel;
import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.deposit.repository.CashDepositRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.dto.CashDepositCreateRequest;
import com.nicico.internal.sales.proforma.dto.CashDepositDetailGenerator;
import com.nicico.internal.sales.proforma.dto.CashSaleCreateRequest;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.proforma.enums.*;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.proforma.service.ProformaContractService;
import com.nicico.internal.sales.proforma.service.ProformaValidationService;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import com.nicico.internal.sales.trade.model.TradeExtractStartProformaModel;
import com.nicico.internal.sales.trade.repository.TradeExtractStartProformaRepository;
import com.nicico.internal.sales.vat.repository.VatRepository;
import com.nicico.internal.sales.wf.detection.CashDepositStepDetector;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.service.ProcessVariableProvider;
import com.nicico.internal.sales.wf.service.ProformaProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class CashDepositReceiptSaleServiceImpl implements CashDepositReceiptSaleService {

	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_USER_NO_ACCESS = "شما دسترسی لازم برای این عملیات را ندارید";
	private static final String MSG_BROKER_NOT_FOUND = "اطلاعات کارگزار وجود ندارد";
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private final ProformaValidationService proformaValidationService;
	private final OfferTextProcess offerTextProcess;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final ProformaProcessService proformaProcessService;
	private final ProformaContractService proformaContractService;
	private final TradeExtractStartProformaRepository tradeExtractRepository;
	private final PreciousMetalRepository preciousMetalRepository;
	private final ProcessVariableProvider processVariableProvider;
	private final CashDepositRepository cashDepositRepository;
	private final VatRepository vatRepository;
	private final CashDepositStepDetector cashDepositStepDetector;

	@Override
	public CashDepositCreateRequest generateSuggestion(CashSaleCreateRequest request) {
		cashDepositStepDetector.updateAllAcknowledgments();
		TradeExtractModel trade = findTradeExtract(request.getTradeId());
		int financialYear = Integer.parseInt(trade.getContractDate().substring(0, 4));
		var vatModel = vatRepository.findByJalaliYear(financialYear)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("مالیات برای سال " + financialYear + " تعریف نشده است"));

		boolean preciousMetal = isPreciousMetal(request.getTradeId());
		BigDecimal unitPrice = trade.getUnitPrice() != null ? BigDecimal.valueOf(trade.getUnitPrice()) : BigDecimal.ZERO;
		BigDecimal contractQuantity = trade.getUnitCount() != null ? BigDecimal.valueOf(trade.getUnitCount()) : BigDecimal.ZERO;
		BigDecimal contractAmount = unitPrice.multiply(contractQuantity);
		BigDecimal saleQuantity = (preciousMetal && request.getNetWeight() != null) ? request.getNetWeight() : contractQuantity;
		BigDecimal saleAmount = unitPrice.multiply(saleQuantity);
		BigDecimal cashBase = preciousMetal ? contractAmount : saleAmount;
		BigDecimal cashDeposit = cashBase.multiply(percentOf(trade.getCashPercentage()));
		BigDecimal amountWithoutTax = roundUp(saleAmount.subtract(cashDeposit));
		BigDecimal vatPercent = percentOf(vatModel.getVatCoefficient());
		BigDecimal vatAmount = roundUp(saleAmount.multiply(vatPercent));
		BigDecimal amountWithTax = amountWithoutTax.add(vatAmount);

		return buildDeposit(request, trade, preciousMetal, saleQuantity, unitPrice,
				amountWithoutTax, amountWithTax, vatAmount);
	}

	private CashDepositCreateRequest buildDeposit(CashSaleCreateRequest request,
	                                              TradeExtractModel trade,
	                                              boolean preciousMetal,
	                                              BigDecimal netWeight,
	                                              BigDecimal unitPrice,
	                                              BigDecimal amountWithoutTax,
	                                              BigDecimal amountWithTax,
	                                              BigDecimal vatAmount) {
		CashDepositCreateRequest result = new CashDepositCreateRequest();
		result.setContractNo(request.getContractNo());
		result.setTradeId(trade.getId());
		result.setCashDepositAmountWithoutTax(amountWithoutTax);
		result.setCashDepositAmountWithTax(amountWithTax);
		result.setVatAmount(vatAmount);
		result.setDepositSlipType(null);
		result.setPreciousMetal(preciousMetal);
		result.setNetWeight(netWeight);
		result.setUnitPrice(unitPrice);
		result.setOrderDate(request.getOrderDate());
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String create(CashDepositCreateRequest requestDto) {

		log.debug("Creating cash deposit for tradeId: {}", requestDto.getTradeId());
		proformaValidationService.validateCashDepositData(requestDto);
		if (!proformaProcessService.canStartProcess())
			throw new InternalSaleCustomException.ValidationException(MSG_USER_NO_ACCESS);

		boolean isPrecious = isPreciousMetal(requestDto.getTradeId());
		ProformaMasterModel model = isPrecious ? createPreciousProformaMaster(requestDto) : createRegularProformaMaster(requestDto);
		ProformaMasterModel startedModel = startProformaProcess(model);
		proformaMasterRepository.saveAndFlush(startedModel);

		log.info("Cash sale proforma created successfully with contractNo: {}", model.getContractNo());
		CashDepositModel cashDepositModel = new CashDepositModel();
		cashDepositModel.setAcknowledgment(Acknowledgment.RECKONING);
		cashDepositModel.setIssueDate(requestDto.getOrderDate());
		cashDepositModel.setNosaCode(null);
		cashDepositModel.setContractNo(requestDto.getContractNo());
		cashDepositModel.setProformaDetailId(startedModel.getProformaDetailModelLists().get(0).getId());
		cashDepositModel.setProformaMasterId(startedModel.getId());
		cashDepositModel.setTradeId(requestDto.getTradeId());
		cashDepositModel.setReckoningSend(false);
		cashDepositModel.setReckoningSendDate(null);
		cashDepositModel.setDepositSlipType(requestDto.getDepositSlipType());
		cashDepositModel.setDepositSlipWithoutTax(requestDto.getCashDepositAmountWithoutTax());
		cashDepositModel.setDepositSlipWithTax(requestDto.getCashDepositAmountWithTax());

		cashDepositRepository.saveAndFlush(cashDepositModel);

		return model.getContractNo().toString();
	}

	// ==================== PROFORMA MASTER CREATION ====================

	private ProformaMasterModel createRegularProformaMaster(CashDepositCreateRequest requestDto) {
		log.debug("Creating regular proforma master");
		ProformaModelResponse contractDetail = getRegularContractDetail(requestDto);
		return saveProformaWithDetails(contractDetail);
	}

	private ProformaMasterModel createPreciousProformaMaster(CashDepositCreateRequest requestDto) {
		log.debug("Creating precious cash deposit");
		ProformaModelResponse contractDetail = getPreciousContractDetail(requestDto);

		return saveProformaWithDetails(contractDetail);
	}

	private ProformaMasterModel saveProformaWithDetails(ProformaModelResponse contractDetail) {
		ProformaMasterModel master = contractDetail.getMasterModel();
		List<ProformaDetailModel> details = distinctDetails(contractDetail.getDetailModels());
		ProformaMasterModel savedMaster = proformaMasterRepository.saveAndFlush(master);
		saveDetailsAndGoodItems(savedMaster, details);
		savedMaster.setProformaDetailModelLists(details);
		proformaMasterRepository.flush();
		return savedMaster;
	}

	private void saveDetailsAndGoodItems(ProformaMasterModel master, List<ProformaDetailModel> details) {
		for (ProformaDetailModel detail : details) {
			// نسخه ی مستقل از لیست، قبل از save گرفته می شود
			List<ProformaGoodItemModel> goodItems = new ArrayList<>(detail.getProformaGoodItemModels());

			detail.setProformaMasterModel(master);
			detail.setProformaMasterId(master.getId());
			ProformaDetailModel savedDetail = proformaDetailRepository.save(detail);

			goodItems.forEach(item -> {
				item.setProformaDetailModel(savedDetail);
				item.setProformaDetailId(savedDetail.getId());
			});
			proformaGoodItemRepository.saveAll(goodItems);
		}
	}

	// ==================== CONTRACT DETAIL METHODS ====================

	private ProformaModelResponse getRegularContractDetail(CashDepositCreateRequest requestDto) {
		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());

		CashDepositDetailGenerator params = buildDetailGenerator(requestDto, tradeExtract);
		List<ProformaDetailModel> detailDtoList = generateRegularPerformaDetailList(params);
		CashCreditPercentages percentages = new CashCreditPercentages(HUNDRED, BigDecimal.ZERO);
		ProformaMasterModel masterModel = buildRegularMasterModel(tradeExtract, params, percentages);
		setupFullRelationships(masterModel, detailDtoList);

		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	private ProformaModelResponse getPreciousContractDetail(CashDepositCreateRequest requestDto) {
		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
		CashDepositDetailGenerator params = buildDetailGenerator(requestDto, tradeExtract);
		List<ProformaDetailModel> detailDtoList = generatePreciousPerformaDetailList(params);
		ProformaMasterModel masterModel = buildPreciousMasterModel(tradeExtract, params);
		setupFullRelationships(masterModel, detailDtoList);

		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	// ==================== DETAIL GENERATION ====================

	private List<ProformaDetailModel> generateRegularPerformaDetailList(CashDepositDetailGenerator params) {

		TradeExtractModel tradeExtract = params.tradeExtract();
		BigDecimal totalWeight = params.requestDto().getNetWeight();
		String lot = extractLotNumber(tradeExtract);
		// استفاده از Helper برای محاسبات مالی
		CashGoodItemCalculation calc = calculateCashGoodItem(
				params.tradeModel(), params.goodsBucketModel(), params.vat(),
				totalWeight.doubleValue(), true, params.good().getId(), params.good().getDescription(), lot);

		return IntStream.range(0, 1)
				.mapToObj(i -> {
					List<ProformaGoodItemModel> goodItem = List.of(buildGoodItemModel(calc));
					DetailTotals detailTotals = calculateDetailTotals(goodItem);
					ProformaDetailModel detailModel = buildDetailModel(params);
					attachGoodItems(detailModel, goodItem);
					return detailModel;
				})
				.collect(Collectors.toCollection(LinkedHashSet::new))
				.stream()
				.toList();
	}

	private List<ProformaDetailModel> generatePreciousPerformaDetailList(CashDepositDetailGenerator params) {

		ProformaGoodItemModel goodItem = generatePreciousGoodItem(params);
		List<ProformaGoodItemModel> goodItems = List.of(goodItem);
		ProformaDetailModel detailModel = buildDetailModel(params);
		attachGoodItems(detailModel, goodItems);

		return List.of(detailModel);
	}

	private void attachGoodItems(ProformaDetailModel detailModel, List<ProformaGoodItemModel> goodItems) {
		goodItems.forEach(item -> item.setProformaDetailModel(detailModel));
		detailModel.getProformaGoodItemModels().addAll(goodItems);
	}

	private ProformaDetailModel buildDetailModel(CashDepositDetailGenerator params) {
		BigDecimal totalAmount;
		BigDecimal vatAmount = params.requestDto().getCashDepositAmountWithTax().subtract(params.requestDto().getCashDepositAmountWithTax());
		BigDecimal finalPrice;
		if (params.requestDto().getDepositSlipType() == DepositSlipType.WITH_TAX) {
			finalPrice = params.requestDto().getCashDepositAmountWithTax();
		} else {
			finalPrice = params.requestDto().getCashDepositAmountWithoutTax();

		}
		totalAmount = finalPrice.subtract(vatAmount);

		return ProformaDetailModel.builder()
				.proformaGoodItemModels(new ArrayList<>())
				.jalaaliYear(params.jalaliYear())
				.storageDeadline(params.saleConditionModel().getStorageDeadline())
				.storageCost(params.saleConditionModel().getStorageCost())
				.creditExpirePeriod(params.saleConditionModel().getCreditExpirePeriod())
				.shippingDeadline(params.saleConditionModel().getShippingDeadline())
				.paymentDeferral(params.saleConditionModel().getPaymentDeferral())
				.deadlineDays(params.saleConditionModel().getShippingDeadline())
				.performaNo("000000000")
				.performaDate(new Date())
				.totalAmount(totalAmount)
				.finalPrice(finalPrice)
				.vatAmount(vatAmount)
				.saleType(SaleType.EXWORKS)
				.settlementType(SettlementType.CASH.name())
				.proformaIssueType(ProformaIssueType.CASH_DEPOSIT_RECEIPT)
				.orderDate(params.requestDto().getOrderDate())
				.contractDate(params.tradeModel().getContractDate())
				.proformaReversalStatus(ProformaReversalStatus.NORMAL)
				.extraBillOfPercent(BigDecimal.ZERO)
				.extraBillOfExchangeAmount(BigDecimal.ZERO)
				.build();
	}

	private ProformaGoodItemModel generatePreciousGoodItem(CashDepositDetailGenerator params) {
		double netWeight = params.requestDto().getNetWeight().doubleValue();
		PreciousGoodItemCalculation calc = calculatePreciousGoodItem(
				params.tradeModel(),
				params.goodsBucketModel(),
				params.vat(),
				netWeight, netWeight,
				true,
				params.good().getId(),
				params.good().getDescription(),
				extractLotNumber(params.tradeExtract())
		);
		return buildPreciousGoodItemModel(calc);
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

	private ProformaGoodItemModel buildPreciousGoodItemModel(PreciousGoodItemCalculation calc) {

		return ProformaGoodItemModel.builder()
				.goodId(calc.goodId())
				.goodName(calc.goodName())
				.unitId(calc.unitId())
				.lotNumber(calc.lotNumber())
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
			CashDepositDetailGenerator params,

			CashCreditPercentages percentages) {

		CustomerModel customerModel = params.customerModel();
		IMETradeModel tradeModel = params.tradeModel();

		BigDecimal finalAmount = BigDecimal.ZERO;
		if (params.requestDto().getDepositSlipType() == DepositSlipType.WITH_TAX)
			finalAmount = params.requestDto().getCashDepositAmountWithTax();
		if (params.requestDto().getDepositSlipType() == DepositSlipType.WITHOUT_TAX)
			finalAmount = params.requestDto().getCashDepositAmountWithoutTax();
		return ProformaMasterModel.builder()
				.deadlineDays(0)
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
				.totalCashAmount(params.requestDto().getCashDepositAmountWithoutTax())
				.totalQuantity(params.requestDto().getNetWeight())
				.totalCreditAmount(BigDecimal.ZERO)
				.totalVatAmount(params.requestDto().getVatAmount())
				.totalFinalAmount(finalAmount)
				.workflowApproveStatus(WorkflowApproveStatus.DRAFT)
				.proformaIssueType(ProformaIssueType.CASH_DEPOSIT_RECEIPT)
				.goodId(params.good().getId())
				.goodName(params.good().getName())
				.isProcessFinal(false)
				.isReversalProcessFinal(false)
				.contractDate(params.tradeModel().getContractDate())
				.tradeId(params.requestDto().getTradeId())
				.settlementType(SettlementType.CASH.name())
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.depositSlipWithoutTax(params.requestDto().getCashDepositAmountWithoutTax())
				.depositSlipWithoutTax(params.requestDto().getCashDepositAmountWithoutTax())
				.depositSlipType(params.requestDto().getDepositSlipType())
				.build();
	}

	private ProformaMasterModel buildPreciousMasterModel(
			TradeExtractModel tradeExtract,
			CashDepositDetailGenerator params

	) {

		CustomerModel customerModel = params.customerModel();
		IMETradeModel tradeModel = params.tradeModel();
		BigDecimal finalAmount = BigDecimal.ZERO;
		if (params.requestDto().getDepositSlipType() == DepositSlipType.WITH_TAX)
			finalAmount = params.requestDto().getCashDepositAmountWithTax();
		if (params.requestDto().getDepositSlipType() == DepositSlipType.WITHOUT_TAX)
			finalAmount = params.requestDto().getCashDepositAmountWithoutTax();
		return ProformaMasterModel.builder()
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.cashPercentage(tradeExtract.getCashPercentage())
				.commissionPercentage(0.0)
				.deadlineDays(0)
				.creditPercentage(BigDecimal.ZERO)
				.customerId(customerModel.getId())
				.customerName(customerModel.getName())
				.nationalCode(customerModel.getNationalCode())
				.phone(customerModel.getPhone())
				.economicCode(customerModel.getEconomicCode())
				.registerNumber(customerModel.getRegisterNumber())
				.postCode(customerModel.getPostCode())
				.address(customerModel.getAddress())
				.totalCashAmount(params.requestDto().getCashDepositAmountWithoutTax())
				.totalQuantity(params.requestDto().getNetWeight())
				.totalCreditAmount(BigDecimal.ZERO)
				.totalVatAmount(params.requestDto().getVatAmount())
				.totalFinalAmount(finalAmount)
				.workflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS)
				.proformaIssueType(ProformaIssueType.CASH_DEPOSIT_RECEIPT)
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

	private CashDepositDetailGenerator buildDetailGenerator(
			CashDepositCreateRequest requestDto,
			TradeExtractModel tradeExtract) {
		int jalaliYear = Integer.parseInt(tradeExtract.getContractDate().substring(0, 4));
		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());
		GoodsModel goodsModel = proformaContractService.getGoodsModel(tradeExtract.getPaymentCode());
		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
		GoodsBucketModel goodsBucketModel = proformaContractService.getGoodBucketModel(tradeExtract.getPaymentCode());
		CustomerModel customerModel = proformaContractService.getCustomerModelByPaymentCode(tradeExtract.getPaymentCode());

		return new CashDepositDetailGenerator(
				requestDto, tradeModel, proformaContractService.getVat(jalaliYear), goodsModel, jalaliYear,
				customerModel, goodsBucketModel, saleConditionModel, tradeExtract);
	}

	private ProformaMasterModel startProformaProcess(ProformaMasterModel masterModel) {
		ProformaVariablesInput input = processVariableProvider.buildProformaVariablesInput(masterModel);
		var process = proformaProcessService.startProformaProcess(input);
		masterModel.setProcessId(process.getId());
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		return masterModel;
	}

	private String extractLotNumber(TradeExtractModel tradeExtract) {
		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String lot = offerTextProcess.extractLotNumber(rawDescription);
		return lot != null ? lot : DEFAULT_PLACEHOLDER;
	}

	private boolean isPreciousMetal(Long tradeId) {
		try {
			TradeExtractModel tradeExtract = findTradeExtract(tradeId);
			return preciousMetalRepository.findByImeCommoditySymbol(tradeExtract.getCommoditySymbol()).isPresent();
		} catch (Exception e) {
			log.warn("Error checking precious metal for tradeId: {}", tradeId, e);
			return false;
		}
	}

	private TradeExtractModel findTradeExtract(Long tradeId) {

		TradeExtractStartProformaModel tradeExtractStartProformaModel = tradeExtractRepository.findById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
		TradeExtractModel result = new TradeExtractModel();
		BeanUtils.copyProperties(tradeExtractStartProformaModel, result);
		return result;

	}

	private BigDecimal percentOf(BigDecimal percent) {
		if (percent == null) {
			return BigDecimal.ZERO;
		}
		return percent.movePointLeft(2);
	}

	private BigDecimal roundUp(BigDecimal value) {
		return value.setScale(0, RoundingMode.CEILING);
	}
}