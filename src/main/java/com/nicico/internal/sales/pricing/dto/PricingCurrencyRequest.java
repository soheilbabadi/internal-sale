package com.nicico.internal.sales.pricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PricingCurrencyRequest {

	@Schema(description = "تاریخ شمسی  (مثال: 1403/01/29)")
	private String persianShortDate;

	@Schema(description = "نرخ USD/IRR(خرید)")
	private BigDecimal usdIrrBuy;

	@Schema(description = "نرخ USD/IRR(فروش)")
	private BigDecimal usdIrrSell;

	@Schema(description = "نرخ EUR/IRR(خرید)")
	private BigDecimal eurIrrBuy;

	@Schema(description = "نرخ EUR/IRR(فروش)")
	private BigDecimal eurIrrSell;


}
