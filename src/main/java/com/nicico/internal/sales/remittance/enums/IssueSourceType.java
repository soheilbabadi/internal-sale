package com.nicico.internal.sales.remittance.enums;

import lombok.Getter;

@Getter
public enum IssueSourceType {

	LETTER_OF_CREDIT_OPENING("گشایش اعتبار اسنادی"),
	BANK_GUARANTEE("ضمانتنامه بانکی"),
	CASH("نقدی"),
	CASH_DEPOSIT_RECEIPT("فیش واریز نقدی"),
	FROM_CREDIT_FACILITIES("از محل مطالبات"),
	GAM_BONDS("اوراق گام"),
	EXTRA_BILL_OF_EXCHANGE("برات الکترونیک"), UNKNOWN("نامشخص");

	private final String value;

	IssueSourceType(String value) {
		this.value = value;
	}

	public static IssueSourceType fromString(String input) {
		for (IssueSourceType type : IssueSourceType.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}
