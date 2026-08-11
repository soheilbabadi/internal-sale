package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum LcReversalProcessVariable {
	CreditBridge("ثبت در خواست ثبت اطلاعات اعتبار اسنادی"), SettleSure("بررسی جهت تایید تسویه"), RemitSure("ثبت اطلاعات اعتبار اسنادی و تایید حواله"), FinalCheck("چه اطلاع و بررسی نهایی");
	private final String value;

	LcReversalProcessVariable(String value) {
		this.value = value;
	}

	public static LcReversalProcessVariable fromString(String input) {
		for (LcReversalProcessVariable type : LcReversalProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}