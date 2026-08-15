package com.nicico.internal.sales.extrabill.dto;

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
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProformaBankBillDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -6293320078087768602L;

	// ==================== فیلدهای شناسه ====================

	@Schema(description = "شناسه برات", example = "1")
	private Long id;

	@Schema(description = "شناسه جزئیات پیش فاکتور", example = "52")
	private Long proformaDetailId;

	@Schema(description = "شناسه پیش فاکتور", example = "52")
	private Long proformaMasterId;

	@Schema(description = "شناسه معامله", example = "123")
	private Long tradeId;

	@Schema(description = "شماره قرارداد", example = "123456")
	private Long contractNo;

	// ==================== فیلدهای بانکی ====================

	@Schema(description = "نام بانک صادر کننده برات", example = "بانک ملی ایران")
	private String issuerBankName;

	@Schema(description = "کد شعبه", example = "1234")
	private String branchCode;

	@Schema(description = "نام شعبه", example = "شعبه مرکزی")
	private String branchName;

	@Schema(description = "شهر محل پرداخت", example = "تهران")
	private String paymentCity;

	@Schema(description = "نام بانک عامل", example = "بانک ایران زمین")
	private String agentBankName;

	@Schema(description = "شناسه بانک عامل", example = "2011")
	private Long agentBankId;

	// ==================== فیلدهای برات الکترونیک ====================

	@Schema(description = "کد تفصیلی حسابداری", example = "123-456-789")
	private String nosaCode;

	@Schema(description = "کد سپام (شماره برات)", example = "1234-5678-9012-3456")
	private String sepamCode;

	@Schema(description = "شناسه خزانه داری کل کشور", example = "1234-5678-9012-3456")
	private String treasuryId;

	@Schema(description = "تاریخ صدور برات", example = "1404-01-15")
	private Date issueDate;

	@Schema(description = "تاریخ سررسید برات", example = "1404-03-15")
	private Date dueDate;

	// ==================== فیلدهای فایل ====================

	@Schema(description = "شناسه فایل پیوست برات الکترونیک (PDF)", example = "file-123-456")
	private String extraBillFileId;

	@Schema(description = "شناسه فایل اصلاحیه برات الکترونیک", example = "file-789-012")
	private String dispatchAttachmentId;

	// ==================== فیلدهای فرآیندی ====================

	@Schema(description = "وضعیت در فرایند", example = "PENDING")
	private WorkflowApproveStatus workflowApproveStatus;

	@Schema(description = "کد فرایند", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String processId;

	@Schema(description = "کد فرایند ابطال", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String reversalProcessId;

	@Schema(description = "شناسه برات در سیستم PMS", example = "PMS-123456")
	private String pmsBillId;

	// ==================== فیلدهای تاییدیه ====================

	@Schema(description = "تاییدیه نوع تسویه و نوع حواله")
	private Acknowledgment acknowledgment;

	@Schema(description = "ایمیل تاییدیه ارسال شده است یا خیر", example = "false")
	private boolean isReckoningSend;

	@Schema(description = "تاریخ ارسال ایمیل تاییدیه", example = "1404-01-20")
	private Date reckoningSendDate;

	// ==================== فیلدهای ابطال ====================

	@Schema(description = "تاریخ ابطال برات", example = "1404-02-01")
	private Date cancelDate;

	@Schema(description = "دلیل ابطال برات")
	private LcCancellationReason cancellationReason;

	// ==================== Inner Classes ====================

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaBankBillDto.Info")
	public static class Info extends ProformaBankBillDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921076L;

		@Schema(description = "تاریخ ایجاد")
		private Date createdDate;

		@Schema(description = "تاریخ آخرین تغییر")
		private Date lastModifiedDate;

		@Schema(description = "ایجاد کننده")
		private String createdBy;

		@Schema(description = "آخرین تغییر دهنده")
		private String lastModifiedBy;

		@Schema(description = "توضیحات")
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@NoArgsConstructor
	@Data
	@ApiModel("ProformaBankBillDto.Create")
	public static class Create extends ProformaBankBillDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921077L;
	}

	@EqualsAndHashCode(callSuper = true)
	@NoArgsConstructor
	@Data
	@ApiModel("ProformaBankBillDto.Update")
	public static class Update extends ProformaBankBillDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921078L;
	}
}