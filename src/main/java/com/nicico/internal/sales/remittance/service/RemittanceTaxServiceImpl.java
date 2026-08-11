package com.nicico.internal.sales.remittance.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.remittance.model.RemittanceProformaDataProviderModel;
import com.nicico.internal.sales.remittance.model.RemittanceTradeDataProviderModel;
import com.nicico.internal.sales.remittance.repository.RemittanceProformaDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceTradeDataProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemittanceTaxServiceImpl implements RemittanceTaxService {
	private static final double TAX_10_PERCENT = 0.1;
	private static final double MULTIPLIER_1_1 = 1.1;
	private static final double PERCENT_100 = 100.0;
	private static final String SETTLEMENT_TYPE_UNKNOWN = "نامشخص";
	private static final String SETTLEMENT_TYPE_CASH = "نقدی";
	private static final String SETTLEMENT_TYPE_CASH_CREDIT = "نقدی/اعتباری";
	private final GoodsRepository goodsRepository;
	private final PreciousMetalRepository preciousMetalRepository;
	private final RemittanceProformaDataProviderRepository remittanceProformaDataProviderRepository;
	private final RemittanceTradeDataProviderRepository remittanceTradeDataProviderRepository;

	@Override
	public Double calculateTax(RemittanceCreateDto dto) {
		if (dto.getSourceType() == RemittanceSourceType.PROFORMA) {
			return calculateProformaTax(dto);
		} else {
			return calculateTradeTax(dto);
		}
	}

	private Double calculateProformaTax(RemittanceCreateDto dto) {
		var proformaData = remittanceProformaDataProviderRepository
				.findFirstByIdOrderByIdDesc(dto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("معامله پیدا نشد"));
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
		if (proformaType == ProformaIssueType.FROM_CREDIT_FACILITIES && isCash) {
			return calculateCreditFacilitiesCashProforma(cashUnitPrice, totalQuantity, cashPercentage, isPreciousMetal, dto);
		}
		if (proformaType == ProformaIssueType.LETTER_OF_CREDIT_OPENING && isCashCredit) {
			return calculateCreditOpeningCashProforma(cashUnitPrice, totalQuantity, cashPercentage);
		}
		if (proformaType == ProformaIssueType.GAM_BONDS && isCashCredit) {
			return calculateGAAMCashProforma(cashUnitPrice, totalQuantity, cashPercentage);
		}
		return 0.0;
	}

	private Double calculateTradeTax(RemittanceCreateDto dto) {
		var trade = remittanceTradeDataProviderRepository.findFirstByIdOrderByContractDateDesc(dto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("معامله پیدا نشد"));
		var goods = goodsRepository.findById(trade.getGoodId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("کالا پیدا نشد"));
		boolean isPreciousMetal = preciousMetalRepository.existsById(goods.getId());
		return calculateCashCash(trade, isPreciousMetal, dto);
	}

	private void validateSettlementType(String settlementTypeDesc) {
		if (SETTLEMENT_TYPE_UNKNOWN.equals(settlementTypeDesc)) {
			throw new InternalSaleCustomException.ValidationException(
					"هنوز وضعیت تسویه حساب این درخواست مشخص نیست");
		}
	}


	private boolean isCashSettlement(RemittanceProformaDataProviderModel proforma) {
		String normalizedDesc = proforma.getSettlementTypeDesc().replaceAll(" ", "");
		return normalizedDesc.equalsIgnoreCase(SETTLEMENT_TYPE_CASH);
	}

	private boolean isCashCreditSettlement(RemittanceProformaDataProviderModel proforma) {
		String normalizedDesc = proforma.getSettlementTypeDesc().replaceAll(" ", "");
		return normalizedDesc.equalsIgnoreCase(SETTLEMENT_TYPE_CASH_CREDIT);
	}

	private Double calculateCreditOpeningCashProforma(long unitPrice, double quantity, double cashPercentage) {
		return unitPrice * quantity * (cashPercentage / PERCENT_100) * TAX_10_PERCENT;
	}

	private Double calculateGAAMCashProforma(long unitPrice, double quantity, double cashPercentage) {
		return unitPrice * quantity * (cashPercentage / PERCENT_100) * TAX_10_PERCENT;
	}

	private Double calculateCreditFacilitiesCashProforma(long unitPrice, double quantity,
	                                                     double cashPercentage, boolean isPreciousMetal,
	                                                     RemittanceCreateDto dto) {
		if (isPreciousMetal) {
			double totalWithTax = dto.getNetWeight() * unitPrice * MULTIPLIER_1_1;
			double cashPortion = quantity * unitPrice * cashPercentage / PERCENT_100;
			return totalWithTax - cashPortion;
		}
		double totalWithTax = unitPrice * quantity * MULTIPLIER_1_1;
		double cashPortion = quantity * cashPercentage / PERCENT_100 * unitPrice;
		return totalWithTax - cashPortion;
	}

	private Double calculateCashCash(RemittanceTradeDataProviderModel trade,
	                                 boolean isPreciousMetal,
	                                 RemittanceCreateDto dto) {
		if (isPreciousMetal) {
			double totalWithTax = dto.getNetWeight() * trade.getUnitPrice() * MULTIPLIER_1_1;
			double baseCost = trade.getUnitCount() * trade.getUnitPrice();
			return totalWithTax - baseCost;
		}
		return trade.getUnitPrice() * trade.getUnitCount() * TAX_10_PERCENT;
	}
}
