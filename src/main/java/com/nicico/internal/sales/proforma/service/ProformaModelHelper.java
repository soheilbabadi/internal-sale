package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.proforma.dto.ProformaDetailDto;
import com.nicico.internal.sales.proforma.dto.ProformaGoodItemDto;
import com.nicico.internal.sales.proforma.dto.ProformaMasterMapper;
import com.nicico.internal.sales.proforma.dto.ProformaResponseDto;
import com.nicico.internal.sales.proforma.enums.*;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ProformaModelHelper {

	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final BigDecimal TOTAL_PERCENTAGE = BigDecimal.valueOf(100);
	private static final String UNIT_NAME = "کیلوگرم";
	private static final String DEFAULT_LOT_NUMBER = "-";
	private static final String SARCHESHMEH = "سرچشمه";
	private static final String EMPTY_STRING = "";

	private ProformaModelHelper() {
	}

	public static void setupFullRelationships(ProformaMasterModel master, List<ProformaDetailModel> details) {
		if (master == null || details == null || details.isEmpty()) {
			return;
		}

		details.forEach(detail -> {
			detail.setProformaMasterModel(master);
			detail.setProformaMasterId(master.getId());

			if (detail.getProformaGoodItemModels() != null && !detail.getProformaGoodItemModels().isEmpty()) {
				detail.getProformaGoodItemModels().forEach(item -> {
					item.setProformaDetailModel(detail);
					if (detail.getId() > 0) {
						item.setProformaDetailId(detail.getId());
					}
				});
			}
		});
	}

	public static void setMasterForDetails(List<ProformaDetailModel> details, ProformaMasterModel master) {
		if (details == null || master == null) {
			return;
		}
		details.forEach(detail -> {
			detail.setProformaMasterModel(master);
			detail.setProformaMasterId(master.getId());
		});
	}

	public static void setDetailForGoodItems(List<ProformaGoodItemModel> goodItems, ProformaDetailModel detail) {
		if (goodItems == null || detail == null) {
			return;
		}
		goodItems.forEach(item -> {
			item.setProformaDetailModel(detail);
			if (detail.getId() > 0) {
				item.setProformaDetailId(detail.getId());
			}
		});
	}

	public static List<ProformaDetailModel> distinctDetails(List<ProformaDetailModel> detailModels) {
		if (detailModels == null || detailModels.isEmpty()) {
			return new ArrayList<>();
		}
		return detailModels.stream()
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static Totals calculateTotals(List<ProformaDetailModel> detailModels) {
		if (detailModels == null || detailModels.isEmpty()) {
			return new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		return new Totals(
				sum(detailModels, ProformaGoodItemModel::getCashAmount),
				sum(detailModels, ProformaGoodItemModel::getQuantity),
				sum(detailModels, ProformaGoodItemModel::getCreditAmount),
				sum(detailModels, ProformaGoodItemModel::getVatAmount),
				sum(detailModels, ProformaGoodItemModel::getFinalAmount)
		);
	}

	public static Totals calculateTotalsWithFilter(
			List<ProformaDetailModel> detailModels,
			java.util.function.Predicate<ProformaDetailModel> filter) {

		if (detailModels == null || detailModels.isEmpty()) {
			return new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		List<ProformaDetailModel> filtered = detailModels.stream()
				.filter(filter)
				.collect(Collectors.toList());
		return calculateTotals(filtered);
	}

	public static DetailTotals calculateDetailTotals(List<ProformaGoodItemModel> goodItems) {
		if (goodItems == null || goodItems.isEmpty()) {
			return new DetailTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		return new DetailTotals(
				sumItems(goodItems, ProformaGoodItemModel::getTotalAmount),
				sumItems(goodItems, ProformaGoodItemModel::getVatAmount),
				sumItems(goodItems, ProformaGoodItemModel::getFinalAmount),
				sumItems(goodItems, ProformaGoodItemModel::getCashAmount),
				sumItems(goodItems, ProformaGoodItemModel::getCreditAmount),
				sumItems(goodItems, ProformaGoodItemModel::getQuantity)
		);
	}


	public static CashGoodItemCalculation calculateCashGoodItem(
			IMETradeModel tradeModel,
			GoodsBucketModel goodsBucketModel,
			BigDecimal vat,
			double quantity,
			boolean cashPercentTotal,
			Long goodId,
			String goodName,
			String lotNumber) {

		double vatRate = vat.doubleValue() / 100;
		long unitPrice = Math.round(tradeModel.getUnitPrice());

		double cashQuantity;
		double creditQuantity;
		long creditPercent;
		long creditAmount;
		long cashAmount;
		long vatCashAmount;
		long vatCreditAmount;
		long unitPriceCredit;

		if (cashPercentTotal) {
			creditPercent = 0;
			double additionalValue = (goodsBucketModel.getCommission() + 100) / 100.0;
			unitPriceCredit = (long) Math.ceil(unitPrice * additionalValue);
			cashQuantity = quantity;
			creditQuantity = 0;
			cashAmount = Math.round(cashQuantity * unitPrice);
			creditAmount = 0;
			vatCashAmount = Math.round(vatRate * cashAmount);
			vatCreditAmount = 0;
		} else {
			int cashPercent = goodsBucketModel.getCashPercentage().intValue();
			creditPercent = 100L - cashPercent;
			creditQuantity = (quantity * creditPercent) / 100.0;
			cashQuantity = quantity - creditQuantity;
			double additionalValue = (goodsBucketModel.getCommission() + 100) / 100.0;
			unitPriceCredit = (long) Math.ceil(unitPrice * additionalValue);
			creditAmount = Math.round(creditQuantity * unitPriceCredit);
			cashAmount = Math.round(cashQuantity * unitPrice);
			vatCashAmount = Math.round(vatRate * cashAmount);
			vatCreditAmount = Math.round(vatRate * creditAmount);
		}

		long vatAmount = vatCreditAmount + vatCashAmount;
		long totalAmount = cashAmount + creditAmount;
		long finalAmount = totalAmount + vatAmount;

		return new CashGoodItemCalculation(
				goodId,
				goodName,
				1L,
				vat,
				BigDecimal.valueOf(quantity),
				BigDecimal.valueOf(creditQuantity),
				BigDecimal.valueOf(unitPriceCredit).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(unitPrice).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(unitPrice).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(creditAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(vatCashAmount).setScale(0, RoundingMode.HALF_UP),
				BigDecimal.valueOf(vatCreditAmount).setScale(0, RoundingMode.HALF_UP),
				BigDecimal.valueOf(vatAmount).setScale(0, RoundingMode.HALF_UP),
				BigDecimal.ZERO,
				BigDecimal.valueOf(totalAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(finalAmount).setScale(0, RoundingMode.UP),
				BigDecimal.ZERO,
				lotNumber != null ? lotNumber : DEFAULT_LOT_NUMBER,
				BigDecimal.valueOf(creditPercent),
				BigDecimal.valueOf(100L - creditPercent)
		);
	}

	public static PreciousGoodItemCalculation calculatePreciousGoodItem(
			IMETradeModel tradeModel,
			GoodsBucketModel goodsBucketModel,
			BigDecimal vat,
			double netWeight,
			double totalWeight,
			boolean cashPercentTotal,
			Long goodId,
			String goodName,
			String lotNumber) {

		double vatRate = vat.doubleValue() / 100;
		long unitPrice = Math.round(tradeModel.getUnitPrice());

		double cashPercentage;
		double creditPercentage;
		double cashQuantity;
		double creditQuantity;
		long cashAmount;
		long vatCashAmount;

		if (cashPercentTotal) {
			cashPercentage = 100.0;
			creditPercentage = 0.0;
			cashQuantity = 0.0;
			creditQuantity = netWeight;
			cashAmount = 0;
			vatCashAmount = 0;
		} else {
			cashPercentage = goodsBucketModel.getCashPercentage().doubleValue();
			creditPercentage = 100.0 - cashPercentage;
			cashQuantity = netWeight * (cashPercentage / 100.0);
			creditQuantity = netWeight - cashQuantity;
			cashAmount = Math.round(cashQuantity * unitPrice);
			vatCashAmount = Math.round(vatRate * cashAmount);
		}

		long creditAmount = Math.round(creditQuantity * unitPrice);
		long vatCreditAmount = Math.round(vatRate * creditAmount);
		long vatAmount = vatCreditAmount + vatCashAmount;
		long totalAmount = cashAmount + creditAmount;
		long finalAmount = totalAmount + vatAmount;
		double additionalValue = (goodsBucketModel.getCommission() + 100) / 100.0;
		long unitPriceCredit = (long) Math.ceil(unitPrice * additionalValue);

		return new PreciousGoodItemCalculation(
				goodId,
				goodName,
				1L,
				vat.setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(totalWeight),
				BigDecimal.valueOf(creditQuantity).setScale(2, RoundingMode.HALF_UP),
				BigDecimal.valueOf(unitPriceCredit).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(unitPrice).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(unitPrice).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(creditAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(vatCashAmount).setScale(0, RoundingMode.HALF_UP),
				BigDecimal.valueOf(vatCreditAmount).setScale(0, RoundingMode.HALF_UP),
				BigDecimal.valueOf(vatAmount).setScale(0, RoundingMode.HALF_UP),
				BigDecimal.ZERO,
				BigDecimal.valueOf(totalAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(finalAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(netWeight),
				lotNumber != null ? lotNumber : DEFAULT_LOT_NUMBER,
				BigDecimal.valueOf(creditPercentage).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashPercentage).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashQuantity).setScale(3, RoundingMode.HALF_UP)
		);
	}

	public static PreciousMetalLCCalculation calculatePreciousMetalLCGoodItem(
			IMETradeModel tradeModel,
			GoodsBucketModel goodsBucketModel,
			BigDecimal vat,
			double totalWeight,
			double netWeight,
			Long goodId,
			String goodName,
			String lotNumber) {

		double cashPercentage = goodsBucketModel.getCashPercentage().doubleValue();
		double creditPercentage = 100.0 - cashPercentage;
		double vatRate = vat.doubleValue() / 100;
		long unitPrice = (long) Math.ceil(tradeModel.getUnitPrice());
		double interestPercent = goodsBucketModel.getCommission();
		double additionalValue = ((interestPercent + 100) / 100.0);
		long unitPriceCredit = (long) Math.ceil(unitPrice * additionalValue);

		double creditQuantity = netWeight - (totalWeight * cashPercentage / 100);
		double cashQuantity = netWeight - creditQuantity;
		long creditAmount = (long) (creditQuantity * unitPriceCredit);
		long cashAmount = (long) (cashQuantity * unitPrice);
		long vatCashAmount = (long) (vatRate * cashAmount);
		long vatCreditAmount = (long) (vatRate * creditAmount);
		long vatAmount = vatCreditAmount + vatCashAmount;
		long totalAmount = cashAmount + creditAmount;
		long finalAmount = totalAmount + vatAmount;

		return new PreciousMetalLCCalculation(
				goodId,
				goodName,
				1L,
				vat.setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(totalWeight),
				BigDecimal.valueOf(creditQuantity).setScale(2, RoundingMode.HALF_UP),
				BigDecimal.valueOf(unitPriceCredit).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(unitPrice).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(unitPrice).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(creditAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(vatCashAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(vatCreditAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(vatAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(interestPercent).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(totalAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(finalAmount).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(netWeight),
				lotNumber != null ? lotNumber : DEFAULT_LOT_NUMBER,
				BigDecimal.valueOf(creditPercentage).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashPercentage).setScale(0, RoundingMode.UP),
				BigDecimal.valueOf(cashQuantity).setScale(2, RoundingMode.HALF_UP)
		);
	}

	public static CashCreditPercentages calculateCashCreditPercentages(
			GoodsBucketModel goodsBucketModel,
			boolean isFullCash) {

		BigDecimal cashPercentage;
		BigDecimal creditPercentage;

		if (isFullCash) {
			cashPercentage = BigDecimal.valueOf(100);
			creditPercentage = BigDecimal.ZERO;
		} else {
			cashPercentage = goodsBucketModel.getCashPercentage();
			creditPercentage = TOTAL_PERCENTAGE.subtract(goodsBucketModel.getCashPercentage());
		}

		return new CashCreditPercentages(cashPercentage, creditPercentage);
	}



	public static String processGoodName(GoodsModel goodsModel, String description) {
		String cleanName = getCleanName(goodsModel);

		if (description.contains("مولیبدن سونگون")) {
			return "سولفور مولیبدن سونگون";
		} else if (description.contains(SARCHESHMEH) || cleanName.contains(SARCHESHMEH)) {
			return cleanName.replace(SARCHESHMEH, "").trim();
		}
		return cleanName;
	}

	public static String getCleanName(GoodsModel goodsModel) {
		if (goodsModel == null) {
			return "";
		}
		return goodsModel.getName()
				.replace(goodsModel.getImeCommoditySymbol() != null ? goodsModel.getImeCommoditySymbol() : "", "")
				.replace("_", "")
				.trim();
	}

	public static String extractLotNumber(String rawDescription, OfferTextProcess offerTextProcess) {
		String lot = offerTextProcess.extractLotNumber(rawDescription);
		return lot != null ? lot : DEFAULT_LOT_NUMBER;
	}

	public static String extractSelenium(String rawDescription, OfferTextProcess offerTextProcess) {
		String selenium = offerTextProcess.hasSelenium(rawDescription);
		return selenium != null ? selenium : EMPTY_STRING;
	}

	// ==================== MASTER MODEL BUILDERS ====================

	public static ProformaMasterModel buildPreciousMetalMasterModel(
			TradeExtractModel tradeExtract,
			IMETradeModel tradeModel,
			GoodsModel goodsModel,
			CustomerModel customerModel,
			GoodsBucketModel goodsBucketModel,
			Totals totals,
			Integer deadlineDays,
			Long tradeId,
			String contractDate,
			ProformaIssueType proformaIssueType,
			String settlementType) {

		return ProformaMasterModel.builder()
				.contractNo(Long.valueOf(tradeExtract.getContractNo()))
				.paymentCode(tradeExtract.getPaymentCode())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.cashPercentage(goodsBucketModel.getCashPercentage())
				.commissionPercentage(goodsBucketModel.getCommission())
				.deadlineDays(deadlineDays)
				.creditPercentage(TOTAL_PERCENTAGE.subtract(goodsBucketModel.getCashPercentage()))
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
				.proformaIssueType(proformaIssueType)
				.goodId(goodsModel.getId())
				.goodName(goodsModel.getDescription())
				.contractDate(contractDate)
				.tradeId(tradeId)
				.brokerId(Long.valueOf(tradeModel.getSellerBrokerCode()))
				.brokerName(tradeModel.getSellerBrokerPersianName())
				.brokerNationalCode(DEFAULT_PLACEHOLDER)
				.imeCommoditySymbol(tradeModel.getCommoditySymbol())
				.offerDescription(tradeModel.getOfferDescription())
				.settlementType(settlementType != null ? settlementType : SettlementType.UNKNOWN.name())
				.isProcessFinal(false)
				.isReversalProcessFinal(false)
				.build();
	}

	// ==================== DETAIL MODEL BUILDER ====================

	public static ProformaDetailModel buildProformaDetailModel(
			List<ProformaGoodItemModel> goodItems,
			Integer jalaliYear,
			SaleConditionModel saleConditionModel,
			Integer deadlineDays,
			String performaNo,
			Date performaDate,
			DetailTotals detailTotals,
			String settlementType,
			ProformaIssueType proformaIssueType,
			Date orderDate,
			String contractDate,
			ProformaReversalStatus reversalStatus,
			BigDecimal extraBillOfPercent,
			BigDecimal extraBillOfExchangeAmount) {

		int gaamcount = 0;
		if (proformaIssueType == ProformaIssueType.GAM_BONDS) {
			gaamcount = calculateGamCertificateCount(detailTotals.finalAmount());
		}
		return ProformaDetailModel.builder()
				.proformaGoodItemModels(goodItems)
				.jalaaliYear(jalaliYear)
				.storageDeadline(saleConditionModel.getStorageDeadline())
				.storageCost(saleConditionModel.getStorageCost())
				.creditExpirePeriod(saleConditionModel.getCreditExpirePeriod())
				.shippingDeadline(saleConditionModel.getShippingDeadline())
				.paymentDeferral(saleConditionModel.getPaymentDeferral())
				.deadlineDays(deadlineDays)
				.performaNo(performaNo)
				.performaDate(performaDate)
				.totalAmount(detailTotals.totalAmount())
				.finalPrice(detailTotals.finalAmount())
				.vatAmount(detailTotals.vatAmount())
				.saleType(SaleType.EXWORKS)
				.settlementType(settlementType)
				.proformaIssueType(proformaIssueType)
				.orderDate(orderDate)
				.contractDate(contractDate)
				.proformaReversalStatus(reversalStatus != null ? reversalStatus : ProformaReversalStatus.NORMAL)
				.extraBillOfPercent(extraBillOfPercent != null ? extraBillOfPercent : BigDecimal.ZERO)
				.extraBillOfExchangeAmount(extraBillOfExchangeAmount != null ? extraBillOfExchangeAmount : BigDecimal.ZERO)
				.gamCertificateCount(gaamcount)
				.build();
	}

	public static Integer calculateGamCertificateCount(BigDecimal finalAmount) {
		return finalAmount.divide(BigDecimal.valueOf(1_000_000), 0, RoundingMode.CEILING).intValue();
	}

	// ==================== CONVERSION METHODS ====================

	public static ProformaDetailDto toDetailDTO(ProformaDetailModel model) {
		if (model == null) {
			return new ProformaDetailDto();
		}
		ProformaDetailDto dto = new ProformaDetailDto();
		BeanUtils.copyProperties(model, dto);

		if (model.getProformaGoodItemModels() != null) {
			dto.setProformaGoodItemDtos(
					model.getProformaGoodItemModels().stream()
							.map(ProformaModelHelper::toGoodItemDTO)
							.collect(Collectors.toList())
			);
		}

		return dto;
	}

	public static List<ProformaDetailDto> toDetailDTOList(List<ProformaDetailModel> models) {
		if (models == null || models.isEmpty()) {
			return new ArrayList<>();
		}
		return models.stream()
				.map(ProformaModelHelper::toDetailDTO)
				.collect(Collectors.toList());
	}

	public static ProformaGoodItemDto toGoodItemDTO(ProformaGoodItemModel model) {
		if (model == null) {
			return new ProformaGoodItemDto();
		}
		ProformaGoodItemDto dto = new ProformaGoodItemDto();
		BeanUtils.copyProperties(model, dto);
		dto.setGoodsName(model.getGoodName());
		dto.setGoodsId(model.getGoodId());
		dto.setUnitName(UNIT_NAME);
		return dto;
	}

	public static List<ProformaGoodItemDto> toGoodItemDTOList(List<ProformaGoodItemModel> models) {
		if (models == null || models.isEmpty()) {
			return new ArrayList<>();
		}
		return models.stream()
				.map(ProformaModelHelper::toGoodItemDTO)
				.collect(Collectors.toList());
	}

	public static ProformaResponseDto buildProformaResponse(
			ProformaMasterModel masterModel,
			List<ProformaDetailModel> detailModels,
			ProformaMasterMapper masterMapper) {

		if (masterModel == null || masterMapper == null) {
			return new ProformaResponseDto();
		}

		ProformaResponseDto response = new ProformaResponseDto();
		response.setMasterDTO(masterMapper.toDTO(masterModel));

		if (detailModels != null && !detailModels.isEmpty() &&
				detailModels.get(0).getProformaGoodItemModels() != null &&
				!detailModels.get(0).getProformaGoodItemModels().isEmpty()) {
			ProformaGoodItemModel firstGoodItem = detailModels.get(0).getProformaGoodItemModels().get(0);
			response.getMasterDTO().setGoodId(firstGoodItem.getGoodId());
			response.getMasterDTO().setGoodName(firstGoodItem.getGoodName());
		}

		response.setDetailDtoList(toDetailDTOList(detailModels));
		return response;
	}

	public static ProformaResponseDto buildProformaResponseFromMaster(
			ProformaMasterModel master,
			ProformaMasterMapper masterMapper) {
		if (master == null || masterMapper == null) {
			return new ProformaResponseDto();
		}
		return buildProformaResponse(
				master,
				master.getProformaDetailModelLists(),
				masterMapper
		);
	}

	// ==================== FILTER METHODS ====================

	public static List<ProformaDetailModel> filterDetailsByStatus(
			List<ProformaDetailModel> details,
			ProformaReversalStatus status) {

		if (details == null || details.isEmpty()) {
			return new ArrayList<>();
		}
		return details.stream()
				.filter(detail -> detail.getProformaReversalStatus() == status)
				.collect(Collectors.toList());
	}

	public static List<String> extractProformaNumbersByStatus(
			List<ProformaDetailModel> details,
			ProformaReversalStatus status) {

		if (details == null || details.isEmpty()) {
			return new ArrayList<>();
		}
		return details.stream()
				.filter(detail -> detail.getProformaReversalStatus() == status)
				.map(ProformaDetailModel::getPerformaNo)
				.distinct()
				.collect(Collectors.toList());
	}

	public static List<ProformaDetailModel> getActiveDetails(List<ProformaDetailModel> details) {
		if (details == null || details.isEmpty()) {
			return new ArrayList<>();
		}
		return details.stream()
				.filter(detail -> detail.getProformaReversalStatus() == ProformaReversalStatus.NORMAL)
				.collect(Collectors.toList());
	}

	/**
	 * فیلتر کردن DetailDtoهای فعال (NORMAL)
	 */
	public static List<ProformaDetailDto> getActiveDetailDTOs(List<ProformaDetailDto> details) {
		if (details == null || details.isEmpty()) {
			return new ArrayList<>();
		}
		return details.stream()
				.filter(detail -> detail.getProformaReversalStatus() == ProformaReversalStatus.NORMAL)
				.collect(Collectors.toList());
	}

	// ==================== VALIDATION HELPERS ====================

	public static boolean hasDetails(List<ProformaDetailModel> details) {
		return details != null && !details.isEmpty();
	}

	public static boolean hasGoodItems(ProformaDetailModel detail) {
		return detail != null &&
				detail.getProformaGoodItemModels() != null &&
				!detail.getProformaGoodItemModels().isEmpty();
	}

	// ==================== MASTER VALIDATION ====================

	public static boolean isMasterReversable(ProformaMasterModel master) {
		if (master == null) {
			return false;
		}
		return master.getIsProcessFinal() != null &&
				master.getIsProcessFinal() &&
				!master.getIsReversalProcessFinal();
	}


	public static boolean hasReversalProcess(ProformaMasterModel master) {
		return master != null &&
				master.getReversalProcessId() != null &&
				!master.getReversalProcessId().equals(DEFAULT_PLACEHOLDER);
	}


	/**
	 * جمع‌آوری مقادیر از لیست ProformaDetailModel ها و گرد کردن با RoundingMode.HALF_UP
	 * اگر اعشار 0.5 یا بیشتر باشد، بالا گرد می‌شود
	 * اگر اعشار 0.4 یا کمتر باشد، پایین گرد می‌شود
	 */
	private static BigDecimal sum(List<ProformaDetailModel> detailModels,
	                              Function<ProformaGoodItemModel, BigDecimal> extractor) {
		if (detailModels == null || detailModels.isEmpty()) {
			return BigDecimal.ZERO;
		}
		BigDecimal result = detailModels.stream()
				.filter(detail -> detail.getProformaGoodItemModels() != null)
				.flatMap(detail -> detail.getProformaGoodItemModels().stream())
				.map(extractor)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// گرد کردن با HALF_UP به عدد صحیح (0 رقم اعشار)
		return result.setScale(0, RoundingMode.HALF_UP);
	}

	/**
	 * جمع‌آوری مقادیر از لیست ProformaGoodItemModel ها و گرد کردن با RoundingMode.HALF_UP
	 * اگر اعشار 0.5 یا بیشتر باشد، بالا گرد می‌شود
	 * اگر اعشار 0.4 یا کمتر باشد، پایین گرد می‌شود
	 */
	private static BigDecimal sumItems(List<ProformaGoodItemModel> goodItems,
	                                   Function<ProformaGoodItemModel, BigDecimal> extractor) {
		if (goodItems == null || goodItems.isEmpty()) {
			return BigDecimal.ZERO;
		}
		BigDecimal result = goodItems.stream()
				.map(extractor)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return result.setScale(0, RoundingMode.HALF_UP);
	}

	// ==================== SAVE METHODS ====================

	/**
	 * ذخیره Detail و GoodItem‌ها
	 */
	public static void saveDetailAndGoodItems(
			List<ProformaDetailModel> detailModels,
			Long masterId,
			ProformaDetailRepository detailRepository,
			ProformaGoodItemRepository goodItemRepository) {

		detailModels.forEach(detailModel -> {
			detailModel.setProformaMasterId(masterId);
			detailRepository.save(detailModel);

			detailModel.getProformaGoodItemModels().stream()
					.distinct()
					.forEach(goodItem -> {
						goodItem.setProformaDetailId(detailModel.getId());
						goodItemRepository.save(goodItem);
					});
		});
	}

	/**
	 * محاسبه مبلغ اضافی برای انواع خاص پیش فاکتور
	 */
	public static BigDecimal calculateExtraAmount(
			ProformaIssueType issueType,
			BigDecimal totalPrice,
			SaleConditionModel saleConditionModel) {

		BigDecimal extraPercent = BigDecimal.ZERO;

		if (issueType == ProformaIssueType.GAM_BONDS) {
			extraPercent = saleConditionModel.getExtraGamCertificatePercent();
		} else if (issueType == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
			extraPercent = saleConditionModel.getExtraBillOfExchangePercent();
		}

		if (extraPercent != null && extraPercent.compareTo(BigDecimal.ZERO) > 0) {
			return totalPrice.multiply(extraPercent).divide(BigDecimal.valueOf(100), 0, RoundingMode.UP);
		}

		return BigDecimal.ZERO;
	}


	public record Totals(BigDecimal totalCashAmount,
	                     BigDecimal totalQuantity,
	                     BigDecimal totalCreditAmount,
	                     BigDecimal totalVatAmount,
	                     BigDecimal totalFinalAmount) {
	}

	public record DetailTotals(BigDecimal totalAmount,
	                           BigDecimal vatAmount,
	                           BigDecimal finalAmount,
	                           BigDecimal cashAmount,
	                           BigDecimal creditAmount,
	                           BigDecimal quantity) {
	}

	public record CashGoodItemCalculation(
			Long goodId,
			String goodName,
			Long unitId,
			BigDecimal vatPercent,
			BigDecimal quantity,
			BigDecimal creditQuantity,
			BigDecimal unitPriceCredit,
			BigDecimal unitPriceCash,
			BigDecimal unitPrice,
			BigDecimal creditAmount,
			BigDecimal cashAmount,
			BigDecimal vatCashAmount,
			BigDecimal vatCreditAmount,
			BigDecimal vatAmount,
			BigDecimal interestPercent,
			BigDecimal totalAmount,
			BigDecimal finalAmount,
			BigDecimal netQuantity,
			String lotNumber,
			BigDecimal creditPercentage,
			BigDecimal cashPercentage
	) {
	}

	public record PreciousGoodItemCalculation(
			Long goodId,
			String goodName,
			Long unitId,
			BigDecimal vatPercent,
			BigDecimal quantity,
			BigDecimal creditQuantity,
			BigDecimal unitPriceCredit,
			BigDecimal unitPriceCash,
			BigDecimal unitPrice,
			BigDecimal creditAmount,
			BigDecimal cashAmount,
			BigDecimal vatCashAmount,
			BigDecimal vatCreditAmount,
			BigDecimal vatAmount,
			BigDecimal interestPercent,
			BigDecimal totalAmount,
			BigDecimal finalAmount,
			BigDecimal netQuantity,
			String lotNumber,
			BigDecimal creditPercentage,
			BigDecimal cashPercentage,
			BigDecimal cashQuantity
	) {
	}

	public record PreciousMetalLCCalculation(
			Long goodId,
			String goodName,
			Long unitId,
			BigDecimal vatPercent,
			BigDecimal quantity,
			BigDecimal creditQuantity,
			BigDecimal unitPriceCredit,
			BigDecimal unitPriceCash,
			BigDecimal unitPrice,
			BigDecimal creditAmount,
			BigDecimal cashAmount,
			BigDecimal vatCashAmount,
			BigDecimal vatCreditAmount,
			BigDecimal vatAmount,
			BigDecimal interestPercent,
			BigDecimal totalAmount,
			BigDecimal finalAmount,
			BigDecimal netQuantity,
			String lotNumber,
			BigDecimal creditPercentage,
			BigDecimal cashPercentage,
			BigDecimal cashQuantity
	) {
	}

	public record CashCreditPercentages(BigDecimal cashPercentage,
	                                    BigDecimal creditPercentage) {
	}
}