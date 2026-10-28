package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.customer.model.CustomerModel;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.proforma.dto.*;
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
import com.nicico.internal.sales.wf.detection.ExtraBillStepDetector;
import com.nicico.internal.sales.wf.service.ProcessVariableProvider;
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
	private static final String ERR_CONTRACT_NO_REQUIRED = "شماره قرارداد الزامی است";
	private static final String ERR_PROFORMA_NOT_FOUND = "پیش فاکتور برای شماره قرارداد %s یافت نشد";
	private static final String ERR_PROFORMA_DETAIL_NOT_FOUND = "جزییات پیش فاکتور برای شماره قرارداد %s یافت نشد";
	private static final String ERR_TRADE_INFO_NOT_FOUND = "اطلاعات معامله برای قرارداد %s یافت نشد";
	private static final String ERR_EXTRA_BILL_PERCENT_INVALID = "درصد اضافه برات نامعتبر است";

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final String DEFAULT_PLACEHOLDER = "-";

	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final ProformaProcessService proformaProcessService;
	private final ProformaValidationService proformaValidationService;
	private final OfferTextProcess offerTextProcess;
	private final ProformaContractService proformaContractService;
	private final ProformaSerialService proformaSerialService;
	private final ExtraBillStepDetector extraBillStepDetector;
	private final TradeExtractRepository tradeExtractRepository;

	private final ProcessVariableProvider processVariableProvider;

	@Override
	@Transactional
	public String create(PerfomaCreateRequest requestDto) {
		log.debug("Creating extra bill proforma for tradeId: {}", requestDto.getTradeId());
		extraBillStepDetector.updateAllAcknowledgments();

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

	@Override
	@Transactional
	public ProformaResponseDto getDetailByContractNo(Long contractNo) {
		log.debug("Getting extra bill proforma detail and updating extra bill percent by contractNo: {}", contractNo);
		return updateProformaExtraBillPercent(contractNo);
	}

	@Transactional
	public ProformaMasterModel createProformaMaster(PerfomaCreateRequest requestDto) {
		log.debug("Creating extra bill proforma master for tradeId: {}", requestDto.getTradeId());

		ProformaModelResponse contractDetail = getContractDetail(requestDto);
		ProformaMasterModel masterModel = contractDetail.getMasterModel();
		masterModel.setContractNo(requestDto.getContractNo());
		setupFullRelationships(masterModel, contractDetail.getDetailModels());
		proformaMasterRepository.save(masterModel);
		Long masterId = masterModel.getId();
		List<ProformaDetailModel> detailModels = distinctDetails(contractDetail.getDetailModels());
		saveDetailAndGoodItems(detailModels, masterId);
		masterModel.setProformaDetailModelLists(detailModels);
		return masterModel;
	}

	private SaleConditionModel getSalesCondition(String paymentCode) {
		return proformaContractService.getSaleConditionModel(paymentCode);
	}

	private ProformaModelResponse getContractDetail(PerfomaCreateRequest requestDto) {
		log.debug("Getting contract detail for tradeId: {}", requestDto.getTradeId());

		var tradeExtract = proformaContractService.getTradeModel(requestDto.getTradeId());
		int jalaliYear = DateUtility.getJalaliYear(requestDto.getOrderDate());
		SaleConditionModel saleConditionModel = getSalesCondition(tradeExtract.getPaymentCode());
		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());
		GoodsModel goodsModel = proformaContractService.getGoodsModel(tradeExtract.getPaymentCode());

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
		Totals totals = calculateTotals(detailDtoList);
		ProformaMasterModel masterModel = buildMasterModel(
				tradeModel,
				goodsModel,
				customerModel,
				goodsBucketModel,
				requestDto,
				totals,
				params
		);

		setMasterForDetails(detailDtoList, masterModel);

		return ProformaModelResponse.builder()
				.masterModel(masterModel)
				.detailModels(detailDtoList)
				.build();
	}

	private ProformaMasterModel buildMasterModel(

			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			CustomerModel customerModel,
			GoodsBucketModel goodsBucketModel,
			PerfomaCreateRequest requestDto,
			Totals totals,
			PerformaDetailGenerator params) {

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
				.depositSlipWithTax(BigDecimal.ZERO)
				.depositSlipWithoutTax(BigDecimal.ZERO)
				.depositSlipType(null)
				.build();
	}

	private List<ProformaDetailModel> generatePerformaDetailList(
			PerformaDetailGenerator params,
			SaleConditionModel saleConditionModel) {

		PerfomaCreateRequest requestDto = params.requestDto();
		Integer jalaliYear = params.jalaliYear();
		List<String> serial = proformaSerialService.getProformaSerial(requestDto.getParts().size());

		List<ProformaDetailModel> detailDtoList = new ArrayList<>();

		for (int i = 0; i < requestDto.getParts().size(); i++) {
			List<ProformaGoodItemModel> goodItem = generateProformaGoodItemList(params, i);

			DetailTotals detailTotals = calculateDetailTotals(goodItem);

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
					saleConditionModel.getExtraBillOfExchangePercent(),
					BigDecimal.ZERO
			);

			calculateAndSetExtraAmount(detailModel, detailTotals.totalAmount(), saleConditionModel.getExtraBillOfExchangePercent());
			goodItem.forEach(item -> item.setProformaDetailModel(detailModel));
			detailDtoList.add(detailModel);
		}

		return detailDtoList.stream().toList();
	}

	private List<ProformaGoodItemModel> generateProformaGoodItemList(PerformaDetailGenerator params, int rank) {
		var tradeExtract = proformaContractService.getTradeModel(params.requestDto().getTradeId());
		String description = offerTextProcess.findDescriptionByPaymentCode(tradeExtract.getPaymentCode());
		String lot = offerTextProcess.extractLotNumber(description);

		String cleanName = processGoodName(params.good(), description);

		long quantity = params.requestDto().getParts().get(rank).longValue();

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

		return List.of(buildGoodItemFromCalculation(calc));
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


	private ExtraBillCalculationContext calculateExtraBillContext(BigDecimal totalPrice, BigDecimal extraPercent) {
		if (totalPrice == null) {
			totalPrice = BigDecimal.ZERO;
		}
		if (extraPercent == null) {
			extraPercent = BigDecimal.ZERO;
		}
		BigDecimal extraPercentRatio = extraPercent.divide(HUNDRED, 10, RoundingMode.HALF_UP);
		BigDecimal finalPrice = totalPrice.multiply(BigDecimal.ONE.add(extraPercentRatio)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal extraAmount = finalPrice.multiply(extraPercentRatio).setScale(2, RoundingMode.HALF_UP);

		return ExtraBillCalculationContext.builder()
				.totalPrice(totalPrice)
				.extraBillPercent(extraPercent)
				.extraPercentRatio(extraPercentRatio)
				.extraBillOfExchangeAmount(extraAmount)
				.finalPrice(finalPrice)
				.build();
	}


	private ExtraBillCalculationContext calculateAndSetExtraAmount(ProformaDetailModel detailModel, BigDecimal totalPrice, BigDecimal extraPercent) {
		ExtraBillCalculationContext context = calculateExtraBillContext(totalPrice, extraPercent);
		detailModel.setExtraBillOfExchangeAmount(context.getExtraBillOfExchangeAmount());
		detailModel.setExtraBillOfPercent(context.getExtraBillPercent());
		detailModel.setFinalPrice(context.getFinalPrice());
		detailModel.setGamCertificateCount(0L);
		detailModel.setProformaIssueType(ProformaIssueType.EXTRA_BILL_OF_EXCHANGE);
		return context;
	}


	@Override
	@Transactional
	public ProformaResponseDto updateProformaExtraBillPercent(Long contractNo) {
		log.debug("Updating proforma extra bill percent for contractNo: {}", contractNo);
		if (contractNo == null) {
			throw new InternalSaleCustomException.ValidationException(ERR_CONTRACT_NO_REQUIRED);
		}
		ProformaMasterModel masterModel = proformaMasterRepository.findFirstByContractNoOrderByIdDesc(contractNo)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND.formatted(contractNo)));


		TradeExtractModel tradeExtract = tradeExtractRepository.findFirstByPaymentCodeOrderByIdDesc(masterModel.getPaymentCode())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND.formatted(contractNo)));


		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
		return updateProformaExtraBillPercent(contractNo, saleConditionModel.getExtraBillOfExchangePercent());
	}




	@Transactional
	public ProformaResponseDto updateProformaExtraBillPercent(Long contractNo, BigDecimal newExtraBillPercent) {

		ProformaMasterModel masterModel = proformaMasterRepository.findFirstByContractNoOrderByIdDesc(contractNo)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND.formatted(contractNo)));
		proformaContractService.getDetailById(masterModel.getId());

		List<ProformaDetailModel> details = proformaDetailRepository.findAllByProformaMasterId(masterModel.getId());

		BigDecimal totalFinal = BigDecimal.ZERO;
		for (ProformaDetailModel detail : details) {
			BigDecimal totalPrice = detail.getTotalAmount() != null && detail.getTotalAmount().compareTo(BigDecimal.ZERO) > 0
					? detail.getTotalAmount()
					: (detail.getProformaGoodItemModels() != null
					? ProformaModelHelper.calculateDetailTotals(detail.getProformaGoodItemModels()).totalAmount()
					: BigDecimal.ZERO);

			calculateAndSetExtraAmount(detail, totalPrice, newExtraBillPercent);
			proformaDetailRepository.save(detail);
			totalFinal = totalFinal.add(detail.getFinalPrice());
		}

		masterModel.setTotalFinalAmount(totalFinal);
		proformaMasterRepository.save(masterModel);

		return proformaContractService.getDetailById(masterModel.getId());
	}

	private void startWorkflowProcess(ProformaMasterModel masterModel) {
		ProformaVariablesInput input = processVariableProvider.buildProformaVariablesInput(masterModel);
		var process = proformaProcessService.startProformaProcess(input);
		masterModel.setProcessId(process.getId());
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
	}

	private void saveDetailAndGoodItems(List<ProformaDetailModel> detailModels, Long masterId) {
		detailModels.forEach(detailModel -> {
			detailModel.setProformaMasterId(masterId);
			proformaDetailRepository.save(detailModel);

			detailModel.getProformaGoodItemModels().stream().distinct()
					.forEach(goodItem -> {
						goodItem.setProformaDetailId(detailModel.getId());
						proformaGoodItemRepository.save(goodItem);
					});
		});
	}

}