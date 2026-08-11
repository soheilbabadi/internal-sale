package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum SettlementType {
	CASH("نقدی", 0), CREDIT("اعتباری", 1), CASH_CREDIT("نقدی/اعتباری", 2), EXHALATION("انفساخ", 4), UNKNOWN("نامشخص", 255);
	private final String value;
	private final int code;

	SettlementType(String value, int code) {
		this.value = value;
		this.code = code;
	}

	public static SettlementType fromCode(int code) {
		for (SettlementType type : SettlementType.values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		return UNKNOWN;
	}
}