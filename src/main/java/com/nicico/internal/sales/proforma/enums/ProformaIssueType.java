package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum ProformaIssueType {
	LETTER_OF_CREDIT_OPENING("گشایش اعتبار اسنادی"),
	BANK_GUARANTEE("ضمانتنامه بانکی"),
	CASH("نقدی"),
	FROM_CREDIT_FACILITIES("از محل مطالبات"),
	GUARANTEE_CHECK("چک ضمانتی"),
	GAM_BONDS("اوراق گام"),
	EXTRA_BILL_OF_EXCHANGE("برات الکترونیک"),
	MIXED("ترکیبی"),
	UNKNOWN("نامشخص");

	private final String value;

	ProformaIssueType(String value) {
		this.value = value;
	}

	public static ProformaIssueType fromString(String input) {
		for (ProformaIssueType type : ProformaIssueType.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}