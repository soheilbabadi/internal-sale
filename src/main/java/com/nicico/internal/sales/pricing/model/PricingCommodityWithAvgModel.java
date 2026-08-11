package com.nicico.internal.sales.pricing.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "نرخ های کالا با میانگین های محاسبه شده (ساب سلکت فقط خواندنی)")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Subselect("SELECT " +
		"   ID, " +
		"   DAY_OF_WEEK, " +
		"   PERSIAN_SHORT_DATE, " +
		"   SHORT_DATE, " +
		"   N_USD_PER_MT_CU, " +
		"   N_USD_PER_OUNCE_GOLD, " +
		"   N_USD_PER_OUNCE_SILVER, " +
		"   " +
		"   (NVL(N_USD_PER_LB_SELENIUM_BID, 0) + NVL(N_USD_PER_LB_SELENIUM_ASK, 0)) / 2 AS SELENIUM_AVG, " +
		"   (NVL(N_USD_PER_LB_MOLYBDENUM_LOW, 0) + NVL(N_USD_PER_LB_MOLYBDENUM_HIGH, 0)) / 2 AS MOLYBDENUM_AVG, " +
		"   (NVL(N_USD_PER_LB_MOLYBDENUM_AM, 0) + NVL(N_USD_PER_LB_MOLYBDENUM_PM, 0)) / 2 AS MOLYBDENUM_AM_PM_AVG, " +
		"   (NVL(N_USD_PER_TO_PLATINUM_AM, 0) + NVL(N_USD_PER_TO_PLATINUM_PM, 0)) / 2 AS PLATINUM_AVG, " +
		"   (NVL(N_USD_PER_TO_PALLADIUM_AM, 0) + NVL(N_USD_PER_TO_PALLADIUM_PM, 0)) / 2 AS PALLADIUM_AVG, " +
		"   " +
		"   N_USD_PER_LB_SELENIUM_BID, " +
		"   N_USD_PER_LB_SELENIUM_ASK, " +
		"   N_USD_PER_LB_MOLYBDENUM_LOW, " +
		"   N_USD_PER_LB_MOLYBDENUM_HIGH, " +
		"   N_USD_PER_TO_PLATINUM_AM, " +
		"   N_USD_PER_TO_PLATINUM_PM, " +
		"   N_USD_PER_TO_PALLADIUM_AM, " +
		"   N_USD_PER_TO_PALLADIUM_PM, " +
		"   N_USD_PER_LB_MOLYBDENUM_AM, " +
		"   N_USD_PER_LB_MOLYBDENUM_PM " +
		"FROM T_INS_PRICE_COMMODITY")
@Synchronize("T_INS_PRICE_COMMODITY")
public class PricingCommodityWithAvgModel {

	@Id
	private Long id;

	@Schema(description = "روز هفته")
	@Column(name = "DAY_OF_WEEK")
	private String dayOfWeek;

	@Schema(description = "تاریخ شمسی ")
	@Column(name = "PERSIAN_SHORT_DATE")
	private String persianShortDate;

	@Schema(description = "تاریخ میلادی")
	@Column(name = "SHORT_DATE")
	private Date shortDate;

	@Schema(description = "قیمت مس (USD/MT)")
	@Column(name = "N_USD_PER_MT_CU", precision = 19, scale = 3)
	private BigDecimal usdPerMtCu;

	@Schema(description = "قیمت طلا (USD/اونس)")
	@Column(name = "N_USD_PER_OUNCE_GOLD", precision = 19, scale = 3)
	private BigDecimal usdPerOunceGold;

	@Schema(description = "قیمت نقره (USD/اونس)")
	@Column(name = "N_USD_PER_OUNCE_SILVER", precision = 19, scale = 3)
	private BigDecimal usdPerOunceSilver;

	@Schema(description = "میانگین قیمت سلنیوم (تقاضا + عرضه) / 2")
	@Column(name = "SELENIUM_AVG", precision = 19, scale = 3)
	private BigDecimal seleniumAvg;

	@Schema(description = "میانگین قیمت مولیبدن (کمترین + بیشترین) / 2")
	@Column(name = "MOLYBDENUM_AVG", precision = 19, scale = 3)
	private BigDecimal molybdenumAvg;

	@Schema(description = "میانگین قیمت مولیبدن (صبح + عصر) / 2")
	@Column(name = "MOLYBDENUM_AM_PM_AVG", precision = 19, scale = 3)
	private BigDecimal molybdenumAmPmAvg;

	@Schema(description = "میانگین قیمت پلاتین (صبح + عصر) / 2")
	@Column(name = "PLATINUM_AVG", precision = 19, scale = 3)
	private BigDecimal platinumAvg;

	@Schema(description = "میانگین قیمت پالادیوم (صبح + عصر) / 2")
	@Column(name = "PALLADIUM_AVG", precision = 19, scale = 3)
	private BigDecimal palladiumAvg;

	@Schema(description = "قیمت سلنیوم تقاضا (USD/پوند)")
	@Column(name = "N_USD_PER_LB_SELENIUM_BID", precision = 19, scale = 3)
	private BigDecimal seleniumBid;

	@Schema(description = "قیمت سلنیوم عرضه (USD/پوند)")
	@Column(name = "N_USD_PER_LB_SELENIUM_ASK", precision = 19, scale = 3)
	private BigDecimal seleniumAsk;

	@Schema(description = "قیمت مولیبدن کمترین (USD/پوند)")
	@Column(name = "N_USD_PER_LB_MOLYBDENUM_LOW", precision = 19, scale = 3)
	private BigDecimal molybdenumLow;

	@Schema(description = "قیمت مولیبدن بیشترین (USD/پوند)")
	@Column(name = "N_USD_PER_LB_MOLYBDENUM_HIGH", precision = 19, scale = 3)
	private BigDecimal molybdenumHigh;

	@Schema(description = "قیمت پلاتین صبح (USD/اونس تروی)")
	@Column(name = "N_USD_PER_TO_PLATINUM_AM", precision = 19, scale = 3)
	private BigDecimal platinumAm;

	@Schema(description = "قیمت پلاتین عصر (USD/اونس تروی)")
	@Column(name = "N_USD_PER_TO_PLATINUM_PM", precision = 19, scale = 3)
	private BigDecimal platinumPm;

	@Schema(description = "قیمت پالادیوم صبح (USD/اونس تروی)")
	@Column(name = "N_USD_PER_TO_PALLADIUM_AM", precision = 19, scale = 3)
	private BigDecimal palladiumAm;

	@Schema(description = "قیمت پالادیوم عصر (USD/اونس تروی)")
	@Column(name = "N_USD_PER_TO_PALLADIUM_PM", precision = 19, scale = 3)
	private BigDecimal palladiumPm;

	@Schema(description = "قیمت مولیبدن صبح (USD/پوند)")
	@Column(name = "N_USD_PER_LB_MOLYBDENUM_AM", precision = 19, scale = 3)
	private BigDecimal molybdenumAm;

	@Schema(description = "قیمت مولیبدن عصر (USD/پوند)")
	@Column(name = "N_USD_PER_LB_MOLYBDENUM_PM", precision = 19, scale = 3)
	private BigDecimal molybdenumPm;
}