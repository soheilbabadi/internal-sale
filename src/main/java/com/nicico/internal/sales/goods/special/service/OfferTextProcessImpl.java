package com.nicico.internal.sales.goods.special.service;

import com.nicico.internal.sales.goods.special.repository.OfferTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class OfferTextProcessImpl implements OfferTextProcess {

	private static final Pattern METAL_PERCENTAGE_PATTERN = Pattern.compile(
			"میانگین\\s*درصد\\s*(نقره|طلا)\\s*(\\d+(\\.\\d+)?)" +
					"|میانگین\\s*درصد(نقره|طلا)\\s*(\\d+(\\.\\d+)?)"
	);

	private static final Pattern COMMISSION_PATTERN = Pattern.compile(
			"کارمزد\\s*(\\d+(\\.\\d+)?)%"
	);

	private static final Pattern SELENIUM_PERCENT_PATTERN = Pattern.compile(
			"درصد\\s*سلنیوم\\s*(\\d+(\\.\\d+)?)|درصدسلنیوم\\s*(\\d+(\\.\\d+)?)"
	);

	private static final Pattern BASKET_NUMBER_PATTERN = Pattern.compile(
			"شماره\\s*بشکه\\s*(\\d+\\s*-\\s*\\d+)"
	);

	private static final Pattern WITHOUT_SELENIUM_PATTERN = Pattern.compile(
			"بدون\\s*سلنیوم"
	);

	private static final Pattern LOT_NUMBER_PATTERN = Pattern.compile(
			"#[^#]*?((?:[A-Za-z]+-)?\\d+(?:-\\d+)*)#"
	);

	// ==================== Constants ====================

	private static final double DEFAULT_COMMISSION = 5.0;
	private static final String EMPTY_STRING = "";
	private static final String DASH = "-";
	private static final String WITHOUT_SELENIUM = "بدون سلنیوم";
	private static final String WITH_SELENIUM = "با سلنیوم";
	private final OfferTextRepository offerTextRepository;


	@Override
	public String findDescriptionByPaymentCode(String paymentCode) {
		return offerTextRepository.getDescriptionByPaymentCode(paymentCode);
	}

	@Override
	public boolean containsMetalPercentage(String text) {
		return METAL_PERCENTAGE_PATTERN.matcher(normalize(text)).find();
	}

	@Override
	public double getCommission(String text) {
		Matcher matcher = COMMISSION_PATTERN.matcher(normalize(text));
		return matcher.find()
				? Double.parseDouble(matcher.group(1))
				: DEFAULT_COMMISSION;
	}

	@Override
	public String getSeleniumPercent(String text) {
		Matcher matcher = SELENIUM_PERCENT_PATTERN.matcher(normalize(text));
		if (!matcher.find()) return EMPTY_STRING;

		// group(1) → "درصد سلنیوم X", group(3) → "درصدسلنیوم X"
		String value = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);
		return value != null ? value : EMPTY_STRING;
	}

	@Override
	public String getBasketNumber(String text) {
		Matcher matcher = BASKET_NUMBER_PATTERN.matcher(normalize(text));
		return matcher.find()
				? matcher.group(1).replaceAll("\\s+", EMPTY_STRING)
				: EMPTY_STRING;
	}


	@Override
	public String hasSelenium(String text) {
		String normalized = normalize(text);

		if (WITHOUT_SELENIUM_PATTERN.matcher(normalized).find()) return WITHOUT_SELENIUM;
		if (SELENIUM_PERCENT_PATTERN.matcher(normalized).find()) return WITH_SELENIUM;

		return WITHOUT_SELENIUM;
	}

	/**
	 * Extracts lot number enclosed in # delimiters.
	 * Supported formats: #1234#, #42345-5345#, #L-3213#
	 */
	@Override
	public String extractLotNumber(String text) {
		Matcher matcher = LOT_NUMBER_PATTERN.matcher(normalize(text));
		return matcher.find() ? matcher.group(1).trim() : DASH;
	}
	// ==================== Private Helpers ====================

	private String normalize(String input) {
		if (input == null) return EMPTY_STRING;

		return convertArabicNumerals(input)
				.replaceAll("\\u200C", "")
				.replaceAll("\\s+", " ")
				.trim();
	}

	private String convertArabicNumerals(String input) {
		return input
				// Arabic-Indic digits (U+0660–U+0669)
				.replace('٠', '0').replace('١', '1').replace('٢', '2')
				.replace('٣', '3').replace('٤', '4').replace('٥', '5')
				.replace('٦', '6').replace('٧', '7').replace('٨', '8')
				.replace('٩', '9')
				// Extended Arabic-Indic / Persian digits (U+06F0–U+06F9)
				.replace('۰', '0').replace('۱', '1').replace('۲', '2')
				.replace('۳', '3').replace('۴', '4').replace('۵', '5')
				.replace('۶', '6').replace('۷', '7').replace('۸', '8')
				.replace('۹', '9')
				// Arabic decimal separator
				.replace('٫', '.');
	}
}