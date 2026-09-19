package com.nicico.internal.sales.accounting.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Supported financial instrument types with their corresponding Accounting detail main-code.
 */
@Getter
@RequiredArgsConstructor
public enum FinancialInstrumentType {

	/**
	 * اعتبار اسنادی - Letter of Credit
	 */
	LETTER_OF_CREDIT("107/18", "اعتبار اسنادی"),

	/**
	 * اوراق گام - GAM Certificate
	 */
	GAM("108/18", "اوراق گام"),

	/**
	 * برات الکترونیکی - Electronic Promissory Note
	 */
	ELECTRONIC_PROMISSORY_NOTE("109/18", "برات الکترونیکی");

	private final String mainCode;
	private final String title;
}
