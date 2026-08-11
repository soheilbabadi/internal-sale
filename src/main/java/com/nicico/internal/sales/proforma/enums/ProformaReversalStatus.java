package com.nicico.internal.sales.proforma.enums;

import lombok.Getter;

@Getter
public enum ProformaReversalStatus {
	NORMAL("NORMAL"), CANCELED("CANCELED"), EDITED("EDITED");
	private final String value;

	ProformaReversalStatus(String value) {
		this.value = value;
	}

	public static ProformaReversalStatus fromString(String input) {
		for (ProformaReversalStatus type : ProformaReversalStatus.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}
