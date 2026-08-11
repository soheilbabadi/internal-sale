package com.nicico.internal.sales.bill.dto;

import lombok.Getter;

@Getter
public enum BillingStatus {

	DRAFT("پیش نویس"),
	ISSUED("صادر شده"),
	PAID("پرداخت شده"),
	DISPUTED("مورد اعتراض"),
	CANCELED("لغو شده"),
	REPLACED("جایگزین شده"),
	REVERSED("برگشت خورده");

	private final String value;

	BillingStatus(String value) {
		this.value = value;
	}

	public static BillingStatus fromString(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		for (BillingStatus status : values()) {
			if (status.name().equalsIgnoreCase(input) || status.value.equalsIgnoreCase(input)) {
				return status;
			}
		}
		return null;
	}
}