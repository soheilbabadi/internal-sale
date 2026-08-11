package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LcDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 6313070493614504250L;
	private long id;
	@Schema(description = "شماره پیش فاکتور")
	private String performaNo;
	@Schema(description = "تاریخ پیش فاکتور")
	private String performaDate;

	@Schema(description = "شماره قرارداد")
	private Long contractNo;


	@Schema(description = "تاریخ قرارداد")
	private String contractDate;

	@Schema(description = "شماره اعتبار اسنادی")
	private String lcNo;
	@Schema(description = "تاریخ گشایش اعتبار")
	private Date lcDate;
	@Schema(name = "شناسه بانک")
	private Long tradingBankId;
	@Schema(name = "نام بانک")
	private String tradingBankTitle;
	@Schema(name = "عنوان شعبه")
	private String tradingBankBranchTitle;
	@Schema(description = "سررسید انقضای اعتبار-ماه")
	private Integer creditExpirePeriod;
	@Schema(description = "مهلت پرداخت وجه اعتبار اسنادی-روز")
	private Integer paymentDeferral;
	@Schema(description = "مدت اعتبار پیش فاکتور-روز")
	private Integer deadlineDays;
	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	private WorkflowApproveStatus workflowApproveStatus;
	private String processId;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	private Date settlementDueDate;
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;
	@Schema(name = "نام بانک بانک گشایش اعتبار اسنادی")
	private String issuerBankName;
	@Schema(name = "نام شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchName;
	@Schema(name = "کد شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchCode;
	@Schema(description = "شناسه پیش فاکتور-اصلی")
	private long proformaMasterId;

	@Schema(description = "شناسه پیش فاکتور-فرعی")
	private long proformaDetailId;
	@Schema(description = "شناسه پیش فاکتور در موتور گردش کار")
	private String proformaInstanceId;
	@Schema(description = "شناسه اعتبار اسنادی در موتور گردش کار")
	private String lcInstanceId;
	@Schema(description = "شناسه فایل پیوست اعتبار اسنادی")
	private String lcAttachmentId;
	@Schema(description = "شناسه فایل اصلاحیه بارنامه")
	private String dispatchAttachmentId;
	@Schema(description = "شناسه فایل پیوست پیش فاکتور")
	private String proformaFileId;
	@Schema(description = "آیا فایل اصلاحیه الزامی است؟")
	private Boolean requireDispatchFile;
	@Schema(description = "شناسه سند ابلاغیه", name = "notificationDocumentId", example = "12345")
	private String notificationDocumentId;
	@Schema(description = "کد نوسا", name = "nosaCode", example = "NOSA123456")
	private String nosaCode;
	@Schema(description = "کد پرداخت", name = "paymentCode", example = "32436455344")
	private String paymentCode;
	@Schema(description = "تاییدیه نوع تسویه و نوع حواله", name = "acknowledgment")
	private Acknowledgment acknowledgment;
	@Schema(description = "ایمیل تاییدیه ارسال شده است یا خیر", name = "isReckoningSend")
	private boolean isReckoningSend = false;
	@Schema(description = "تاریخ ارسال ایمیل تاییدیه", name = "reckoningSendDate")
	private Date reckoningSendDate;
	@Schema(description = "تاریخ ابطال ال سی")
	private Date cancelDate;
	@Schema(description = "دلیل ابطال")
	private LcCancellationReason lcCancellationReason;


	private Long brokerId;
	private String brokerName;
	private String brokerNationalCode;

	private BigDecimal totalQuantity;
	private BigDecimal totalFinalAmount;
	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	@Schema(description = "شناسه PMS LC")
	private String pmsLcId;

	@Schema(description = "شناسه کالا", name = "goodId")
	private Long goodId;

	@Schema(description = "نام کالا", name = "goodName")
	private String goodName;


	@Schema(description = "کد مشتری", name = "customerId", example = "1")
	private Long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "مهدی حسینی")
	private String customerName;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("LcDto.Info")
	public static class Info extends LcDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921075L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LcDto.Create")
	public static class Create extends LcDto {
		@Serial
		private static final long serialVersionUID = -6875168459138326735L;
	}
}
