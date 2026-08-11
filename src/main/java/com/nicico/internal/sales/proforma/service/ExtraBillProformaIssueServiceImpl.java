package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.service.GoodBucketService;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.proforma.dto.PerfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.PerformaDetailGenerator;
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
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.service.ProformaProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nicico.internal.sales.proforma.service.ProformaModelHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtraBillProformaIssueServiceImpl implements ExtraBillProformaIssueService {
	private static final String ERR_PROFORMA_ACCESS_DENIED = "شما اجازه شروع فرایند صدور پیش فاکتور را ندارید";
	private static final BigDecimal PERCENT_DIVISOR = BigDecimal.valueOf(100);
	private static final String DEFAULT_PLACEHOLDER = "-";

	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final ProformaProcessService proformaProcessService;
	private final ProformaValidationService proformaValidationService;
	private final OfferTextProcess offerTextProcess;
	private final ProformaContractService proformaContractService;
	private final ProformaSerialService proformaSerialService;
	private final GoodBucketService goodBucketService;


	@Override
	@Transactional
	public String create(PerfomaCreateRequest requestDto) {
		log.debug("Creating extra bill proforma for tradeId: {}", requestDto.getTradeId());

		if (!proformaProcessService.canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ERR_PROFORMA_ACCESS_DENIED);
		}

		proformaValidationService.validateProformaData(requestDto);
		proformaValidationService.validateDate(requestDto);

		ProformaMasterModel model = createProformaMaster(requestDto);
		startWorkflowProcess(model);
		proformaMasterRepository.saveAndFlush(model);

		log.info("Extra bill proforma created successfully with contractNo: {}", model.getContractNo());
		return model.getContractNo().toString();
	}

	// ==================== PROFORMA MASTER CREATION ====================

	@Transactional
	public ProformaMasterModel createProformaMaster(PerfomaCreateRequest requestDto) {
		log.debug("Creating extra bill proforma master for tradeId: {}", requestDto.getTradeId());

		requestDto.setProformaIssueType(requestDto.getProformaIssueType());
		ProformaModelResponse contractDetail = getContractDetail(requestDto);
		ProformaMasterModel masterModel = contractDetail.getMasterModel();
		masterModel.setContractNo(requestDto.getContractNo());


		// استفاده از Helper برای تنظیم روابط
		setupFullRelationships(masterModel, contractDetail.getDetailModels());

		proformaMasterRepository.save(masterModel);

		Long masterId = masterModel.getId();
		List<ProformaDetailModel> detailModels = distinctDetails(contractDetail.getDetailModels());

		// ذخیره Detail و GoodItem‌ها
		saveDetailAndGoodItems(detailModels, masterId);

		masterModel.setProformaDetailModelLists(detailModels);
		return masterModel;
	}

	// ==================== CONTRACT DETAIL ====================

	private ProformaModelResponse getContractDetail(PerfomaCreateRequest requestDto) {
		log.debug("Getting contract detail for tradeId: {}", requestDto.getTradeId());

		var tradeExtract = proformaContractService.getTradeModel(requestDto.getTradeId());
		int jalaliYear = DateUtility.getJalaliYear(requestDto.getOrderDate());

		// استفاده از ProformaContractService برای دریافت اطلاعات
		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());
		GoodsModel goodsModel = proformaContractService.getGoodsModel(tradeExtract.getPaymentCode());
		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
		GoodsBucketModel goodsBucketModel = proformaContractService.getGoodBucketModel(tradeExtract.getPaymentCode());
		CustomerModel customerModel = proformaContractService.getCustomerModel(tradeExtract.getBuyerNationalCode());

		PerformaDetailGenerator params = new PerformaDetailGenerator(
				requestDto,
				tradeModel,
				proformaContractService.getVat(jalaliYear),
				goodsModel,
				jalaliYear,
				goodsBucketModel,
				saleConditionModel
		);

		List<ProformaDetailModel> detailDtoList = generatePerformaDetailList(params, saleConditionModel);

		// استفاده از Helper برای محاسبه مجموع‌ها
		Totals totals = calculateTotals(detailDtoList);

		// ساخت MasterModel با Helper
		ProformaMasterModel masterModel = buildMasterModel(
				tradeModel,
				goodsModel,
				customerModel,
				goodsBucketModel,
				requestDto,
				totals,
				params
		);

		// استفاده از Helper برای تنظیم روابط
		setMasterForDetails(detailDtoList, masterModel);

		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	// ==================== MASTER MODEL BUILDER ====================

	private ProformaMasterModel buildMasterModel(

			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			CustomerModel customerModel,
			GoodsBucketModel goodsBucketModel,
			PerfomaCreateRequest requestDto,
			Totals totals,
			PerformaDetailGenerator params) {

		// استفاده از Helper برای محاسبه درصدها
		CashCreditPercentages percentages = calculateCashCreditPercentages(goodsBucketModel, false);

		return ProformaMasterModel.builder()
				.contractNo(Long.valueOf(tradeModel.getContractNo()))
				.paymentCode(tradeModel.getPaymentCode())
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
				.contractDate(params.tradeModel().getContractDate())
				.tradeId(params.requestDto().getTradeId())
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.settlementType(SettlementType.UNKNOWN.name())
				.contractNo(requestDto.getContractNo())
				.build();
	}

	// ==================== DETAIL GENERATION ====================

	private List<ProformaDetailModel> generatePerformaDetailList(
			PerformaDetailGenerator params,
			SaleConditionModel saleConditionModel) {

		PerfomaCreateRequest requestDto = params.requestDto();
		Integer jalaliYear = params.jalaliYear();
		List<String> serial = proformaSerialService.getProformaSerial(requestDto.getParts().size());

		List<ProformaDetailModel> detailDtoList = new ArrayList<>();

		for (int i = 0; i < requestDto.getParts().size(); i++) {
			List<ProformaGoodItemModel> goodItem = generatePerformaGoodItemList(params, i);

			// استفاده از Helper برای محاسبه مجموع‌های Detail
			DetailTotals detailTotals = calculateDetailTotals(goodItem);
			BigDecimal extraBillOfPercent = params.saleConditionModel().getExtraBillOfExchangePercent();
			BigDecimal extraAmonut = BigDecimal.ZERO;

			if (params.requestDto().getProformaIssueType()== ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
				BigDecimal percent = BigDecimal.valueOf(extraBillOfPercent.longValue());
				extraAmonut = detailTotals.finalAmount().multiply(
						BigDecimal.ONE.add(percent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
				);
			}




			// استفاده از Helper برای ساخت DetailModel
			ProformaDetailModel detailModel = buildProformaDetailModel(
					goodItem,
					jalaliYear,
					saleConditionModel,
					requestDto.getDeadlineDays(),
					serial.get(i),
					new Date(),
					detailTotals,
					SettlementType.UNKNOWN.name(),
					requestDto.getProformaIssueType(),
					requestDto.getOrderDate(),
					params.tradeModel().getContractDate(),
					ProformaReversalStatus.NORMAL,
					extraBillOfPercent,
					extraAmonut
			);

			// محاسبه مبلغ اضافی
			calculateAndSetExtraAmount(detailModel, requestDto.getProformaIssueType(),
					detailTotals.totalAmount(), saleConditionModel);



			// تنظیم GamCertificateCount

			if (params.requestDto().getProformaIssueType()== ProformaIssueType.GAM_BONDS) {

				calculateGamCertificateCount(extraBillOfPercent);
			}


			// تنظیم رابطه
			goodItem.forEach(item -> item.setProformaDetailModel(detailModel));
			detailDtoList.add(detailModel);
		}

		return new ArrayList<>(detailDtoList).stream().toList();
	}

	// ==================== GOOD ITEM GENERATION ====================

	private List<ProformaGoodItemModel> generatePerformaGoodItemList(PerformaDetailGenerator params, int rank) {
		var tradeExtract = proformaContractService.getTradeModel(params.requestDto().getTradeId());
		String description = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String lot = offerTextProcess.extractLotNumber(description);

		// استفاده از Helper برای پردازش نام
		String cleanName = processGoodName(params.good(), description);

		long quantity = params.requestDto().getParts().get(rank).longValue();

		// استفاده از Helper برای محاسبات مالی
		CashGoodItemCalculation calc = calculateCashGoodItem(
				params.tradeModel(),
				params.goodsBucketModel(),
				params.vat(),
				(double) quantity,
				false,
				params.good().getId(),
				cleanName,
				lot
		);

		// استفاده از Helper برای ساخت GoodItem
		return List.of(buildGoodItemFromCalculation(calc));
	}

	/**
	 * ساخت GoodItem از محاسبات
	 */
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

	// ==================== EXTRA AMOUNT CALCULATION ====================

	/**
	 * محاسبه و تنظیم مبلغ اضافی بر اساس نوع پیش فاکتور
	 */
	private void calculateAndSetExtraAmount(
			ProformaDetailModel detailModel,
			ProformaIssueType issueType,
			BigDecimal totalPrice,
			SaleConditionModel saleConditionModel) {

		BigDecimal extraPercent = resolveExtraPercent(issueType, saleConditionModel);
		BigDecimal extraAmount = calculateExtraAmount(totalPrice, extraPercent);
		BigDecimal finalPrice = totalPrice.add(extraAmount);

		detailModel.setExtraBillOfExchangeAmount(extraAmount);
		detailModel.setExtraBillOfPercent(extraPercent);
		detailModel.setFinalPrice(finalPrice);

		if (issueType == ProformaIssueType.GAM_BONDS) {
			detailModel.setGamCertificateCount(calculateGamCertificateCount(finalPrice));
		}
	}

	/**
	 * تعیین درصد اضافی بر اساس نوع صدور
	 */
	private BigDecimal resolveExtraPercent(ProformaIssueType issueType, SaleConditionModel saleConditionModel) {
		return switch (issueType) {
			case GAM_BONDS -> nullSafe(saleConditionModel.getExtraGamCertificatePercent());
			case EXTRA_BILL_OF_EXCHANGE -> nullSafe(saleConditionModel.getExtraBillOfExchangePercent());
			default -> BigDecimal.ZERO;
		};
	}

	/**
	 * محاسبه مبلغ اضافی از روی درصد
	 */
	private BigDecimal calculateExtraAmount(BigDecimal totalPrice, BigDecimal extraPercent) {
		if (extraPercent.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
		return totalPrice.multiply(extraPercent).divide(PERCENT_DIVISOR, 0, RoundingMode.UP);
	}

	/**
	 * محاسبه تعداد اوراق گام (با گرد کردن به بالا)
	 */
	private Integer calculateGamCertificateCount(BigDecimal finalAmount) {
		return finalAmount.divide(BigDecimal.valueOf(1_000_000), 0, RoundingMode.CEILING).intValue();
	}

	private BigDecimal nullSafe(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	// ==================== WORKFLOW ====================

	private void startWorkflowProcess(ProformaMasterModel model) {
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

	// ==================== SAVE HELPERS ====================

	/**
	 * ذخیره Detail و GoodItem‌ها
	 * این متد در چند کلاس مشابه است و باید به Helper منتقل شود
	 */
	private void saveDetailAndGoodItems(List<ProformaDetailModel> detailModels, Long masterId) {
		detailModels.forEach(detailModel -> {
			detailModel.setProformaMasterId(masterId);
			proformaDetailRepository.save(detailModel);

			detailModel.getProformaGoodItemModels().stream()
					.distinct()
					.forEach(goodItem -> {
						goodItem.setProformaDetailId(detailModel.getId());
						proformaGoodItemRepository.save(goodItem);
					});
		});
	}


	// ==================== PUBLIC SERVICE METHODS ====================

	public GoodsBucketModel getGoodBucketModel(String paymentCode) {
		return goodBucketService.findByPaymentCodeModel(paymentCode);
	}
}