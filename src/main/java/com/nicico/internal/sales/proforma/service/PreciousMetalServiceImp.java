package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.proforma.dto.PreciousMetalDetailGenerator;
import com.nicico.internal.sales.proforma.dto.PreciousMetalProfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import static com.nicico.internal.sales.proforma.service.ProformaModelHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreciousMetalServiceImp implements PreciousMetalService {

	private static final String MSG_PROCESS_ACCESS_DENIED = "شما اجازه شروع فرایند صدور پیش فاکتور را ندارید";
	private static final String MSG_TRADE_NOT_FOUND_DETAIL = "آگهی عرضه وجود ندارد";
	private static final String MSG_GOOD_NOT_FOUND = "کالا با کد کالا یافت نشد: ";
	private static final String SETTLEMENT_TYPE_DEFAULT = SettlementType.UNKNOWN.name();
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaContractService proformaContractService;
	private final ProformaSerialService proformaSerialService;
	private final OfferTextProcess offerTextProcess;
	private final ProformaProcessService proformaProcessService;
	private final ProformaValidationService proformaValidationService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final GoodsRepository goodsRepository;
	private final TradeExtractRepository tradeExtractRepository;
	private final PreciousMetalRepository preciousMetalRepository;
	private final PreciousMetalExtraBillService preciousMetalExtraBillService;

	@Override
	@Transactional
	public String create(PreciousMetalProfomaCreateRequest requestDto) {
		log.debug("Creating precious metal proforma for tradeId: {}", requestDto.getTradeId());
		// Check if EXTRA_BILL_OF_EXCHANGE type, delegate to extra bill service
		if (requestDto.getProformaIssueType() == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
			log.debug("Delegating to PreciousMetalExtraBillService for EXTRA_BILL_OF_EXCHANGE");
			return preciousMetalExtraBillService.create(requestDto);
		}


		// اعتبارسنجی دسترسی
		if (!proformaProcessService.canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(MSG_PROCESS_ACCESS_DENIED);
		}

		// اعتبارسنجی داده ها
		proformaValidationService.validateProformaData(requestDto);

		// ایجاد پیش فاکتور
		ProformaMasterModel model = createProformaMaster(requestDto);

		// شروع فرآیند
		startWorkflowProcess(model);

		// ذخیره نهایی
		proformaMasterRepository.saveAndFlush(model);

		log.info("Precious metal proforma created successfully with contractNo: {}", model.getContractNo());
		return model.getContractNo().toString();
	}

	@Transactional
	@Override
	public ProformaMasterModel createProformaMaster(PreciousMetalProfomaCreateRequest requestDto) {
		log.debug("Creating proforma master for precious metal");

		// دریافت جزئیات قرارداد
		ProformaModelResponse contractDetail = getContractDetail(requestDto);
		ProformaMasterModel masterModel = contractDetail.getMasterModel();

		// حذف تکراری ها و تنظیم لیست جزئیات
		List<ProformaDetailModel> detailList = distinctDetails(contractDetail.getDetailModels());
		masterModel.setProformaDetailModelLists(detailList);

		// ذخیره Master
		masterModel = proformaMasterRepository.save(masterModel);


		// ذخیره جزئیات و GoodItem ها
		ProformaModelHelper.saveDetailAndGoodItems(detailList, masterModel.getId(), proformaDetailRepository, proformaGoodItemRepository);

		log.info("Proforma master created successfully with id: {}", masterModel.getId());
		return masterModel;
	}

	@Override
	public ProformaModelResponse getContractDetail(PreciousMetalProfomaCreateRequest requestDto) {
		log.debug("Getting contract detail for precious metal, tradeId: {}", requestDto.getTradeId());

		// یافتن اطلاعات
		TradeExtractModel tradeExtract = findTradeExtract(requestDto.getTradeId());
		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());

		// اعتبارسنجی
		proformaValidationService.validateProformaData(requestDto);

		// دریافت اطلاعات مورد نیاز
		int jalaliYear = DateUtility.getJalaliYear(requestDto.getOrderDate());
		GoodsModel goodsModel = proformaContractService.findGoodsModelByCommodityCode(Long.valueOf(tradeModel.getCommodityCode()));
		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
		GoodsBucketModel goodsBucketModel = proformaContractService.getGoodBucketModel(tradeExtract.getPaymentCode());
		CustomerModel customerModel = proformaContractService.getCustomerModel(tradeExtract.getBuyerNationalCode());

		// ایجاد پارامترها
		PreciousMetalDetailGenerator params = createDetailGenerator(
				requestDto, tradeModel, goodsModel, jalaliYear,
				goodsBucketModel, saleConditionModel
		);

		// تولید جزئیات
		List<ProformaDetailModel> detailDtoList = generatePerformaDetailList(params);

		// محاسبه مجموع ها
		Totals totals = calculateTotals(detailDtoList);

		// ساخت Master
		ProformaMasterModel masterModel = buildMasterModel(
				tradeExtract, tradeModel, goodsModel, customerModel,
				goodsBucketModel, requestDto, totals, params
		);

		// تنظیم روابط
		setupFullRelationships(masterModel, detailDtoList);

		log.info("Contract detail retrieved successfully");
		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	@Override
	public boolean isPreciousMetalByPaymentCode(String paymentCode) {
		try {
			GoodsModel good = proformaContractService.getGoodsModel(paymentCode);
			return preciousMetalRepository.existsById(good.getId());
		} catch (Exception e) {
			log.warn("Error checking precious metal by paymentCode: {}", paymentCode, e);
			return false;
		}
	}


	/**
	 * شروع فرآیند برای پیش فاکتور
	 */
	private void startWorkflowProcess(ProformaMasterModel model) {
		ProformaVariablesInput input = ProformaVariablesInput.builder()
				.contractDate(model.getProformaDetailModelLists().get(0).getContractDate())
				.proformaMasterId(model.getId())
				.goodId(model.getGoodId())
				.contractNo(String.valueOf(model.getContractNo()))
				.customerName(model.getCustomerName())
				.goodName(model.getGoodName())
				.build();

		var process = proformaProcessService.startProformaProcess(input);
		model.setProcessId(process.getId());
		model.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		model.setIsProcessFinal(false);
		model.setIsReversalProcessFinal(false);
	}

	/**
	 * ذخیره Detail و GoodItem ها
	 */
//	private void saveDetailAndGoodItems(List<ProformaDetailModel> detailModels, Long masterId) {
//		detailModels.forEach(detail -> {
//			detail.setProformaMasterId(masterId);
//			proformaDetailRepository.save(detail);
//
//			detail.getProformaGoodItemModels().forEach(goodItem -> {
//				goodItem.setProformaDetailId(detail.getId());
//				proformaGoodItemRepository.save(goodItem);
//			});
//		});
//	}

	/**
	 * ایجاد DetailGenerator
	 */
	private PreciousMetalDetailGenerator createDetailGenerator(
			PreciousMetalProfomaCreateRequest requestDto,
			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			int jalaliYear,
			GoodsBucketModel goodsBucketModel,
			SaleConditionModel saleConditionModel) {

		return new PreciousMetalDetailGenerator(
				requestDto,
				tradeModel,
				proformaContractService.getVat(jalaliYear),
				goodsModel,
				jalaliYear,
				proformaContractService.getCustomerModel(
						tradeModel.getBuyerNationalCode()
				),
				goodsBucketModel,
				saleConditionModel
		);
	}

	/**
	 * ساخت MasterModel
	 */
	private ProformaMasterModel buildMasterModel(
			TradeExtractModel tradeExtract,
			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			CustomerModel customerModel,
			GoodsBucketModel goodsBucketModel,
			PreciousMetalProfomaCreateRequest requestDto,
			Totals totals,
			PreciousMetalDetailGenerator params) {

		return ProformaMasterModel.builder()
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.cashPercentage(goodsBucketModel.getCashPercentage())
				.commissionPercentage(goodsBucketModel.getCommission())
				.deadlineDays(requestDto.getDeadlineDays())
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
				.proformaIssueType(requestDto.getProformaIssueType())
				.goodId(goodsModel.getId())
				.goodName(goodsModel.getDescription())
				.contractDate(params.tradeModel().getContractDate())
				.tradeId(requestDto.getTradeId())
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.settlementType(SETTLEMENT_TYPE_DEFAULT)
				.isProcessFinal(false)
				.isReversalProcessFinal(false)
				.build();
	}

	/**
	 * تولید لیست Detail
	 */
	private List<ProformaDetailModel> generatePerformaDetailList(PreciousMetalDetailGenerator params) {
		List<String> serial = proformaSerialService.getProformaSerial(1);

		// 1. تولید آیتم های کالا
		List<ProformaGoodItemModel> goodItems = generatePerformaGoodItemList(params);

		// 2. محاسبه مجموع های Detail
		DetailTotals detailTotals = calculateDetailTotals(goodItems);

		// 3. ساخت DetailModel
		ProformaDetailModel detailModel = buildProformaDetailModel(
				goodItems,
				params.jalaliYear(),
				params.saleConditionModel(),
				params.requestDto().getDeadlineDays(),
				serial.get(0),
				new Date(),
				detailTotals,
				SETTLEMENT_TYPE_DEFAULT,
				params.requestDto().getProformaIssueType(),
				params.requestDto().getOrderDate(),
				params.tradeModel().getContractDate(),
				ProformaReversalStatus.NORMAL,
				params.saleConditionModel().getExtraBillOfExchangePercent(),
				BigDecimal.ZERO // مقدار موقت، بعداً محاسبه می شود
		);

		// 4. محاسبه مبلغ اضافی و مبلغ نهایی
		calculateAndSetExtraAmount(
				detailModel,
				params.requestDto().getProformaIssueType(),
				detailTotals.totalAmount(),
				params.saleConditionModel()
		);

		// 5. تنظیم رابطه بین GoodItem و Detail
		goodItems.forEach(item -> item.setProformaDetailModel(detailModel));

		return List.of(detailModel);
	}

	/**
	 * تولید GoodItem
	 */
	private List<ProformaGoodItemModel> generatePerformaGoodItemList(PreciousMetalDetailGenerator params) {
		ProformaGoodItemModel goodItem = generatePerformaGoodItem(params);
		return List.of(goodItem);
	}

	/**
	 * تولید GoodItem تکی
	 */
	private ProformaGoodItemModel generatePerformaGoodItem(PreciousMetalDetailGenerator params) {
		TradeExtractModel tradeExtract = findTradeExtract(params.requestDto().getTradeId());

		// استخراج اطلاعات از توضیحات
		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String selenium = extractSelenium(rawDescription, offerTextProcess);
		String lot = extractLotNumber(rawDescription, offerTextProcess);
		String cleanName = getCleanName(params.good());
		String finalGoodName = cleanName + " " + selenium;

		// محاسبات مالی
		PreciousMetalLCCalculation calc = calculatePreciousMetalLCGoodItem(
				params.tradeModel(),
				params.goodsBucketModel(),
				params.vat(),
				params.requestDto().getTotalWeight().doubleValue(),
				params.requestDto().getNetWeight().doubleValue(),
				params.good().getId(),
				finalGoodName,
				lot
		);

		return buildPreciousMetalGoodItem(calc);
	}

	/**
	 * ساخت GoodItem از محاسبات
	 */
	private ProformaGoodItemModel buildPreciousMetalGoodItem(PreciousMetalLCCalculation calc) {
		return ProformaGoodItemModel.builder()
				.goodId(calc.goodId())
				.goodName(calc.goodName())
				.unitId(calc.unitId())
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
				.lotNumber(calc.lotNumber())
				.netQuantity(calc.netQuantity())
				.build();
	}

	// ==================== REPOSITORY FINDER METHODS ====================

	/**
	 * یافتن TradeExtract
	 */
	private TradeExtractModel findTradeExtract(Long tradeId) {
		return tradeExtractRepository.findById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND_DETAIL));
	}


	/**
	 * محاسبه و تنظیم مبلغ اضافی بر اساس نوع پیش فاکتور
	 */
	private void calculateAndSetExtraAmount(
			ProformaDetailModel detailModel,
			ProformaIssueType issueType,
			BigDecimal totalPrice,
			SaleConditionModel saleConditionModel) {

		// دریافت درصد اضافی
		BigDecimal extraPercent = BigDecimal.ZERO;
		if (issueType == ProformaIssueType.GAM_BONDS) {
			extraPercent = saleConditionModel.getExtraGamCertificatePercent() != null ?
					saleConditionModel.getExtraGamCertificatePercent() : BigDecimal.ZERO;
		} else if (issueType == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
			extraPercent = saleConditionModel.getExtraBillOfExchangePercent() != null ?
					saleConditionModel.getExtraBillOfExchangePercent() : BigDecimal.ZERO;
		}

		// محاسبه مبلغ نهایی با همان فرمول ExportDocService: totalPrice * (1 + percent/100)
		BigDecimal factor = BigDecimal.ONE.add(
				extraPercent.divide(HUNDRED, 10, RoundingMode.HALF_UP)
		);
		BigDecimal finalPrice = totalPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
		BigDecimal extraAmount = finalPrice.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);

		// تنظیم مقادیر
		// برای EXTRA_BILL مقدار فیلد amount باید «مبلغ نهایی با اضافه درصد» باشد.
		detailModel.setExtraBillOfExchangeAmount(
				issueType == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE ? finalPrice : extraAmount
		);
		detailModel.setExtraBillOfPercent(extraPercent);
		detailModel.setFinalPrice(finalPrice);

		// محاسبه تعداد اوراق گام (فقط برای نوع GAM_BONDS)
		if (issueType == ProformaIssueType.GAM_BONDS) {
			int gamCount = finalPrice.divide(BigDecimal.valueOf(1_000_000), 0, RoundingMode.CEILING).intValue();
			detailModel.setGamCertificateCount(gamCount);
		}
	}
}