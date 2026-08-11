package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum RemittanceProcessVariable {
	RemitForge("ثبت درخواست تولید حواله"), RemitGuardboss("بررسی جهت تایید رئیس حواله"), RemitGuard("بررسی جهت تایید مدیر حواله");
	private final String value;

	RemittanceProcessVariable(String value) {
		this.value = value;
	}

	public static RemittanceProcessVariable fromString(String input) {
		for (RemittanceProcessVariable type : RemittanceProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}