package com.nicico.internal.sales.pms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nicico.internal.sales.exception.InternalSaleCustomException;

public enum PMSLcStatusEnum {
	UnKnown(0), ACTIVE(281), DISABLED(282);
	private final int code;

	PMSLcStatusEnum(int code) {
		this.code = code;
	}

	@JsonCreator
	public static PMSLcStatusEnum fromCode(int code) {
		for (PMSLcStatusEnum status : PMSLcStatusEnum.values()) {
			if (status.code == code) {
				return status;
			}
		}
		throw new InternalSaleCustomException.ValidationException("Invalid PMSLcStatusEnum code: " + code);
	}

	@JsonValue
	public int getCode() {
		return code;
	}
}
