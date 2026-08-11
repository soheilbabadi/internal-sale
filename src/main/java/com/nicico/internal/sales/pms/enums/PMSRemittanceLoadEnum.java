package com.nicico.internal.sales.pms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PMSRemittanceLoadEnum {
	MES_SARCHESHMEH(1000, "مجتمع مس سرچشمه"),
	MES_SHAHRBABAK_KHATOON_ABAD(1021, "مجتمع مس شهربابك - خاتون آباد"),
	MES_SHAHRBABAK_MIDOOK(1540, "مجتمع مس شهربابك -ميدوك"),
	MES_SONGUN(1541, "مجتمع مس سونگون");
	@JsonValue
	private final Integer id;
	private final String name;

	@JsonCreator
	// متد برای پیدا کردن enum از روی id
	public static PMSRemittanceLoadEnum fromId(Integer id) {
		for (PMSRemittanceLoadEnum mahal : values()) {
			if (mahal.getId().equals(id)) {
				return mahal;
			}
		}
		throw new InternalSaleCustomException.ValidationException("Invalid MahalBarGiri id: " + id);
	}
}