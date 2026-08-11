package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum ProformaStatus {
	DRAFT("DRAFT"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED"), CANCELED("CANCELED");
	private final String value;

	ProformaStatus(String value) {
		this.value = value;
	}

	public static ProformaStatus fromString(String input) {
		for (ProformaStatus type : ProformaStatus.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}