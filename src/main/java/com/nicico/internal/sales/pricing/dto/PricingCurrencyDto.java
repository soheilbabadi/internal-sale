package com.nicico.internal.sales.pricing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class PricingCurrencyDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 731563241094728225L;

	private long id;

	@Schema(description = "تاریخ شمسی  (مثال: 1403/01/29)")
	@Column(name = "PERSIAN_SHORT_DATE", nullable = false, length = 20, unique = true)
	private String persianShortDate;

	@Schema(description = "تاریخ میلادی")
	@Temporal(TemporalType.TIMESTAMP)
	private Date shortDate;

	@Schema(description = "روز هفته")
	private String dayOfWeek;

	@Schema(description = "نرخ USD/IRR(خرید)")
	private BigDecimal usdIrrBuy;

	@Schema(description = "نرخ USD/IRR(فروش)")
	private BigDecimal usdIrrSell;

	@Schema(description = "نرخ EUR/IRR(خرید)")
	private BigDecimal eurIrrBuy;

	@Schema(description = "نرخ EUR/IRR(فروش)")
	private BigDecimal eurIrrSell;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PricingCurrencyDto.Info")
	public static class Info extends PricingCurrencyDto {
		@Serial
		private static final long serialVersionUID = -1559891514322818599L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PricingCurrencyDto.Create")
	public static class Create extends PricingCurrencyDto {
		@Serial
		private static final long serialVersionUID = -155989351432281859L;
	}
}
