package com.nicico.internal.sales.wf.enums;


@Getter
public enum ExtraBillProcessVariable {

	// ==================== مرحله اول: ثبت و تشکیل پرونده ====================
	BillRegistration("ثبت برات الکترونیک"),

	// ==================== مرحله دوم: تایید تسویه ====================
	SettlementReview("بررسی اطلاعات تسویه برات"),
	SettlementApproval("تایید تسویه برات"),

	// ==================== مرحله سوم: تایید حواله ====================
	TransferReview("بررسی اطلاعات حواله برات"),
	TransferApproval("تایید حواله برات"),

	// ==================== مرحله چهارم: بررسی نهایی ====================
	FinalVerification("کنترل و تایید نهایی برات"),
	BillArchiving("بایگانی برات الکترونیک");

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