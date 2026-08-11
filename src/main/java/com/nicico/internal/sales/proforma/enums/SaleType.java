package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum SaleType {
	EXWORKS("EXWORKS");
	private final String value;

	SaleType(String value) {
		this.value = value;
	}

	public static SaleType fromString(String input) {
		for (SaleType type : SaleType.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}
