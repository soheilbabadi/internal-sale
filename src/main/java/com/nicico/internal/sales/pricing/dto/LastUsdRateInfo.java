package com.nicico.internal.sales.pricing.dto;

import java.math.BigDecimal;


public record LastUsdRateInfo(
		String persianShortDate,
		BigDecimal usdBuyRate,
		BigDecimal usdSellRate,
		BigDecimal averageRate
) {
}

