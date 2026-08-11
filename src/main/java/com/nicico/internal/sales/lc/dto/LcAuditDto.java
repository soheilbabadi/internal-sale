package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LcAuditDto {
	@Schema(description = "شماره نسخه")
	private String rev;
	@Schema(description = "شناسه")
	private Long id;
	@Schema(description = "تاریخ ایجاد")
	private String createdDate;
	@Schema(description = "تاریخ آخرین ویرایش")
	private String lastModifiedDate;
	@Schema(description = "کاربر ایجاد کننده")
	private String createdBy;
	@Schema(description = "کاربر ویرایش کننده")
	private String lastModifiedBy;
	@Schema(description = "نظر")
	private String comment;
	@Schema(description = "توضیحات")
	private String description;
	@Schema(description = "نوع عملیات")
	private String revisionType;
	@Schema(description = "شماره پیش فاکتور")
	private String performaNo;
	@Schema(description = "تاریخ پیش فاکتور")
	private String performaDate;
	@Schema(description = "شماره قرارداد")
	private String contractNo;
	@Schema(description = "شماره اعتبار اسنادی")
	private String lcNo;
	@Schema(description = "تاریخ گشایش اعتبار")
	private String lcDate;
	@Schema(description = "شناسه بانک معامله گر")
	private String tradingBankId;
	@Schema(description = "نام بانک معامله گر")
	private String tradingBankTitle;
	@Schema(description = "عنوان شعبه بانک معامله گر")
	private String tradingBankBranchTitle;
	@Schema(description = "دوره اعتبار")
	private String creditExpirePeriod;
	@Schema(description = "تعویق پرداخت")
	private String paymentDeferral;
	@Schema(description = "روزهای مهلت")
	private String deadlineDays;
	@Schema(description = "وضعیت تایید گردش کار")
	private String workflowApproveStatus;
	@Schema(description = "کد فرایند")
	private String processId;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی")
	private String lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی")
	private String settlementDueDate;
	@Schema(description = "شناسه بانک صادر کننده")
	private String issuerBankId;
	@Schema(description = "نام بانک صادر کننده")
	private String issuerBankName;
	@Schema(description = "نام شعبه بانک صادر کننده")
	private String issuerBankBranchName;
	@Schema(description = "کد بانک صادر کننده")
	private String issuerBankCode;
	@Schema(description = "شناسه فایل اعتبار اسنادی")
	private String lcFileId;
	@Schema(description = "شناسه فایل ارسال")
	private String dispatchFileId;
	@Schema(description = "شناسه فایل پیش فاکتور")
	private String proformaFileId;
	@Schema(description = "شناسه اصلی پیش فاکتور")
	private String proformaMasterId;
	@Schema(description = "شناسه جزئیات پیش فاکتور")
	private String proformaDetailId;
	@Schema(description = "شناسه نمونه پیش فاکتور")
	private String proformaInstanceId;
	@Schema(description = "شناسه نمونه اعتبار اسنادی")
	private String lcInstanceId;
	@Schema(description = "نیازمند فایل ارسال")
	private String requireDispatchFile;
	@Schema(description = "شناسه سند اطلاعیه")
	private String notificationDocumentId;
	@Schema(description = "کد نوسا")
	private String nosaCode;
	@Schema(description = "کد پرداخت")
	private String paymentCode;
	@Schema(description = "شناسه PMS LC")
	private String pmsLcId;
	@Schema(description = "تاریخ ابطال ال سی")
	private Date cancelDate;
	@Schema(description = "دلیل ابطال")
	private LcCancellationReason lcCancellationReason;


}
