package com.nicico.internal.sales.remittance.enums;

import lombok.Getter;

@Getter
public enum IssueSourceType {
	LETTER_OF_CREDIT_OPENING("بورس-نقدی/اعتباری(گشایش اعتبار اسنادی)"), BANK_GUARANTEE("بورس-نقدی/اعتباری(ضمانتنامه بانکی)"), CASH("بورس-نقدی"), FROM_CREDIT_FACILITIES("بورس-نقدی/اعتباری(از محل مطالبات)"), GUARANTEE_CHECK("بورس-نقدی/اعتباری(چک ضمانتی)"), GAM_BONDS("بورس-نقدی/اعتباری(اوراق گام)"), EXTRA_BILL_OF_EXCHANGE("بورس-نقدی/اعتباری(برات الکترونیک)"), UNKNOWN("نامشخص");
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
