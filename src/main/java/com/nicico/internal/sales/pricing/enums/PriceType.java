package com.nicico.internal.sales.pricing.enums;

import lombok.Getter;

@Getter
public enum PriceType {
	BASE("پایه"),
	FINAL("نهایی"),
	SETTLEMENT("تسویه");

	private final String persianName;

	PriceType(String persianName) {
		this.persianName = persianName;
	}

}