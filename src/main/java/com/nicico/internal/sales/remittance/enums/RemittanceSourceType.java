package com.nicico.internal.sales.remittance.enums;

import lombok.Getter;

@Getter
public enum RemittanceSourceType {
	PROFORMA("پیش فاکتور"), TRADE("معامله");
	private final String value;

	RemittanceSourceType(String value) {
		this.value = value;
	}

	public static RemittanceSourceType fromString(String input) {
		for (RemittanceSourceType type : RemittanceSourceType.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}
