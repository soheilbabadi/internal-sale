package com.nicico.internal.sales.lc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.Getter;

@Getter
public enum Acknowledgment {
	RECKONING("تایید تسویه"), REMITTANCE("تایید حواله"), UNKNOWN("نامشخص"), FINISHED("تمام شده"), FINAL_CHECK("بررسی نهایی"), CANCELED("رد شده");

	private final String value;

	Acknowledgment(String value) {
		this.value = value;
	}

	@JsonCreator
	public static Acknowledgment fromString(String input) {
		if (input == null || input.trim().isEmpty()) {
			return null;
		}

		for (Acknowledgment acknowledgment : Acknowledgment.values()) {
			if (acknowledgment.name().equalsIgnoreCase(input)) {
				return acknowledgment;
			}
		}

		throw new InternalSaleCustomException.ValidationException("Invalid Acknowledgment: " + input);
	}
}


