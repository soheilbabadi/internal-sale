package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum ProformaProcessVariable {
	Proforma("ثبت پیش فاکتور ها بر اساس شماره قرار داد"), bossProforma("بررسی پیش فاکتور توسط مدیر فروش و بازاریابی");
	private final String value;

	ProformaProcessVariable(String value) {
		this.value = value;
	}

	public static ProformaProcessVariable fromString(String input) {
		for (ProformaProcessVariable type : ProformaProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}