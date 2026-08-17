//package com.nicico.internal.sales.proforma.service.cash;
//
//import com.nicico.internal.sales.exception.InternalSaleCustomException;
//import com.nicico.internal.sales.goods.model.GoodsBucketModel;
//import com.nicico.internal.sales.goods.model.GoodsModel;
//import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
//import com.nicico.internal.sales.ime.trade.IMETradeModel;
//import com.nicico.internal.sales.ins.customer.model.CustomerModel;
//import com.nicico.internal.sales.proforma.dto.CashSaleCreateRequest;
//import com.nicico.internal.sales.proforma.dto.CashSaleDetailGenerator;
//import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
//import com.nicico.internal.sales.proforma.enums.*;
//import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
//import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
//import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
//import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
//import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
//import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
//import com.nicico.internal.sales.proforma.service.ProformaContractService;
//import com.nicico.internal.sales.proforma.service.ProformaSerialService;
//import com.nicico.internal.sales.proforma.service.ProformaValidationService;
//import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
//import com.nicico.internal.sales.trade.model.TradeExtractModel;
//import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
//import com.nicico.internal.sales.util.date.DateUtility;
//import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
//import com.nicico.internal.sales.wf.service.ProformaProcessService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static com.nicico.internal.sales.proforma.service.ProformaModelHelper.*;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CashSalePreciousProformaServiceImpl implements CashSalePreciousProformaService {
//
//	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
//	private static final String MSG_USER_NO_ACCESS = "شما دسترسی لازم برای این عملیات را ندارید";
//	private static final String MSG_NET_WEIGHT_INVALID = "مقدار وزن خالص خشک اشتباه است. وزن خالص خشک باید از مقدار نقدی بیشتر باشد";
//	private static final String MSG_ISSUING_PROFORMA_NOT_VALID = "اطلاعات پیش فاکتور معتبر نیست";
//	private static final String DEFAULT_PLACEHOLDER = "-";
//	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
//
//	private final ProformaMasterRepository proformaMasterRepository;
//	private final ProformaContractService proformaContractService;
//	private final ProformaSerialService proformaSerialService;
//	private final OfferTextProcess offerTextProcess;
//	private final ProformaProcessService cashSaleProcessService;
//	private final ProformaValidationService proformaValidationService;
//	private final TradeExtractRepository tradeExtractRepository;
//	private final ProformaDetailRepository proformaDetailRepository;
//	private final ProformaGoodItemRepository proformaGoodItemRepository;
//
//	// ==================== PUBLIC SERVICE METHODS ====================
//
//	@Override
//	@Transactional
//	public String create(CashSaleCreateRequest requestDto) {
//		log.debug("Creating cash sale precious proforma for tradeId: {}", requestDto.getTradeId());
//
//		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
//
//		// اعتبارسنجی
//		validateCreateRequest(requestDto);
//
//		// ایجاد پیش فاکتور
//		ProformaMasterModel model = createProformaMaster(requestDto, tradeExtract);
//
//		// شروع فرآیند
//		attachProcessToModel(model);
//
//		// ذخیره
//		proformaMasterRepository.saveAndFlush(model);
//		saveProformaDetails(model);
//
//		log.info("Cash sale precious proforma created successfully with contractNo: {}", model.getContractNo());
//		return model.getContractNo().toString();
//	}
//
//	@Override
//	@Transactional
//	public ProformaMasterModel createProformaMaster(CashSaleCreateRequest requestDto) {
//		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
//		return createProformaMaster(requestDto, tradeExtract);
//	}
//
//	@Override
//	public ProformaModelResponse getContractDetail(CashSaleCreateRequest requestDto) {
//		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
//		return getContractDetail(requestDto, tradeExtract);
//	}
//
//	// ==================== PRIVATE METHODS ====================
//
//	/**
//	 * ایجاد ProformaMaster
//	 */
//	private ProformaMasterModel createProformaMaster(CashSaleCreateRequest requestDto, TradeExtractModel tradeExtract) {
//		log.debug("Creating proforma master for cash sale precious");
//
//		ProformaModelResponse contractDetail = getContractDetail(requestDto, tradeExtract);
//		ProformaMasterModel ProformaMasterModel = contractDetail.getMasterModel();
//
//		// حذف تکراری‌ها و تنظیم لیست جزئیات
//		List<ProformaDetailModel> detailList = distinctDetails(contractDetail.getDetailModels());
//		ProformaMasterModel.setProformaDetailModelLists(detailList);
//
//		// تنظیم روابط
//		setupFullRelationships(ProformaMasterModel, detailList);
//
//		return proformaMasterRepository.saveAndFlush(ProformaMasterModel);
//	}
//
//	/**
//	 * دریافت جزئیات قرارداد
//	 */
//	private ProformaModelResponse getContractDetail(CashSaleCreateRequest requestDto, TradeExtractModel tradeExtract) {
//		log.debug("Getting contract detail for cash sale precious");
//
//		// اعتبارسنجی
//		proformaValidationService.validateProformaData(requestDto);
//
//		// دریافت اطلاعات مورد نیاز
//		int jalaliYear = DateUtility.getJalaliYear(requestDto.getOrderDate());
//		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());
//		GoodsModel goodsModel = proformaContractService.getGoodsModel(tradeModel.getPaymentCode());
//		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
//		GoodsBucketModel goodsBucketModel = proformaContractService.getGoodBucketModel(tradeExtract.getPaymentCode());
//		CustomerModel customerModel = proformaContractService.getCustomerModel(tradeExtract.getBuyerNationalCode());
//
//		// ایجاد پارامترها
//		CashSaleDetailGenerator params = createDetailGenerator(
//				requestDto, tradeModel, goodsModel, jalaliYear,
//				customerModel, goodsBucketModel, saleConditionModel, tradeExtract
//		);
//
//		// تولید جزئیات
//		List<ProformaDetailModel> detailDtoList = generatePerformaDetailList(params);
//
//		// محاسبه مجموع‌ها
//		Totals totals = calculateTotals(detailDtoList);
//
//		// ساخت Master
//		ProformaMasterModel masterModel = buildMasterModel(
//				tradeExtract, tradeModel, goodsModel, customerModel,
//				goodsBucketModel, requestDto, totals, params
//		);
//
//		// تنظیم روابط
//		setupFullRelationships(masterModel, detailDtoList);
//
//		log.info("Contract detail retrieved successfully");
//		return ProformaModelResponse.builder()
//				.masterModel(masterModel)
//				.detailModels(detailDtoList)
//				.build();
//	}
//
//	/**
//	 * ایجاد DetailGenerator
//	 */
//	private CashSaleDetailGenerator createDetailGenerator(
//			CashSaleCreateRequest requestDto,
//			IMETradeModel tradeModel,
//			GoodsModel goodsModel,
//			int jalaliYear,
//			CustomerModel customerModel,
//			GoodsBucketModel goodsBucketModel,
//			SaleConditionModel saleConditionModel,
//			TradeExtractModel tradeExtract) {
//
//		return new CashSaleDetailGenerator(
//				requestDto,
//				tradeModel,
//				proformaContractService.getVat(jalaliYear),
//				goodsModel,
//				jalaliYear,
//				customerModel,
//				goodsBucketModel,
//				saleConditionModel,
//				tradeExtract,
//				requestDto.isCashPercentTotal()
//		);
//	}
//
//	/**
//	 * ساخت MasterModel
//	 */
//	private ProformaMasterModel buildMasterModel(
//			TradeExtractModel tradeExtract,
//			IMETradeModel tradeModel,
//			GoodsModel goodsModel,
//			CustomerModel customerModel,
//			GoodsBucketModel goodsBucketModel,
//			CashSaleCreateRequest requestDto,
//			Totals totals,
//			CashSaleDetailGenerator params) {
//
//		return ProformaMasterModel.builder()
//				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
//				.paymentCode(tradeExtract.getPaymentCode())
//				.processId(DEFAULT_PLACEHOLDER)
//				.reversalProcessId(DEFAULT_PLACEHOLDER)
//				.cashPercentage(goodsBucketModel.getCashPercentage())
//				.commissionPercentage(0.0)
//				.deadlineDays(requestDto.getDeadlineDays())
//				.creditPercentage(HUNDRED.subtract(goodsBucketModel.getCashPercentage()))
//				.customerId(customerModel.getId())
//				.customerName(customerModel.getName())
//				.nationalCode(customerModel.getNationalCode())
//				.phone(customerModel.getPhone())
//				.economicCode(customerModel.getEconomicCode())
//				.registerNumber(customerModel.getRegisterNumber())
//				.postCode(customerModel.getPostCode())
//				.address(customerModel.getAddress())
//				.totalCashAmount(totals.totalCashAmount())
//				.totalQuantity(totals.totalQuantity())
//				.totalCreditAmount(totals.totalCreditAmount())
//				.totalVatAmount(totals.totalVatAmount())
//				.totalFinalAmount(totals.totalFinalAmount())
//				.workflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS)
//				.proformaIssueType(ProformaIssueType.FROM_CREDIT_FACILITIES)
//				.goodId(goodsModel.getId())
//				.goodName(goodsModel.getDescription())
//				.contractDate(params.tradeModel().getContractDate())
//				.tradeId(params.requestDto().getTradeId())
//				.isProcessFinal(false)
//				.isReversalProcessFinal(false)
//				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
//				.brokerName(tradeModel.getSellerBrokerPersianName())
//				.brokerNationalCode(DEFAULT_PLACEHOLDER)
//				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
//				.offerDescription(tradeModel.getOfferDescription())
//				.settlementType(SettlementType.CASH.name())
//				.build();
//	}
//
//	/**
//	 * تولید لیست Detail
//	 */
//	private List<ProformaDetailModel> generatePerformaDetailList(CashSaleDetailGenerator params) {
//		Integer jalaliYear = params.jalaliYear();
//		List<String> performaSerials = proformaSerialService.getProformaSerial(1);
//
//		ProformaGoodItemModel goodItem = generatePerformaGoodItem(params);
//		List<ProformaGoodItemModel> goodItems = List.of(goodItem);
//
//		// محاسبه مجموع‌های Detail
//		DetailTotals detailTotals = calculateDetailTotals(goodItems);
//
//		// ساخت DetailModel
//		ProformaDetailModel detailModel = ProformaDetailModel.builder()
//				.proformaGoodItemModels(goodItems)
//				.jalaaliYear(jalaliYear)
//				.storageDeadline(params.saleConditionModel().getStorageDeadline())
//				.storageCost(params.saleConditionModel().getStorageCost())
//				.creditExpirePeriod(params.saleConditionModel().getCreditExpirePeriod())
//				.shippingDeadline(params.saleConditionModel().getShippingDeadline())
//				.paymentDeferral(params.saleConditionModel().getPaymentDeferral())
//				.deadlineDays(params.requestDto().getDeadlineDays())
//				.performaNo(performaSerials.get(0))
//				.performaDate(new Date())
//				.totalAmount(detailTotals.totalAmount())
//				.finalPrice(detailTotals.finalAmount())
//				.vatAmount(detailTotals.vatAmount())
//				.saleType(SaleType.EXWORKS)
//				.settlementType(SettlementType.CASH.name())
//				.proformaIssueType(ProformaIssueType.FROM_CREDIT_FACILITIES)
//				.orderDate(params.requestDto().getOrderDate())
//				.contractDate(params.tradeModel().getContractDate())
//				.proformaReversalStatus(ProformaReversalStatus.NORMAL)
//				.extraBillOfPercent(BigDecimal.ZERO)
//				.extraBillOfExchangeAmount(BigDecimal.ZERO)
//				.build();
//
//		// تنظیم رابطه
//		goodItem.setProformaDetailModel(detailModel);
//
//		return List.of(detailModel);
//	}
//
//	/**
//	 * تولید GoodItem
//	 */
//	private ProformaGoodItemModel generatePerformaGoodItem(CashSaleDetailGenerator params) {
//		TradeExtractModel tradeExtract = params.tradeExtract();
//
//		double netWeight = params.requestDto().getNetWeight().doubleValue();
//		double totalWeight = params.requestDto().getTotalWeight().doubleValue();
//
//		// محاسبات مالی با Helper
//		PreciousGoodItemCalculation calc = calculatePreciousGoodItem(
//				params.tradeModel(),
//				params.goodsBucketModel(),
//				params.vat(),
//				netWeight,
//				totalWeight,
//				params.requestDto().isCashPercentTotal(),
//				params.good().getId(),
//				params.good().getDescription(),
//				null
//		);
//
//		// اعتبارسنجی وزن
//		validateNetWeight(calc);
//
//		// پردازش نام کالا
//		String finalGoodName = buildPreciousGoodName(params.good(), tradeExtract);
//		String lot = extractLotNumber(tradeExtract);
//
//		return buildPreciousGoodItem(calc, finalGoodName, lot);
//	}
//
//	/**
//	 * ساخت نام کالای گرانبها
//	 */
//	private String buildPreciousGoodName(GoodsModel good, TradeExtractModel tradeExtract) {
//		String cleanName = getCleanName(good);
//		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
//		String selenium = offerTextProcess.hasSelenium(rawDescription);
//		return cleanName + " " + selenium;
//	}
//
//	/**
//	 * استخراج شماره Lot
//	 */
//	private String extractLotNumber(TradeExtractModel tradeExtract) {
//		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
//		String lot = offerTextProcess.getBasketNumber(rawDescription);
//		return lot != null ? lot : DEFAULT_PLACEHOLDER;
//	}
//
//	/**
//	 * ساخت GoodItem از محاسبات
//	 */
//	private ProformaGoodItemModel buildPreciousGoodItem(
//			PreciousGoodItemCalculation calc,
//			String goodName,
//			String lot) {
//
//		return ProformaGoodItemModel.builder()
//				.goodId(calc.goodId())
//				.goodName(goodName)
//				.unitId(calc.unitId())
//				.lotNumber(lot)
//				.quantity(calc.quantity())
//				.creditQuantity(calc.creditQuantity())
//				.unitPriceCredit(calc.unitPriceCredit())
//				.unitPriceCash(calc.unitPriceCash())
//				.unitPrice(calc.unitPrice())
//				.creditAmount(calc.creditAmount())
//				.cashAmount(calc.cashAmount())
//				.vatCashAmount(calc.vatCashAmount())
//				.vatCreditAmount(calc.vatCreditAmount())
//				.vatAmount(calc.vatAmount())
//				.vatPercent(calc.vatPercent())
//				.interestPercent(calc.interestPercent())
//				.totalAmount(calc.totalAmount())
//				.finalAmount(calc.finalAmount())
//				.creditPercentage(calc.creditPercentage())
//				.netQuantity(calc.netQuantity())
//				.build();
//	}
//
//	/**
//	 * اعتبارسنجی وزن
//	 */
//	private void validateNetWeight(PreciousGoodItemCalculation calc) {
//		List<String> errors = new ArrayList<>();
//
//		if (calc.creditQuantity().compareTo(BigDecimal.ONE) < 0) {
//			errors.add("مقدار اعتباری باید حداقل 1 باشد");
//		}
//
//		if (calc.creditAmount().compareTo(BigDecimal.ONE) < 0) {
//			errors.add("مبلغ اعتباری باید حداقل 1 باشد");
//		}
//
//		if (calc.vatAmount().compareTo(BigDecimal.ONE) < 0) {
//			errors.add("مبلغ مالیات باید حداقل 1 باشد");
//		}
//
//		if (calc.cashQuantity().compareTo(calc.netQuantity()) > 0) {
//			errors.add("مقدار نقدی نمی‌تواند از وزن خالص بیشتر باشد");
//		}
//
//		if (!errors.isEmpty()) {
//			throw new InternalSaleCustomException.ValidationException(
//					MSG_NET_WEIGHT_INVALID + ": " + String.join(", ", errors)
//			);
//		}
//	}
//
//	/**
//	 * اعتبارسنجی درخواست
//	 */
//	private void validateCreateRequest(CashSaleCreateRequest requestDto) {
//		if (!cashSaleProcessService.canStartProcess()) {
//			throw new InternalSaleCustomException.ValidationException(MSG_USER_NO_ACCESS);
//		}
//
//		if (!proformaValidationService.validateCashSaleData(requestDto).isEmpty()) {
//			throw new InternalSaleCustomException.ValidationException(MSG_ISSUING_PROFORMA_NOT_VALID);
//		}
//		proformaValidationService.validateProformaData(requestDto);
//	}
//
//	/**
//	 * شروع فرآیند
//	 */
//	private void attachProcessToModel(ProformaMasterModel model) {
//		ProformaVariablesInput input = buildProformaVariablesInput(model);
//		var process = cashSaleProcessService.startProformaProcess(input);
//		model.setProcessId(process.getId());
//		model.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
//		model.setIsProcessFinal(false);
//		model.setIsReversalProcessFinal(false);
//	}
//
//	/**
//	 * ساخت ورودی فرآیند
//	 */
//	private ProformaVariablesInput buildProformaVariablesInput(ProformaMasterModel model) {
//		return ProformaVariablesInput.builder()
//				.contractDate(model.getProformaDetailModelLists().get(0).getContractDate())
//				.proformaMasterId(model.getId())
//				.goodId(model.getGoodId())
//				.contractNo(String.valueOf(model.getContractNo()))
//				.customerName(model.getCustomerName())
//				.goodName(model.getGoodName())
//				.commission(model.getCommissionPercentage())
//				.build();
//	}
//
//	/**
//	 * ذخیره جزئیات
//	 */
//	private void saveProformaDetails(ProformaMasterModel model) {
//		model.getProformaDetailModelLists().forEach(detail -> {
//			detail.setProformaMasterId(model.getId());
//			proformaDetailRepository.saveAndFlush(detail);
//			detail.getProformaGoodItemModels().forEach(item -> {
//				item.setProformaDetailId(detail.getId());
//				proformaGoodItemRepository.saveAndFlush(item);
//			});
//		});
//	}
//
//	// ==================== REPOSITORY FINDER METHODS ====================
//
//	private TradeExtractModel findTradeExtract(Long tradeId) {
//		return tradeExtractRepository.findById(tradeId)
//				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
//	}
//}