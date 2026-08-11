package com.nicico.internal.sales.lc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.Getter;

@Getter
public enum LcCancellationReason {
	BUYER_WITHDRAWAL("انصراف خریدار از معامله"),
	SELLER_WITHDRAWAL("انصراف فروشنده از معامله"),
	COMMERCIAL_DISAGREEMENT("عدم توافق تجاری نهایی"),
	INABILITY_TO_SUPPLY_GOODS("عدم توانایی فروشنده در تأمین کالا"),
	DELAY_IN_GOODS_PREPARATION("تاخیر در ارسال کالا"),
	CONTRACT_TERMINATION("فسخ قرارداد اصلی"),
	INSUFFICIENT_FUNDS_OR_COLLATERAL("عدم تأمین وجه یا وثیقه کافی"),
	FAILED_CREDIT_EVALUATION("عدم تأیید اعتبار متقاضی"),
	BANKING_REGULATION_VIOLATION("نقض مقررات بانکی"),
	LC_TEXT_ERROR("اشکال اساسی در متن اعتبار اسنادی"),
	LC_NOT_ACCEPTED_BY_BENEFICIARY("عدم پذیرش اعتبار اسنادی توسط فروشنده"),
	DOCUMENTS_NOT_PRESENTED_ON_TIME("عدم ارائه اسناد در مهلت مقرر"),
	DOCUMENT_DISCREPANCY("مغایرت اسناد با شرایط اعتبار اسنادی"),
	FORGED_OR_INVALID_DOCUMENTS("ارائه اسناد جعلی یا مخدوش"),
	GOODS_NOT_MATCHING_DOCUMENTS("عدم تطابق کالا با اسناد"),
	LEGAL_RESTRICTION_OR_SANCTION("تحریم یا محدودیت قانونی"),
	COURT_ORDER("دستور مراجع قضایی یا نظارتی"),
	BANKRUPTCY("ورشکستگی"),
	ILLEGAL_TRADE_SUBJECT("غیرقانونی شدن موضوع معامله"),
	LC_EXPIRED("انقضای اعتبار اعتبار اسنادی"),
	LC_NOT_EXTENDED("عدم تمدید اعتبار اسنادی"),
	LATE_SHIPMENT("تاخیر در حمل نسبت به تاریخ مجاز"),
	MUTUAL_CANCELLATION("ابطال اعتبار اسنادی با توافق طرفین"),
	OTHER("سایر دلایل");
	private final String value;

	LcCancellationReason(String value) {
		this.value = value;
	}

	@JsonCreator
	public static LcCancellationReason fromString(String input) {
		if (input == null || input.trim().isEmpty()) {
			return null;
		}

		for (LcCancellationReason reason : LcCancellationReason.values()) {
			if (reason.name().equalsIgnoreCase(input)) {
				return reason;
			}
		}

		throw new InternalSaleCustomException.ValidationException("Invalid LcCancellationReason: " + input);
	}
}
