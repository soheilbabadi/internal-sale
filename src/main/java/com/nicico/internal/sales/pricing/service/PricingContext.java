package com.nicico.internal.sales.pricing.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * سیاق قیمت گذاری برای محاسبه قیمت های مختلف کالاهای مختلف
 * Pricing context for calculating prices of various commodities
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class PricingContext {
	// --- قیمت های فلزات بر حسب LME (London Metal Exchange) ---
	/**
	 * قیمت مس LME (هر تن متریک) / LME Copper price (per metric ton)
	 */
	private BigDecimal lmeCopperPrice;
	/**
	 * قیمت طلا / Gold price
	 */
	private BigDecimal goldPrice;
	/**
	 * قیمت نقره / Silver price
	 */
	private BigDecimal silverPrice;
	/**
	 * قیمت پلاتین / Platinum price
	 */
	private BigDecimal platinumPrice;
	/**
	 * قیمت پالادیوم / Palladium price
	 */
	private BigDecimal palladiumPrice;
	/**
	 * قیمت مولیبدن / Molybdenum price
	 */
	private BigDecimal molybdenumPrice;
	/**
	 * قیمت سلنیوم / Selenium price
	 */
	private BigDecimal seleniumPrice;
	// --- نرخ تبدیل ---
	/**
	 * نرخ تبدیل دلار به ریال / USD to IRR exchange rate
	 */
	private BigDecimal usdToIrr;
	/**
	 * تبدیل پوند به کیلوگرم / Pound to kilogram conversion
	 */
	private BigDecimal poundToKg;
	/**
	 * تبدیل تروی اونس به گرم / Troy ounce to gram conversion
	 */
	private BigDecimal troyOunceToGram;
	// --- درصد ترکیب و خلوص ---
	/**
	 * درصد طلا / Gold percentage/purity
	 */
	private BigDecimal auDecimal;
	/**
	 * درصد نقره / Silver percentage/purity
	 */
	private BigDecimal agDecimal;
	/**
	 * درصد مس / Copper percentage/purity
	 */
	private BigDecimal cuDecimal;
	/**
	 * درصد مولیبدن / Molybdenum percentage/purity
	 */
	private BigDecimal moDecimal;
	/**
	 * درصد سلنیوم / Selenium percentage/purity
	 */
	private BigDecimal seDecimal;
	/**
	 * درصد پلاتین / Platinum percentage/purity
	 */
	private BigDecimal ptDecimal;
	/**
	 * درصد پالادیوم / Palladium percentage/purity
	 */
	private BigDecimal pdDecimal;
	// --- درصدهای کاهشی ---
	/**
	 * درصد تخفیف / Discount percentage
	 */
	private BigDecimal discount;
	/**
	 * درصد روغن / Oil percentage
	 */
	private BigDecimal oilPercent;
	/**
	 * درصد آب / Water percentage
	 */
	private BigDecimal h2oPercent;
	// --- هزینه های تولید ---
	/**
	 * هزینه ذوب آوری / Smelting cost
	 */
	private BigDecimal smeltingCost;
	/**
	 * هزینه پالایش / Refining cost
	 */
	private BigDecimal refiningCost;

	/**
	 * تبدیل سیاق به نقشه متغیرهای برای استفاده در SpEL
	 * Convert context to variable map for SpEL evaluation
	 *
	 * @return Map of variables for SpEL expression evaluation
	 */
	public Map<String, Object> toVariableMap() {
		Map<String, Object> variables = new HashMap<>();
		// قیمت های فلزات
		variables.put("lmeCopperPrice", this.lmeCopperPrice);
		variables.put("goldPrice", this.goldPrice);
		variables.put("silverPrice", this.silverPrice);
		variables.put("platinumPrice", this.platinumPrice);
		variables.put("palladiumPrice", this.palladiumPrice);
		variables.put("molybdenumPrice", this.molybdenumPrice);
		variables.put("seleniumPrice", this.seleniumPrice);
		// نرخ تبدیل
		variables.put("usdToIrr", this.usdToIrr);
		variables.put("poundToKg", this.poundToKg);
		variables.put("troyOunceToGram", this.troyOunceToGram);
		// درصدهای ترکیب
		variables.put("auDecimal", this.auDecimal);
		variables.put("agDecimal", this.agDecimal);
		variables.put("cuDecimal", this.cuDecimal);
		variables.put("moDecimal", this.moDecimal);
		variables.put("seDecimal", this.seDecimal);
		variables.put("ptDecimal", this.ptDecimal);
		variables.put("pdDecimal", this.pdDecimal);
		// درصدهای کاهشی
		variables.put("discount", this.discount);
		variables.put("oilPercent", this.oilPercent);
		variables.put("h2oPercent", this.h2oPercent);
		// هزینه های تولید
		variables.put("smeltingCost", this.smeltingCost);
		variables.put("refiningCost", this.refiningCost);
		return variables;
	}
}
