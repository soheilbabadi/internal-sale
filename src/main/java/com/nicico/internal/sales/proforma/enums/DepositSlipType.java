package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum DepositSlipType {
	WITH_TAX("با مالیات"),
	WITHOUT_TAX("بدون مالیات");

	private final String value;

	DepositSlipType(String value) {
		this.value = value;
	}

	public static DepositSlipType fromString(String input) {
		for (DepositSlipType type : DepositSlipType.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}