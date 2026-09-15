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
public class PreciousMetalGaamBoundServiceImp implements PreciousMetalGaamBoundService {

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

	// ==================== PUBLIC SERVICE METHODS ====================


	@Transactional
	@Override
	public String create(PreciousMetalProfomaCreateRequest requestDto) {
		log.debug("Creating precious metal proforma for tradeId: {}", requestDto.getTradeId());

		// اعتبارسنجی دسترسی
		if (!proformaProcessService.canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(MSG_PROCESS_ACCESS_DENIED);
		}

		// اعتبارسنجی داده ها
		proformaValidationService.validateProformaData(requestDto);

		// ایجاد پیش فاکتور
		ProformaMasterModel model = createProformaMaster(requestDto);

		// شروع فرآیند
		startProformaProcess(model);

		// ذخیره نهایی
		proformaMasterRepository.saveAndFlush(model);

		log.info("Precious metal proforma created successfully with contractNo: {}", model.getContractNo());
		return model.getContractNo().toString();
	}


	private ProformaMasterModel createProformaMaster(PreciousMetalProfomaCreateRequest requestDto) {
		log.debug("Creating proforma master for precious metal");

		ProformaModelResponse contractDetail = getContractDetail(requestDto);
		ProformaMasterModel masterModel = contractDetail.getMasterModel();
		masterModel.setContractNo(requestDto.getContractNo());
		List<ProformaDetailModel> detailList = distinctDetails(contractDetail.getDetailModels());
		masterModel.setProformaDetailModelLists(detailList);
		masterModel = proformaMasterRepository.saveAndFlush(masterModel);
		saveDetailAndGoodItems(masterModel.getId(), detailList, detailList.stream().flatMap(d -> d.getProformaGoodItemModels().stream()).toList());
		log.info("Proforma master created successfully with id: {}", masterModel.getId());
		return masterModel;
	}


	private ProformaModelResponse getContractDetail(PreciousMetalProfomaCreateRequest requestDto) {
		log.debug("Getting contract detail for precious metal, tradeId: {}", requestDto.getTradeId());
		TradeExtractModel tradeExtract = tradeExtractRepository.findById(requestDto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND_DETAIL));
		IMETradeModel tradeModel = proformaContractService.getTradeModel(tradeExtract.getPaymentCode());
		GoodsModel goodsModel = proformaContractService.findGoodsModelByCommodityCode(Long.valueOf(tradeModel.getCommodityCode()));
		SaleConditionModel saleConditionModel = proformaContractService.getSaleConditionModel(tradeExtract.getPaymentCode());
		GoodsBucketModel goodsBucketModel = proformaContractService.getGoodBucketModel(tradeExtract.getPaymentCode());
		CustomerModel customerModel = proformaContractService.getCustomerModel(tradeExtract.getBuyerNationalCode());
		PreciousMetalDetailGenerator params = createDetailGenerator(requestDto, tradeModel, goodsModel, DateUtility.getJalaliYear(requestDto.getOrderDate()), goodsBucketModel, saleConditionModel);
		List<ProformaDetailModel> detailDtoList = generatePerformaDetailList(params);
		Totals totals = calculateTotals(detailDtoList);
		ProformaMasterModel masterModel = buildMasterModel(tradeExtract, tradeModel, goodsModel, customerModel, goodsBucketModel, requestDto, totals, params);
		setupFullRelationships(masterModel, detailDtoList);

		log.info("Contract detail retrieved successfully");
		return ProformaModelResponse.builder().masterModel(masterModel).detailModels(detailDtoList).build();
	}

	private void startProformaProcess(ProformaMasterModel model) {
		var input = proformaProcessService.buildProformaVariablesInput(model);
		var process = proformaProcessService.startProformaProcess(input);
		model.setProcessId(process.getId());
		model.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		model.setIsProcessFinal(false);
		model.setIsReversalProcessFinal(false);
	}

	private void saveDetailAndGoodItems(Long masterId, List<ProformaDetailModel> details, List<ProformaGoodItemModel> goodItems) {
		if (details != null && !details.isEmpty()) {
			details.forEach(detail -> {
				detail.setProformaMasterId(masterId);
				proformaDetailRepository.save(detail);

				if (detail.getProformaGoodItemModels() != null) {
					detail.getProformaGoodItemModels().forEach(goodItem -> {
						goodItem.setProformaDetailId(detail.getId());

						proformaGoodItemRepository.save(goodItem);
					});
				}
			});
		} else if (goodItems != null && !goodItems.isEmpty()) {
			proformaGoodItemRepository.saveAll(goodItems);
		}
	}

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

	private List<ProformaDetailModel> generatePerformaDetailList(PreciousMetalDetailGenerator params) {
		List<String> serial = proformaSerialService.getProformaSerial(1);
		List<ProformaGoodItemModel> goodItems = generateProformaGoodItemList(params);
		DetailTotals detailTotals = calculateDetailTotals(goodItems);
		ProformaDetailModel detailModel = buildProformaDetailModel(
				goodItems,
				params.jalaliYear(),
				params.saleConditionModel(),
				params.requestDto().getDeadlineDays(),
				serial.get(0),
				new Date(),
				detailTotals,
				SETTLEMENT_TYPE_DEFAULT,
				ProformaIssueType.LETTER_OF_CREDIT_OPENING,
				params.requestDto().getOrderDate(),
				params.tradeModel().getContractDate(),
				ProformaReversalStatus.NORMAL,
				BigDecimal.ZERO,
				BigDecimal.ZERO
		);
		goodItems.forEach(goodItem -> goodItem.setProformaDetailModel(detailModel));
		calculateAndSetExtraAmount(detailModel, detailTotals.finalAmount(), params.saleConditionModel());
		return List.of(detailModel);
	}

	private List<ProformaGoodItemModel> generateProformaGoodItemList(PreciousMetalDetailGenerator params) {
		ProformaGoodItemModel goodItem = generateProformaGoodItem(params);
		return List.of(goodItem);
	}

	private ProformaGoodItemModel generateProformaGoodItem(PreciousMetalDetailGenerator params) {
		var tradeModel = proformaContractService.getTradeModel(params.requestDto().getTradeId());
		String rawDescription = offerTextProcess.findDescriptionByPaymentCode(tradeModel.getPaymentCode());
		String selenium = extractSelenium(rawDescription, offerTextProcess);
		String lot = extractLotNumber(rawDescription, offerTextProcess);
		String cleanName = getCleanName(params.good());
		String finalGoodName = cleanName + " " + selenium;

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


	private void calculateAndSetExtraAmount(ProformaDetailModel detailModel, BigDecimal totalPrice, SaleConditionModel saleConditionModel) {
		if (saleConditionModel == null) {
			return;
		}
		BigDecimal extraPercent = null;
		extraPercent = saleConditionModel.getExtraGamCertificatePercent();
		if (extraPercent != null && extraPercent.compareTo(BigDecimal.ZERO) > 0) {
			// Calculate Final Price: Base * (1 + percent/100)
			BigDecimal factor = BigDecimal.ONE.add(extraPercent.divide(HUNDRED, 10, RoundingMode.UP));
			BigDecimal finalPrice = totalPrice.multiply(factor).setScale(2, RoundingMode.UP);
			BigDecimal extraAmount = finalPrice.subtract(totalPrice).setScale(2, RoundingMode.UP);
			detailModel.setFinalPrice(finalPrice);
			detailModel.setExtraBillOfPercent(extraPercent);
			detailModel.setExtraBillOfExchangeAmount(extraAmount);
		}
	}

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


}