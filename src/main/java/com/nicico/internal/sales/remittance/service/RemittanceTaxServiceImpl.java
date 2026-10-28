package com.nicico.internal.sales.remittance.service;

import com.nicico.internal.sales.deposit.repository.CashDepositRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.proforma.enums.DepositSlipType;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.service.ProformaContractService;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.remittance.model.RemittanceProformaDataProviderModel;
import com.nicico.internal.sales.remittance.repository.RemittanceProformaDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceSulfuricAcidDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceTradeDataProviderRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemittanceTaxServiceImpl implements RemittanceTaxService {
	//	private static final double TAX_10_PERCENT = 0.1;
//	private static final double MULTIPLIER_1_1 = 1.1;
	private static final double PERCENT_100 = 100.0;
	private static final String SETTLEMENT_TYPE_UNKNOWN = "نامشخص";
	private static final String SETTLEMENT_TYPE_CASH = "نقدی";
	private static final String SETTLEMENT_TYPE_CASH_CREDIT = "نقدی/اعتباری";
	private static final String MSG_OFFER_NOT_FOUND = "آگهی عرضه پیدا نشد";
	private final GoodsRepository goodsRepository;
	private final PreciousMetalRepository preciousMetalRepository;
	private final RemittanceProformaDataProviderRepository remittanceProformaDataProviderRepository;
	private final RemittanceTradeDataProviderRepository tradeDataProviderRepository;
	private final RemittanceSulfuricAcidDataProviderRepository acidDataProviderRepository;
	private final CashDepositRepository cashDepositRepository;
	private final ProformaContractService proformaContractService;


	@Override
	public Double calculateTax(RemittanceCreateDto dto) {
		if (dto.getSourceType() == RemittanceSourceType.PROFORMA) {
			return calculateProformaTax(dto);
		} else {
			return calculateTradeTax(dto);
		}
	}

	private double getVatPercent(RemittanceCreateDto dto) {
		if (dto.getSourceType() == RemittanceSourceType.PROFORMA) {
			var proformaData = remittanceProformaDataProviderRepository
					.findFirstByIdOrderByIdDesc(dto.getTradeId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_OFFER_NOT_FOUND));
			return (proformaContractService.getVat(DateUtility.extractPersianDateParts(proformaData.getContractDate())[0]).doubleValue()) / 100;
		} else if (dto.getSourceType() == RemittanceSourceType.TRADE) {
			var proformaData = tradeDataProviderRepository.findFirstByIdOrderByContractDateDesc(dto.getTradeId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_OFFER_NOT_FOUND));
			return (proformaContractService.getVat(DateUtility.extractPersianDateParts(proformaData.getContractDate())[0]).doubleValue()) / 100;
		}

		var proformaData = acidDataProviderRepository.findFirstByIdOrderByContractDateDesc(dto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_OFFER_NOT_FOUND));
		return (proformaContractService.getVat(DateUtility.extractPersianDateParts(proformaData.getContractDate())[0]).doubleValue()) / 100;
	}

	private Double calculateProformaTax(RemittanceCreateDto dto) {
		double vatPercent = getVatPercent(dto);
		var proformaData = remittanceProformaDataProviderRepository
				.findFirstByIdOrderByIdDesc(dto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_OFFER_NOT_FOUND));
		validateSettlementType(proformaData.getSettlementTypeDesc());
		var goods = goodsRepository.findById(proformaData.getGoodId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("کالا پیدا نشد"));
		boolean isPreciousMetal = preciousMetalRepository.existsById(goods.getId());
		int totalQuantity = proformaData.getUnitCount();
		long cashUnitPrice = proformaData.getUnitPrice().longValue();
		double cashPercentage = PERCENT_100 - proformaData.getCreditPercentage().doubleValue();
		var proformaType = proformaData.getProformaIssueType();
		boolean isCash = isCashSettlement(proformaData);
		boolean isCashCredit = isCashCreditSettlement(proformaData);
		if (proformaType == ProformaIssueType.FROM_CREDIT_FACILITIES) {
			return calculateCreditFacilitiesCashProforma(cashUnitPrice, totalQuantity, cashPercentage, isPreciousMetal, dto);
		}
		if (proformaType == ProformaIssueType.LETTER_OF_CREDIT_OPENING) {
			return calculateCreditOpeningCashProforma(cashUnitPrice, totalQuantity, cashPercentage, vatPercent);
		}
		if (proformaType == ProformaIssueType.GAM_BONDS) {
			return calculateGAAMCashProforma(cashUnitPrice, totalQuantity, cashPercentage, vatPercent);
		}
		if (proformaType == ProformaIssueType.CASH_DEPOSIT_RECEIPT) {
			return calculateCashDeposit(proformaData.getProformaDetailId());
		}
		if (proformaType == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
			return calculateExtraBillOfExchange(cashUnitPrice, totalQuantity, cashPercentage, vatPercent);
		}
		return 0.0;
	}

	private Double calculateTradeTax(RemittanceCreateDto dto) {
		double result = 0.0;
		var trade = tradeDataProviderRepository.findFirstByIdOrderByContractDateDesc(dto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_OFFER_NOT_FOUND));
		var goods = goodsRepository.findByImeCommoditySymbol(trade.getImeCommoditySymbol())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("کالا پیدا نشد"));
		boolean isPreciousMetal = preciousMetalRepository.existsById(goods.getId());

		if (isPreciousMetal) {
			double netAmount = dto.getNetWeight() * trade.getUnitPrice();
			double totalWithTax = netAmount * (getVatPercent(dto) + 1);

			result = totalWithTax - netAmount;
		} else {
			result = trade.getUnitPrice() * trade.getUnitCount() * (getVatPercent(dto));
		}
		return result;
	}

	private void validateSettlementType(String settlementTypeDesc) {
		if (SETTLEMENT_TYPE_UNKNOWN.equals(settlementTypeDesc)) {
			throw new InternalSaleCustomException.ValidationException("هنوز وضعیت تسویه حساب این درخواست مشخص نیست");
		}
	}

	private boolean isCashSettlement(RemittanceProformaDataProviderModel proforma) {
		String normalizedDesc = proforma.getSettlementTypeDesc().replaceAll(" ", "");
		return normalizedDesc.equalsIgnoreCase(SETTLEMENT_TYPE_CASH);
	}

	private boolean isCashCreditSettlement(RemittanceProformaDataProviderModel proforma) {
		String normalizedDesc = proforma.getSettlementTypeDesc().replaceAll(" ", "");
		return normalizedDesc.contains("اعتباری");
	}

	private Double calculateCreditOpeningCashProforma(long unitPrice, double quantity, double cashPercentage, double vatPercent) {
		return unitPrice * quantity * (cashPercentage / PERCENT_100) * vatPercent;
	}

	private Double calculateGAAMCashProforma(long unitPrice, double quantity, double cashPercentage, double vatPercent) {
		return unitPrice * quantity * (cashPercentage / PERCENT_100) * vatPercent;
	}

	private Double calculateCashDeposit(long detailId) {
		var model = cashDepositRepository.findByProformaDetailId(detailId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_OFFER_NOT_FOUND));
		if (model.getDepositSlipType() == DepositSlipType.WITHOUT_TAX) {
			return model.getDepositSlipWithTax().doubleValue() - model.getDepositSlipWithoutTax().doubleValue();
		} else {
			return 0.0;
		}
	}

	private Double calculateExtraBillOfExchange(long unitPrice, double quantity, double cashPercentage, double vatPercent) {
		return unitPrice * quantity * (cashPercentage / PERCENT_100) * vatPercent;
	}


	private Double calculateCreditFacilitiesCashProforma(long unitPrice, double quantity,
	                                                     double cashPercentage, boolean isPreciousMetal,
	                                                     RemittanceCreateDto dto) {
		if (isPreciousMetal) {
			double totalWithTax = dto.getNetWeight() * unitPrice * getVatPercent(dto) + 1;
			double cashPortion = quantity * unitPrice * cashPercentage / PERCENT_100;
			return totalWithTax - cashPortion;
		}
		double totalWithTax = unitPrice * quantity * getVatPercent(dto) + 1;
		double cashPortion = quantity * cashPercentage / PERCENT_100 * unitPrice;
		return totalWithTax - cashPortion;
	}

}
