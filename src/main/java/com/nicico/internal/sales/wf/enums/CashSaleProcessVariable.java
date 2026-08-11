package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum CashSaleProcessVariable {
	Proforma("ثبت پیش فاکتور ها بر اساس شماره قرار داد"), bossProforma("بررسی پیش فاکتور توسط مدیر فروش و بازاریابی"), Customer("بررسی جهت ارسال پیش فاکتور به مشتری");
	private final String value;

	CashSaleProcessVariable(String value) {
		this.value = value;
	}

	public static CashSaleProcessVariable fromString(String input) {
		for (CashSaleProcessVariable type : CashSaleProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}