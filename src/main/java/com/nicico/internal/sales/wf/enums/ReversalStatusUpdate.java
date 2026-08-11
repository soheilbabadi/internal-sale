package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum ReversalStatusUpdate {
	ACTIVE("ACTIVE"),
	CANCELED("CANCELED"),
	FINISHED_ACCEPTED("FINISHED_ACCEPTED"),
	FINISHED_REJECTED("FINISHED_REJECTED"),
	DEFAULT("DEFAULT");
	private final String value;

	ReversalStatusUpdate(String value) {
		this.value = value;
	}

	public static ReversalStatusUpdate fromString(String input) {
		for (ReversalStatusUpdate type : ReversalStatusUpdate.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}
