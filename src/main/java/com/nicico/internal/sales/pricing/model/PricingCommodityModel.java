package com.nicico.internal.sales.pricing.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "نرخ های کالا")
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Accessors(chain = true)
@Table(
		name = "T_INS_PRICE_COMMODITY",
		uniqueConstraints = {
				@UniqueConstraint(name = "UK_INS_PRICE_COMMODITY_SHORT_DATE", columnNames = "SHORT_DATE"),
				@UniqueConstraint(name = "UK_INS_PRICE_COMMODITY_PERSIAN_SHORT_DATE", columnNames = "PERSIAN_SHORT_DATE")
		}
)

public class PricingCommodityModel extends BaseClassModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRICE_FORMULA")
	@SequenceGenerator(name = "SEQ_PRICE_FORMULA", sequenceName = "SEQ_PRICE_FORMULA", allocationSize = 1)
	private Long id;

	@Schema(description = "روز هفته")
	@Column(name = "DAY_OF_WEEK", nullable = false)
	private String dayOfWeek;

	@Schema(description = "تاریخ شمسی ")
	@Column(name = "PERSIAN_SHORT_DATE", nullable = false, unique = true)
	private String persianShortDate;

	@Schema(description = "تاریخ میلادی")
	@Column(name = "SHORT_DATE", nullable = false, unique = true)
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