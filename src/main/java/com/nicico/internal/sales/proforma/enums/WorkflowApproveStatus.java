package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum WorkflowApproveStatus {
	DRAFT("DRAFT"), IN_PROGRESS("IN_PROGRESS"),
	ACCEPTED("ACCEPTED"),
	CANCELED("CANCELED"),
	EXCEPTION("EXCEPTION"),
	REVERSAL("REVERSAL"), NOT_STARTED("NOT_STARTED");
	private final String value;

	WorkflowApproveStatus(String value) {
		this.value = value;
	}

	public static WorkflowApproveStatus fromString(String input) {
		for (WorkflowApproveStatus type : WorkflowApproveStatus.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}