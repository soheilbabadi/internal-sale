package com.nicico.internal.sales.pricing.service;


import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpelPricingEngine {

	private static final Map<String, String> DEFAULT_FORMULAS = Map.ofEntries(
			Map.entry("COPPER_CATHODE", "lmeCopperPrice * usdToIrr / 1000"), // قیمت هر کیلوگرم

			Map.entry("MOLYBDENUM_OXIDE",
					"(molybdenumPrice * poundToKg) * (moDecimal) * usdToIrr * 0.95"),

			Map.entry("MOLYBDENUM_SULFIDE",
					"((molybdenumPrice * poundToKg * usdToIrr * moDecimal * (1 - discount/100)) * (1 - oilPercent/100) * (1 - h2oPercent/100))"),

			Map.entry("ANODE_SLIME_WITH_SE",
					"((goldPrice * 1000 / troyOunceToGram * auDecimal * 0.89) + " +
							"(silverPrice * 1000 / troyOunceToGram * agDecimal * 0.89) + " +
							"(seleniumPrice * poundToKg * seDecimal * 0.45)) * usdToIrr"),

			Map.entry("ANODE_SLIME_WITHOUT_SE",
					"((goldPrice * 1000 / troyOunceToGram * auDecimal * 0.90) + " +
							"(silverPrice * 1000 / troyOunceToGram * agDecimal * 0.90) + " +
							"(lmeCopperPrice / 1000 * cuDecimal * 0.40) + " +
							"(platinumPrice * 1000 / troyOunceToGram * ptDecimal * 0.40) + " +
							"(palladiumPrice * 1000 / troyOunceToGram * pdDecimal * 0.80)) * usdToIrr"),

			Map.entry("COPPER_CONCENTRATE",
					"(((lmeCopperPrice * usdToIrr / 1000) * (cuDecimal - 0.01)) + " +
							"((goldPrice * 1000 / troyOunceToGram * (auDecimal - 0.000001)) * usdToIrr) + " +
							"((silverPrice * 1000 / troyOunceToGram * (agDecimal - 0.00003)) * usdToIrr) - " +
							"(smeltingCost + refiningCost))"),

			Map.entry("GRANULATED_SLAG",
					"(lmeCopperPrice * 0.005 * 0.35 * usdToIrr / 1000)")
	);
	private final ExpressionParser parser = new SpelExpressionParser();
	private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
	private final Map<String, String> customFormulas = new ConcurrentHashMap<>(DEFAULT_FORMULAS);

	public static BigDecimal round(BigDecimal value, int scale) {
		return value.setScale(scale, RoundingMode.HALF_UP);
	}

	public static BigDecimal max(BigDecimal a, BigDecimal b) {
		return a.max(b);
	}

	public static BigDecimal min(BigDecimal a, BigDecimal b) {
		return a.min(b);
	}

	@PostConstruct
	public void init() {
		customFormulas.forEach((key, formula) -> {
			try {
				expressionCache.put(key, parser.parseExpression(formula));
				log.info("فرمول {} با موفقیت کامپایل شد", key);
			} catch (Exception e) {
				log.error("خطا در کامپایل فرمول {}: {}", key, formula, e);
			}
		});
	}

	public BigDecimal calculate(String formulaKey, PricingContext context) {
		Expression expression = expressionCache.get(formulaKey);
		if (expression == null) {
			throw new InternalSaleCustomException.ValidationException("خطا در محاسبه قیمت", new ArrayList<>(Collections.singleton("فرمول پیدا نشد")));
		}

		return calculate(expression, context);
	}

	public BigDecimal calculate(String formula, PricingContext context, boolean cacheFormula) {
		Expression expression;
		if (cacheFormula) {
			expression = expressionCache.computeIfAbsent(formula, parser::parseExpression);
		} else {
			expression = parser.parseExpression(formula);
		}

		return calculate(expression, context);
	}

	public BigDecimal calculate(Expression expression, PricingContext context) {
		try {
			StandardEvaluationContext evalContext = new StandardEvaluationContext(context);
			evalContext.setVariables(context.toVariableMap());

			evalContext.registerFunction("round",
					this.getClass().getDeclaredMethod("round", BigDecimal.class, int.class));
			evalContext.registerFunction("max",
					this.getClass().getDeclaredMethod("max", BigDecimal.class, BigDecimal.class));
			evalContext.registerFunction("min",
					this.getClass().getDeclaredMethod("min", BigDecimal.class, BigDecimal.class));

			Object result = expression.getValue(evalContext);

			if (result instanceof BigDecimal) {
				return ((BigDecimal) result).setScale(2, RoundingMode.HALF_UP);
			} else if (result instanceof Number) {
				return BigDecimal.valueOf(((Number) result).doubleValue())
						.setScale(2, RoundingMode.HALF_UP);
			} else {
				throw new InternalSaleCustomException.ValidationException("خطا در محاسبه قیمت", new ArrayList<>(Collections.singleton("نتیجه فرمول معتبر نیست")));
			}

		} catch (Exception e) {
			log.error("خطا در محاسبه فرمول: {}", expression.getExpressionString(), e);
			throw new InternalSaleCustomException.ValidationException("خطا در محاسبه قیمت", new ArrayList<>(Collections.singleton(e.getMessage())));
		}
	}

	public void updateFormula(String key, String newFormula) {
		try {
			Expression expression = parser.parseExpression(newFormula);
			expressionCache.put(key, expression);
			customFormulas.put(key, newFormula);
			log.info("فرمول {} با موفقیت به روزرسانی شد", key);
		} catch (Exception e) {
			log.error("خطا در به روزرسانی فرمول {}: {}", key, newFormula, e);
			throw new InternalSaleCustomException.ValidationException("خطا در محاسبه قیمت", new ArrayList<>(Collections.singleton(e.getMessage())));
		}
	}

	public boolean validateFormula(String formula, PricingContext sampleContext) {
		try {
			Expression expression = parser.parseExpression(formula);
			calculate(expression, sampleContext);
			return true;
		} catch (Exception e) {
			log.error("فرمول نامعتبر: {}", formula, e);
			return false;
		}
	}
}