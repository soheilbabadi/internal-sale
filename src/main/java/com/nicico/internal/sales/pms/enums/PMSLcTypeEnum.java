package com.nicico.internal.sales.pms.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.Getter;

@Getter
public enum PMSLcTypeEnum {
	UnKnown(0),
	;
	private final int code;

	PMSLcTypeEnum(int code) {
		this.code = code;
	}

	public static PMSLcTypeEnum fromCode(int code) {
		for (PMSLcTypeEnum type : PMSLcTypeEnum.values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		throw new InternalSaleCustomException.ValidationException(String.format("PMSLcTypeEnum not found for code %s", code));
	}

	@JsonValue
	public int getCode() {
		return code;
	}
}
