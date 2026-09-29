package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum ProformaIssueType {
	LETTER_OF_CREDIT_OPENING("گشایش اعتبار اسنادی"),
	BANK_GUARANTEE("ضمانتنامه بانکی"),
	CASH("نقدی"),
	CASH_DEPOSIT_RECEIPT("فیش واریز نقدی"),
	FROM_CREDIT_FACILITIES("از محل مطالبات"),
	GAM_BONDS("اوراق گام"),
	EXTRA_BILL_OF_EXCHANGE("برات الکترونیک");

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