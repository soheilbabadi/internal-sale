package com.nicico.internal.sales.pricing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PricingCommodityDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 9078828953143376738L;

	private Long id;


	@Schema(description = "روز هفته")
	private String dayOfWeek;

	@Schema(description = "تاریخ شمسی ")
	private String persianShortDate;


	@Schema(description = "تاریخ میلادی")
	private Date shortDate;

	@Schema(description = "قیمت مس (USD/MT)")
	private BigDecimal usdPerMtCu;

	@Schema(description = "قیمت طلا (USD/اونس)")
	private BigDecimal usdPerOunceGold;

	@Schema(description = "قیمت نقره (USD/اونس)")
	private BigDecimal usdPerOunceSilver;

	@Schema(description = "قیمت سلنیوم تقاضا (USD/پوند)")
	private BigDecimal seleniumBid;

	@Schema(description = "قیمت سلنیوم عرضه (USD/پوند)")
	private BigDecimal seleniumAsk;

	@Schema(description = "قیمت مولیبدن کمترین (USD/پوند)")
	private BigDecimal molybdenumLow;

	@Schema(description = "قیمت مولیبدن بیشترین (USD/پوند)")
	private BigDecimal molybdenumHigh;

	@Schema(description = "قیمت پلاتین صبح (USD/اونس تروی)")
	private BigDecimal platinumAm;

	@Schema(description = "قیمت پلاتین عصر (USD/اونس تروی)")
	private BigDecimal platinumPm;

	@Schema(description = "قیمت پالادیوم صبح (USD/اونس تروی)")
	private BigDecimal palladiumAm;

	@Schema(description = "قیمت پالادیوم عصر (USD/اونس تروی)")
	private BigDecimal palladiumPm;

	@Schema(description = "قیمت مولیبدن صبح (USD/پوند)")
	private BigDecimal molybdenumAm;

	@Schema(description = "قیمت مولیبدن عصر (USD/پوند)")
	private BigDecimal molybdenumPm;


	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PricingCommodityDto.Info")
	public static class Info extends PricingCommodityDto {
		@Serial
		private static final long serialVersionUID = -1559891514322818599L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PricingCommodityDto.Create")
	public static class Create extends PricingCommodityDto {
		@Serial
		private static final long serialVersionUID = -155989351432281859L;
	}
}
