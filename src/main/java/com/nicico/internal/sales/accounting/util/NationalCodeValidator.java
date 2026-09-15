package com.nicico.internal.sales.accounting.util;

import org.apache.commons.lang3.StringUtils;

public final class NationalCodeValidator {

    private NationalCodeValidator() {
    }

    /**
     * Normalizes a national code or national ID by removing whitespace and dashes.
     */
    public static String normalize(String code) {
        if (code == null) {
            return null;
        }
        return code.trim().replace("-", "").replace(" ", "");
    }

    /**
     * Checks if the given code is a valid 10-digit Iranian national code format.
     */
    public static boolean isValidPersonNationalCode(String nationalCode) {
        String normalized = normalize(nationalCode);
        if (StringUtils.isBlank(normalized) || !normalized.matches("^\\d{10}$")) {
            return false;
        }

        // Check for invalid repetitive numbers (e.g. 0000000000, 1111111111)
        if (normalized.chars().distinct().count() == 1) {
            return false;
        }

        int checkDigit = Character.getNumericValue(normalized.charAt(9));
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(normalized.charAt(i)) * (10 - i);
        }
        int remainder = sum % 11;

        return (remainder < 2 && checkDigit == remainder) || (remainder >= 2 && checkDigit == (11 - remainder));
    }

    /**
     * Checks if the given code is a valid 11-digit Iranian company national ID (شناسه ملی).
     */
    public static boolean isValidCompanyNationalId(String nationalId) {
        String normalized = normalize(nationalId);
        if (StringUtils.isBlank(normalized) || !normalized.matches("^\\d{11}$")) {
            return false;
        }

        int checkDigit = Character.getNumericValue(normalized.charAt(10));
        int d = Character.getNumericValue(normalized.charAt(9)) + 2;
        int[] weights = {29, 27, 23, 19, 17, 29, 27, 23, 19, 17};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (Character.getNumericValue(normalized.charAt(i)) + d) * weights[i];
        }
        int remainder = sum % 11;
        if (remainder == 10) {
            remainder = 0;
        }
        return checkDigit == remainder;
    }

    /**
     * Determines whether the given normalized input represents a company national ID (11 digits) or person national code (10 digits).
     * Returns true if it is 11 digits, false if 10 digits.
     */
    public static boolean isCompany(String code) {
        String normalized = normalize(code);
        if (normalized != null && normalized.length() == 11) {
            return true;
        }
        return false;
    }
}
