package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum ReversalProcessVariable {
	salesExpert("انتخاب شماره قرارداد مورد نظر جهت ابطال"), salesExpertReview("اظهار نظر نهایی"), salesManager("بررسی مدیر فروش جهت ابطال");
	private final String value;

	ReversalProcessVariable(String value) {
		this.value = value;
	}

	public static ReversalProcessVariable fromString(String input) {
		for (ReversalProcessVariable type : ReversalProcessVariable.values()) {
			if (type.name().equalsIgnoreCase(input) || type.value.equals(input)) {
				return type;
			}
		}
		return null;
	}
}