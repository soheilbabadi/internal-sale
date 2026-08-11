package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum LcProcessVariable {
	CreditBridge("ثبت در خواست اعتبار اسنادی"),
	SettleSure("بررسی جهت تایید تسویه"), RemitSure("ثبت اطلاعات اعتبار اسنادی و تایید حواله"), FinalCheck("مشاهده تاریخچه و بررسی نهایی");
	private final String value;

	LcProcessVariable(String value) {
		this.value = value;
	}

	public static LcProcessVariable fromString(String input) {
		for (LcProcessVariable type : LcProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}