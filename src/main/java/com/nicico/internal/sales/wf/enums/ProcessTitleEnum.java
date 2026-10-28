package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Getter
public enum ProcessTitleEnum {
	PREINVOICE("PREINVOICE"),
	REVERSAL("REVERSAL"),
	LC("LC"),
	EXTRA_BILL("EXTRA_BILL"),
	REMITTANCE("REMITTANCE"),
	GAAM_BOUND("GAAM_BOUND");

	private final String value;

	ProcessTitleEnum(String value) {
		this.value = value;
	}

	public static Optional<ProcessTitleEnum> fromValue(String value) {
		if (value == null || value.isBlank()) {
			return Optional.empty();
		}
		String normalized = value.trim().toUpperCase(Locale.ROOT);
		return Arrays.stream(values())
				.filter(processTitleEnum -> processTitleEnum.value.equals(normalized))
				.findFirst();
	}
}

