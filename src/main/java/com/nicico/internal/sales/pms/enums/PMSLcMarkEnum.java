package com.nicico.internal.sales.pms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PMSLcMarkEnum {
	lc,
	oragh,
	brat,
	check,
	LC,
	ORAGH,
	BRAT;

	@JsonValue
	public String toValue() {
		return this.name().toLowerCase();
	}

	@JsonCreator
	public static PMSLcMarkEnum fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (PMSLcMarkEnum mark : PMSLcMarkEnum.values()) {
			if (mark.name().equalsIgnoreCase(value)) {
				return mark;
			}
		}
		return null;
	}
}
