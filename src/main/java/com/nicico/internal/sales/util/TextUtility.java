package com.nicico.internal.sales.util;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class TextUtility {
	private static final String ALPHANUMERIC =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final String ALPHABETIC =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String NUMERIC = "0123456789";
	private static final String ALPHANUMERIC_SPECIAL =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
	private static final Random RANDOM = new SecureRandom();

	private TextUtility() {
		// Prevent instantiation
	}

	public static boolean isValidUUID(String uuid) {
		try {
			UUID.fromString(uuid);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public static Double getSimilarity(String s1, String s2) {
		s1 = normalizeArabicToPersian(s1);
		s2 = normalizeArabicToPersian(s2);
		JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
		Double similarity = jaroWinkler.apply(s1, s2);
		return similarity * 100;
	}

	public static String reformatAddress(String address) {
		address = normalizeArabicToPersian(address);
		return address.replace("،", "-");
	}

	public static String shortenAddress(String address) {
		int limit = 70;
		address = reformatAddress(address);
		return Optional.of(address).filter(s -> !s.isBlank()).filter(s -> s.length() > limit).map(s -> {
			int lastDash = s.lastIndexOf('-', limit);
			int lastComma = s.lastIndexOf('،', limit);
			int lastSpace = s.lastIndexOf(' ', limit);
			int firstDash = s.indexOf('-', limit);
			int firstComma = s.indexOf('،', limit);
			int firstSpace = s.indexOf(' ', limit);
			return Stream.of(lastDash, lastComma, lastSpace).filter(i -> i > 0).max(Integer::compare).or(() -> Stream.of(firstDash, firstComma, firstSpace).filter(i -> i > 0).min(Integer::compare)).map(i -> s.substring(0, i).trim()).orElseGet(() -> {
				int safe = s.lastIndexOf(' ', limit);
				return (safe > 0 ? s.substring(0, safe) : s.substring(0, limit)).trim();
			});
		}).orElse(address);
	}

	public static String normalizeArabicToPersian(String input) {
		if (input == null) return null;
		return input.replace("ي", "ی").replace("ك", "ک").replace("ة", "ه").replace("ۀ", "ه").replace("ؤ", "و").replace("إ", "ا").replace("أ", "ا").replace("ء", "");
	}

	public static String maskString(String originalString, int visibleChars) {
		if (originalString == null || originalString.length() <= visibleChars) {
			return originalString;
		}
		int maskedLength = originalString.length() - visibleChars;
		String maskedPart = "*".repeat(maskedLength);
		return maskedPart + originalString.substring(maskedLength);
	}


	public static String generateRandomString(int length) {
		return generateRandomString(length, ALPHANUMERIC);
	}


	public static String generateRandomString(int length, String characterSet) {
		if (length <= 0) {
			throw new InternalSaleCustomException.ValidationException("Length must be positive");
		}
		if (characterSet == null || characterSet.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException("Character set cannot be null or empty");
		}

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = RANDOM.nextInt(characterSet.length());
			sb.append(characterSet.charAt(randomIndex));
		}
		return sb.toString();
	}

	public static String generateAlphabeticString(int length) {
		return generateRandomString(length, ALPHABETIC);
	}

	public static String generateNumericString(int length) {
		return generateRandomString(length, NUMERIC);
	}

	public static String generateStringWithSpecialChars(int length) {
		return generateRandomString(length, ALPHANUMERIC_SPECIAL);
	}
}
