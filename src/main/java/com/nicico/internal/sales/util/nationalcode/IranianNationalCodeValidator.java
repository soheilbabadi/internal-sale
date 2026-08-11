package com.nicico.internal.sales.util.nationalcode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IranianNationalCodeValidator implements ConstraintValidator<IranianNationalCode, String> {
	public void initialize(IranianNationalCode constraintAnnotation) {
		throw new UnsupportedOperationException("Initialization is not supported for this validator.");
	}

	@Override
	public boolean isValid(String code, ConstraintValidatorContext context) {
		if (code == null || !code.matches("\\d{10}")) {
			return false;
		}
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			int digit = Character.getNumericValue(code.charAt(i));
			sum += digit * (10 - i);
		}
		int remainder = sum % 11;
		int expectedCheckDigit = (remainder < 2) ? remainder : 11 - remainder;
		int actualCheckDigit = Character.getNumericValue(code.charAt(9));
		return actualCheckDigit == expectedCheckDigit;
	}
}