package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum ExtraBillProcessVariable {

	BillDraftRegistration("ثبت برات الکترونیک"),
	BillSettleSure("بررسی جهت تایید تسویه"),
	BillRemitSure("ثبت اطلاعات برات و تایید حواله"),
	BillFinalCheck("مشاهده تاریخچه و بررسی نهایی");

	private final String value;

	ExtraBillProcessVariable(String value) {
		this.value = value;
	}

	public static ExtraBillProcessVariable fromString(String input) {
		for (ExtraBillProcessVariable type : ExtraBillProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}

}