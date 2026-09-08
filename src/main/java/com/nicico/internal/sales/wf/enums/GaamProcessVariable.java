package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum GaamProcessVariable {

	BillDraftRegistration("ثبت برات الکترونیک"),
	BillSettleSure("بررسی جهت تایید تسویه"),
	BillRemitSure("ثبت اطلاعات برات و تایید حواله"),
	BillFinalCheck("مشاهده تاریخچه و بررسی نهایی");

	private final String value;

	GaamProcessVariable(String value) {
		this.value = value;
	}

	public static GaamProcessVariable fromString(String input) {
		for (GaamProcessVariable type : GaamProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}

}