package com.nicico.internal.sales.wf.enums;

import lombok.Getter;

@Getter
public enum ExtraBillProcessVariable {

	DraftRegistration("ثبت برات الکترونیک"),
	DraftReview("بررسی اطلاعات برات"),
	BankApproval("تایید بانک"),
	BeneficiaryConfirmation("تایید ذینفع"),
	DraftIssuance("صدور برات الکترونیک"),
	FinalVerification("کنترل و تایید نهایی");

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