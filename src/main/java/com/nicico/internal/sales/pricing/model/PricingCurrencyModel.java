package com.nicico.internal.sales.pricing.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "نرخ های روزانه تبدیل ارز - دلار و یورو")

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "T_INS_PRICE_CURRENCY")
public class PricingCurrencyModel extends BaseClassModel {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ins_price_currency")
	@SequenceGenerator(name = "seq_ins_price_currency", sequenceName = "seq_ins_price_currency", allocationSize = 1)
	private long id;


	@Schema(description = "تاریخ شمسی  (مثال: 1403/01/29)")
	@Column(name = "PERSIAN_SHORT_DATE", nullable = false, length = 20, unique = true)
	private String persianShortDate;

	@Schema(description = "تاریخ میلادی")
	@Column(name = "SHORT_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date shortDate;

	@Schema(description = "روز هفته")
	@Column(name = "DAY_OF_WEEK", nullable = false, length = 20)
	private String dayOfWeek;

	@Schema(description = "نرخ USD/IRR(خرید)")
	@Column(name = "N_USD_IRR_BUY", precision = 20, scale = 4)
	private BigDecimal usdIrrBuy;

	@Schema(description = "نرخ USD/IRR(فروش)")
	@Column(name = "N_USD_IRR_SELL", precision = 20, scale = 4)
	private BigDecimal usdIrrSell;

	@Schema(description = "نرخ EUR/IRR(خرید)")
	@Column(name = "N_EUR_IRR_BUY", precision = 20, scale = 4)
	private BigDecimal eurIrrBuy;

	@Schema(description = "نرخ EUR/IRR(فروش)")
	@Column(name = "N_EUR_IRR_SELL", precision = 20, scale = 4)
	private BigDecimal eurIrrSell;

	@Column(name = "C_RATE_TYPE", nullable = false, length = 100)
	private String rateType;
}