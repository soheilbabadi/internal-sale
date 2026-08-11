package com.nicico.internal.sales.remittance.service;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.service.GoodBucketService;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.goods.special.repository.OfferTextRepository;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.loading.model.IssuePlaceModel;
import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import com.nicico.internal.sales.loading.repository.IssuePlaceRepository;
import com.nicico.internal.sales.loading.repository.LoadingPlaceRepository;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.enums.IssueSourceType;
import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.remittance.model.RemittanceGoodItemModel;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.model.RemittanceProformaDataProviderModel;
import com.nicico.internal.sales.remittance.model.RemittanceTradeDataProviderModel;
import com.nicico.internal.sales.remittance.repository.RemittanceDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceProformaDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceTradeDataProviderRepository;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemittanceDataProviderImpl implements RemittanceDataProvider {
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final long DEFAULT_UNIT_ID = 0L;
	private static final int VALIDITY_PERIOD_DAYS = 21;
	private static final Date DEFAULT_DATE = new Date(0);
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final String SETTLEMENT_TYPE_CASH = "نقدی";
	private static final double MIN_NET_WEIGHT = 1.0;
	// Error messages
	private static final String ERROR_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String ERROR_LC_NOT_FOUND = "اعتبار اسنادی وجود ندارد";
	private static final String ERROR_GOOD_NOT_FOUND = "کالا وجود ندارد";
	private static final String ERROR_CUSTOMER_NOT_FOUND = "مشتری مورد نظر وجود ندارد";
	private static final String ERROR_LOADING_PLACE_NOT_FOUND = "محل بارگیری مورد نظر وجود ندارد";
	private static final String ERROR_TRADE_NOT_FOUND = "کالای مورد نظر وجود ندارد";
	private static final String ERROR_ISSUE_PLACE_NOT_FOUND = "پرفورما مورد نظر وجود ندارد";
	private static final String ERROR_PROFORMA_DETAIL_NOT_FOUND = "جزئیات پیش فاکتور وجود ندارد";
	private static final String ERROR_SETTLEMENT_DATE_NOT_FOUND = "تاریخ تسویه معامله وجود ندارد";
	private static final String ERROR_DELIVERY_DATE_NOT_FOUND = "تاریخ تحویل معامله وجود ندارد";
	private static final String ERROR_NET_WEIGHT_REQUIRED = "برای کالاهای فلزات گرانبها وزن خالص باید بزرگتر از صفر باشد";
	private final RemittanceTradeDataProviderRepository remittanceTradeDataProviderRepository;
	private final RemittanceProformaDataProviderRepository remittanceProformaDataProviderRepository;
	private final RemittanceDataProviderRepository remittanceDataProviderRepository;
	private final CustomerRepository customerRepository;
	private final LoadingPlaceRepository loadingPlaceRepository;
	private final IssuePlaceRepository issuePlaceRepository;
	private final LcRepository lcRepository;
	private final OfferTextProcess offerTextProcess;
	private final GoodsService goodsService;
	private final GoodBucketService goodBucketService;
	private final RemittanceTaxService taxService;
	private final ProformaMasterRepository proformaMasterRepository;
	private final TradeExtractRepository tradeExtractRepository;
	private final OfferTextRepository offerTextRepository;


	@Override
	public RemittanceMasterModel getRemittanceFromProforma(RemittanceCreateDto request) {
		RemittanceProformaDataProviderModel proformaData = remittanceProformaDataProviderRepository.findFirstByIdOrderByIdDesc(request.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_PROFORMA_NOT_FOUND));
		LcModel lc = lcRepository.findById(proformaData.getLcId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_LC_NOT_FOUND));

		ProformaMasterModel proformaMaster = proformaMasterRepository.findById(proformaData.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_PROFORMA_NOT_FOUND));
		ProformaGoodItemModel goodItem = extractFirstGoodItem(proformaMaster.getProformaDetailModelLists()
				.stream()
				.filter(detail -> detail.getId() == (proformaData.getProformaDetailId()))
				.findFirst()
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_PROFORMA_DETAIL_NOT_FOUND))
		);
		RemittanceMasterModel masterModel = buildBaseMasterModel(request, proformaData.getPaymentCode(), proformaData.getContractDate());
		masterModel.setValidityDate(calculateValidityDate(request, true));
		BeanUtils.copyProperties(proformaData, masterModel);

		BigDecimal quantity = goodItem.getQuantity();
		BigDecimal creditQuantity = goodItem.getCreditQuantity() != null ? goodItem.getCreditQuantity() : ZERO;
		GoodsBucketModel goodBucket = goodBucketService.findByPaymentCodeModel(proformaData.getPaymentCode());

		ProformaIssueType proformaIssueType = proformaData.getProformaIssueType();

		switch (proformaIssueType) {
			case LETTER_OF_CREDIT_OPENING:
				masterModel.setIssueSourceType(IssueSourceType.LETTER_OF_CREDIT_OPENING);
				break;
			case BANK_GUARANTEE:
				masterModel.setIssueSourceType(IssueSourceType.BANK_GUARANTEE);
				break;
			case CASH:
				masterModel.setIssueSourceType(IssueSourceType.CASH);
				break;
			case FROM_CREDIT_FACILITIES:
				masterModel.setIssueSourceType(IssueSourceType.FROM_CREDIT_FACILITIES);
				break;
			case GUARANTEE_CHECK:
				masterModel.setIssueSourceType(IssueSourceType.GUARANTEE_CHECK);
				break;
			case GAM_BONDS:
				masterModel.setIssueSourceType(IssueSourceType.GAM_BONDS);
				break;
			case EXTRA_BILL_OF_EXCHANGE:
				masterModel.setIssueSourceType(IssueSourceType.EXTRA_BILL_OF_EXCHANGE);
				break;
			default:
				masterModel.setIssueSourceType(IssueSourceType.UNKNOWN);
				break;
		}
		masterModel.setTradeId(proformaMaster.getTradeId());
		masterModel.setCustomerId(proformaMaster.getCustomerId());
		masterModel.setCustomerName(proformaMaster.getCustomerName());
		masterModel.setEconomicCode(proformaMaster.getEconomicCode());
		masterModel.setNationalCode(proformaMaster.getNationalCode());
		masterModel.setPaymentCode(proformaMaster.getPaymentCode());
		masterModel.setPackingId(goodBucket.getPackingId());
		masterModel.setPackingName(goodBucket.getPackingName());
		masterModel.setGoodId(goodBucket.getGoodId());
		masterModel.setGoodName(goodBucket.getGoodName());
		masterModel.setProcessFinal(false);
		masterModel.setProformaMasterId(proformaMaster.getId());
		masterModel.setProformaDetailId(proformaData.getProformaDetailId());
		masterModel.setLcId(proformaData.getLcId());
		masterModel.setLcNo(proformaData.getLcNo());
		masterModel.setProformaNo(proformaData.getProformaNo());
		masterModel.setProformaDate(proformaData.getProformaDate());
		masterModel.setLcExpiryDate(lc.getLcExpiryDate());
		masterModel.setSettlementDueDate(lc.getSettlementDueDate());
		masterModel.setIssuerBankId(lc.getIssuerBankId());
		masterModel.setIssuerBankName(lc.getIssuerBankName());
		masterModel.setIssuerBankBranchCode(lc.getIssuerBankBranchCode());
		masterModel.setIssuerBankBranchName(lc.getIssuerBankBranchName());
		masterModel.setContractNo(String.valueOf(proformaMaster.getContractNo()));
		masterModel.setDelayPenalty(proformaData.isDelayPenalty());
		masterModel.setSellerBrokerName(proformaData.getSellerBrokerName());
		masterModel.setBuyerBrokerName(proformaData.getBuyerBrokerName());
		masterModel.setTradingBankId(proformaData.getTradingBankId());
		masterModel.setTradingBankTitle(proformaData.getTradingBankName());
		masterModel.setTradingBankBranchTitle(proformaData.getTradingBankBranchName());
		masterModel.setSettlementType(proformaData.getSettlementType());
		masterModel.setSettlementTypeDesc(proformaData.getSettlementTypeDesc());
		masterModel.setRemittanceUnitPriceCredit(proformaData.getCreditUnitPrice());
		masterModel.setRemittanceQuantity(quantity);
		masterModel.setRemittanceQuantityCash(quantity.subtract(creditQuantity));
		masterModel.setRemittanceQuantityCredit(creditQuantity);
		masterModel.setRemittanceUnitPriceCash(BigDecimal.valueOf(proformaData.getUnitPrice()));
		masterModel.setCashPercentage(proformaData.getCashPercentage());
		masterModel.setCreditPercentage(proformaData.getCreditPercentage());
		masterModel.setBrokerId(proformaMaster.getBrokerId());
		masterModel.setBrokerName(proformaMaster.getBrokerName());
		masterModel.setBrokerNationalCode("-");
		masterModel.setTotalQuantity(proformaMaster.getTotalQuantity());
		masterModel.setTotalFinalAmount(proformaMaster.getTotalFinalAmount());
		masterModel.setOfferDescription(proformaMaster.getOfferDescription());
		masterModel.setImeCommoditySymbol(proformaMaster.getImeCommoditySymbol());
		masterModel.setLotNumber(proformaData.getLotNumber());


		GoodItemInput goodItemInput = new GoodItemInput(
				proformaData.getGoodId(),
				proformaData.getGoodName(),
				Double.valueOf(proformaData.getUnitCount()),
				proformaData.getUnitPrice(),
				proformaData.getCashAmount(),
				proformaData.getFinalAmount(),
				proformaData.getPaymentCode()
		);
		RemittanceGoodItemModel goodItemModel = createGoodItem(request, goodItemInput);
		return createRemittanceWithGoodItem(masterModel, goodItemModel);
	}

	@Override
	public RemittanceMasterModel getRemittanceFromTrade(RemittanceCreateDto request) {
		RemittanceTradeDataProviderModel trade = getTradeById(request);
		CustomerModel customer = customerRepository.findById(trade.getCustomerId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_CUSTOMER_NOT_FOUND));
		GoodsBucketModel goodBucket = goodBucketService.getOnSpecificDateModel(trade.getGoodId(), DateUtility.toGregorianDate(trade.getContractDate()));
		RemittanceMasterModel masterModel = buildBaseMasterModel(request, trade.getPaymentCode(), trade.getContractDate());
		masterModel.setValidityDate(calculateValidityDate(request, false));
		BeanUtils.copyProperties(trade, masterModel);
		BigDecimal quantity = BigDecimal.valueOf(trade.getUnitCount());
		BigDecimal cashQuantity = BigDecimal.valueOf(trade.getUnitCount());
		double unitPriceCredit = trade.getUnitPrice();


		String offerText = offerTextRepository.getDescriptionByPaymentCode(trade.getPaymentCode());
		String goodName = "";

		if (offerText.contains("مولیبدن سونگون")) {
			goodName = "سولفور مولیبدن سونگون";
		} else if (offerText.contains("مولیبدن سرچشمه")) {
			goodName = "سولفور مولیبدن";
		} else {
			goodName = goodBucket.getGoodName();
		}


		masterModel.setTradeId(trade.getId());
		masterModel.setRemittanceUnitPriceCredit(BigDecimal.valueOf(unitPriceCredit));
		masterModel.setCustomerId(customer.getId());
		masterModel.setCustomerName(customer.getName());
		masterModel.setEconomicCode(customer.getEconomicCode());
		masterModel.setNationalCode(customer.getNationalCode());
		masterModel.setPaymentCode(trade.getPaymentCode());
		masterModel.setPackingId(goodBucket.getPackingId());
		masterModel.setPackingName(goodBucket.getPackingName());
		masterModel.setGoodId(goodBucket.getGoodId());
		masterModel.setGoodName(goodName);
		masterModel.setProcessFinal(false);
		masterModel.setIssueSourceType(IssueSourceType.CASH);
		masterModel.setProformaMasterId(DEFAULT_UNIT_ID);
		masterModel.setProformaDetailId(DEFAULT_UNIT_ID);
		masterModel.setLcId(DEFAULT_UNIT_ID);
		masterModel.setLcNo(DEFAULT_PLACEHOLDER);
		masterModel.setProformaNo(DEFAULT_PLACEHOLDER);
		masterModel.setProformaDate(DEFAULT_DATE);
		masterModel.setProformaIssueType(ProformaIssueType.CASH);
		masterModel.setLcExpiryDate(DEFAULT_DATE);
		masterModel.setSettlementDueDate(DEFAULT_DATE);
		masterModel.setIssuerBankId(DEFAULT_UNIT_ID);
		masterModel.setIssuerBankName(DEFAULT_PLACEHOLDER);
		masterModel.setIssuerBankBranchCode(DEFAULT_PLACEHOLDER);
		masterModel.setIssuerBankBranchName(DEFAULT_PLACEHOLDER);
		masterModel.setTradingBankId(0L);
		masterModel.setTradingBankTitle(DEFAULT_PLACEHOLDER);
		masterModel.setTradingBankBranchTitle(DEFAULT_PLACEHOLDER);
		masterModel.setIssueSourceType(IssueSourceType.CASH);
		masterModel.setRemittanceQuantity(quantity);
		masterModel.setRemittanceQuantityCash(cashQuantity);
		masterModel.setRemittanceQuantityCredit(BigDecimal.ZERO);
		masterModel.setRemittanceUnitPriceCash(BigDecimal.valueOf(trade.getUnitPrice()));
		masterModel.setCashPercentage(HUNDRED);
		masterModel.setCreditPercentage(BigDecimal.ZERO);
		masterModel.setProcessFinal(false);

		masterModel.setBrokerId(trade.getSellerBrokerId());
		masterModel.setBrokerName(trade.getBuyerBrokerName());
		masterModel.setBrokerNationalCode("-");
		masterModel.setTotalQuantity(BigDecimal.valueOf(trade.getUnitCount()));
		masterModel.setTotalFinalAmount(BigDecimal.valueOf(trade.getUnitPrice() * trade.getUnitCount()));
		masterModel.setOfferDescription(trade.getOfferDescription());
		masterModel.setImeCommoditySymbol(trade.getImeCommoditySymbol());
		masterModel.setLotNumber(offerTextProcess.extractLotNumber(trade.getOfferDescription()));


		GoodItemInput goodItemInput = new GoodItemInput(
				trade.getGoodId(),
				trade.getGoodName(),
				Double.valueOf(trade.getUnitCount()),
				trade.getUnitPrice(),
				trade.getCashAmount(),
				trade.getFinalAmount(),
				trade.getPaymentCode()
		);
		RemittanceGoodItemModel goodItemModel = createGoodItem(request, goodItemInput);

		return createRemittanceWithGoodItem(masterModel, goodItemModel);
	}

	@Override
	public String getLotnumber(Long tradeId, RemittanceSourceType sourceType) {
		if (sourceType == RemittanceSourceType.TRADE) {
			var trade = tradeExtractRepository.findById(tradeId)
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_TRADE_NOT_FOUND));
			return extractLotNumber(trade.getOfferDescription());
		} else {
			RemittanceProformaDataProviderModel proformaData = remittanceProformaDataProviderRepository.findFirstByIdOrderByIdDesc(tradeId)
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_PROFORMA_NOT_FOUND));
			return extractLotNumber(proformaData.getOfferDescription());
		}
	}

	private RemittanceMasterModel createRemittanceWithGoodItem(RemittanceMasterModel masterModel,
	                                                           RemittanceGoodItemModel goodItemModel) {
		goodItemModel.setRemittanceMasterModel(masterModel);
		masterModel.setRemittanceGoodItemModels(List.of(goodItemModel));
		return masterModel;
	}

	private RemittanceMasterModel buildBaseMasterModel(RemittanceCreateDto request,
	                                                   String paymentCode, String contractDate) {
		IssuePlaceModel issuePlace = issuePlaceRepository.findById(request.getIssuePlaceId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_ISSUE_PLACE_NOT_FOUND));
		LoadingPlaceModel loadingPlace = loadingPlaceRepository.findById(request.getLoadingPortId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_LOADING_PLACE_NOT_FOUND));
		String lotNumber = extractLotNumber(paymentCode);
		GoodsBucketModel goodBucket = goodBucketService.findByPaymentCodeModel(paymentCode);


		RemittanceMasterModel masterModel = new RemittanceMasterModel();
		masterModel.setRemittanceDate(new Date());
		masterModel.setLotNumber(lotNumber);
		masterModel.setContractDate(DateUtility.toGregorianDate(contractDate));
		masterModel.setLoadingPort(loadingPlace.getPlaceTitle());
		masterModel.setLoadingPortId(loadingPlace.getId());
		masterModel.setIssuePlaceId(issuePlace.getId());
		masterModel.setIssuePlace(issuePlace.getPlaceTitle());
		masterModel.setProcessId(DEFAULT_PLACEHOLDER);
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.DRAFT);
		masterModel.setIssuerId(SecurityUtil.getUserId());
		masterModel.setIssuerName(SecurityUtil.getFirstName() + " " + SecurityUtil.getLastName());
		masterModel.setProcessFinal(false);
		masterModel.setRemittanceNumber(DEFAULT_PLACEHOLDER);
		masterModel.setTargetAddress(DEFAULT_PLACEHOLDER);
		masterModel.setDescription(DEFAULT_PLACEHOLDER);
		masterModel.setPackingId(goodBucket.getPackingId());
		masterModel.setPackingName(goodBucket.getPackingName());
		masterModel.setTaxAmount(BigDecimal.valueOf(taxService.calculateTax(request)));
		masterModel.setGoodId(goodBucket.getGoodId());
		masterModel.setGoodName(goodBucket.getGoodName());


		return masterModel;
	}


	private RemittanceGoodItemModel createGoodItem(RemittanceCreateDto request, GoodItemInput input) {
		if (goodsService.isPreciousMetal(input.goodId)) {
			return createPreciousMetalGoodItem(request, input.goodId, input.goodName, input.unitCount,
					input.unitPrice, input.paymentCode);
		}
		return createStandardGoodItem(request, input);
	}

	private RemittanceGoodItemModel createStandardGoodItem(RemittanceCreateDto request, GoodItemInput input) {

		String lot = extractLotNumber(input.paymentCode);
		BigDecimal price = BigDecimal.valueOf(input.unitPrice);
		BigDecimal quantity = BigDecimal.valueOf(input.unitCount);
		RemittanceGoodItemModel goodItemModel = new RemittanceGoodItemModel();
		goodItemModel.setGoodId(input.goodId);
		goodItemModel.setGoodName(input.goodName);
		goodItemModel.setUnitId(DEFAULT_UNIT_ID);
		goodItemModel.setQuantity(quantity);
		goodItemModel.setCreditQuantity(ZERO);
		goodItemModel.setUnitPrice(price);
		goodItemModel.setUnitPriceCash(price);
		goodItemModel.setUnitPriceCredit(price);
		goodItemModel.setCashAmount(price.multiply(quantity));
		goodItemModel.setCreditAmount(ZERO);
		goodItemModel.setTotalAmount(input.cashAmount);
		goodItemModel.setVatAmount(ZERO);
		goodItemModel.setNetQuantity(BigDecimal.valueOf(request.getNetWeight()));
		goodItemModel.setFinalAmount(input.finalAmount);
		goodItemModel.setLotNumber(lot);
		goodItemModel.setCreditPercentage(ZERO);

		return goodItemModel;
	}

	private RemittanceGoodItemModel createPreciousMetalGoodItem(RemittanceCreateDto request, Long goodId, String goodName,
	                                                            Double unitCount, Double unitPrice,
	                                                            String paymentCode) {

		long calculatedCashAmount = Math.round(unitCount * unitPrice);
		String lot = extractLotNumber(paymentCode);
		BigDecimal calculatedAmount = BigDecimal.valueOf(calculatedCashAmount);

		RemittanceGoodItemModel goodItemModel = new RemittanceGoodItemModel();
		goodItemModel.setGoodId(goodId);
		goodItemModel.setGoodName(goodName);
		goodItemModel.setUnitId(DEFAULT_UNIT_ID);
		goodItemModel.setQuantity(BigDecimal.valueOf(unitCount));
		goodItemModel.setCreditQuantity(ZERO);
		goodItemModel.setUnitPrice(BigDecimal.valueOf(unitPrice));
		goodItemModel.setUnitPriceCash(BigDecimal.valueOf(unitPrice));
		goodItemModel.setUnitPriceCredit(ZERO);
		goodItemModel.setCashAmount(calculatedAmount);
		goodItemModel.setCreditAmount(ZERO);
		goodItemModel.setTotalAmount(calculatedAmount);
		goodItemModel.setNetQuantity(BigDecimal.valueOf(request.getNetWeight()));
		goodItemModel.setFinalAmount(calculatedAmount);
		goodItemModel.setLotNumber(lot);
		goodItemModel.setCreditPercentage(ZERO);

		return goodItemModel;
	}

	@Override
	public String extractLotNumber(String paymentCode) {
		return offerTextProcess.extractLotNumber(
				offerTextProcess.findDescriptionByPaymentCode(paymentCode));
	}


	private ProformaGoodItemModel extractFirstGoodItem(ProformaDetailModel proformaDetail) {
		var items = proformaDetail.getProformaGoodItemModels();
		if (items == null || items.isEmpty()) {
			throw new InternalSaleCustomException.ResourceNotFoundException(ERROR_GOOD_NOT_FOUND);
		}
		return items.get(0);
	}

	private RemittanceTradeDataProviderModel getTradeById(RemittanceCreateDto request) {
		return remittanceTradeDataProviderRepository.findFirstByIdOrderByContractDateDesc(request.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_TRADE_NOT_FOUND));
	}

	@Override
	public Date lastDeliveryDeadlineProforma(RemittanceCreateDto request) {

		var dataProviderModel = remittanceDataProviderRepository.findById(request.getTradeId());
//                .orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERROR_TRADE_NOT_FOUND));

		if (dataProviderModel.isEmpty()) return new Date();
		Date settlement = DateUtility.toGregorianDate(dataProviderModel.get().getSettlementDate());
		Date delivery = DateUtility.toGregorianDate(dataProviderModel.get().getDeliveryDate());
		return calculateDeadline(settlement, delivery);
	}

	@Override
	public Date lastDeliveryDeadlineTrade(RemittanceCreateDto request) {
		try {
			var trade = getTradeById(request);
			Date settlement = DateUtility.toGregorianDate(trade.getSettlementDate());
			Date delivery = DateUtility.toGregorianDate(trade.getDeliveryDate());

			return calculateDeadline(settlement, delivery);
		} catch (Exception exception) {
			log.error(exception.getMessage());
		}
		return new Date();

	}


	private Date calculateValidityDate(RemittanceCreateDto request, boolean isProforma) {
		return isProforma ? lastDeliveryDeadlineProforma(request) : lastDeliveryDeadlineTrade(request);
	}

	private Date calculateDeadline(Date settlement, Date delivery) {
		validateDates(settlement, delivery);
		Date laterDate = settlement.after(delivery) ? settlement : delivery;
		return addDays(laterDate);
	}

	private void validateDates(Date settlement, Date delivery) {
		if (settlement == null) {
			throw new InternalSaleCustomException.ValidationException(ERROR_SETTLEMENT_DATE_NOT_FOUND);
		}
		if (delivery == null) {
			throw new InternalSaleCustomException.ValidationException(ERROR_DELIVERY_DATE_NOT_FOUND);
		}
	}

	private Date addDays(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, VALIDITY_PERIOD_DAYS);
		return cal.getTime();
	}

	private record GoodItemInput(Long goodId, String goodName, Double unitCount, Double unitPrice,
	                             BigDecimal cashAmount, BigDecimal finalAmount, String paymentCode) {
	}
}
