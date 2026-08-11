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

	private Long id;

	@Schema(description = "نام بانک صادر کننده برات")
	private String issuerBankName;


	@Schema(description = "کد شعبه")
	private String branchCode;

	@Schema(description = "نام شعبه")
	private String branchName;

	@Schema(description = "شهر محل پرداخت")
	private String paymentCity;


	@Schema(description = "شناسه بانک عامل")
	private Long agentBankId;

	@Schema(description = "بانک عامل")
	private String agentBankName;

	@Schema(description = "کد تفصیلی")
	private String nosaCode;

	@Schema(description = "کد سپام")
	private String sepamCode;

	@Schema(description = "شناسه خزانه داری")
	private String treasuryId;

	@Schema(description = "تاریخ صدور برات")
	private Date issueDate;

	@Schema(description = "تاریخ سررسید")
	private Date dueDate;

	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "52")
	private Long proformaDetailId;

	@Schema(description = "شناسه  قرارداد پیش فاکتور", name = "proformaMasterId", example = "52")
	private Long proformaMasterId;


	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	private WorkflowApproveStatus workflowApproveStatus;


	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String processId;

	@Schema(description = "کد فرایندابطال", name = "reversalProcessId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String reversalProcessId;

	@Schema(description = "شماره قرارداد", name = "contractNo", example = "123456", nullable = false)
	private Long contractNo;

	@Schema(name = "tradeId", description = "شناسه معامله یا پیش فاکتور", example = "123")
	private Long tradeId;

	@Schema(description = "شناسه لجستک")
	private String pmsBillId;

	@Schema(description = "تاریخ ابطال")
	private Date cancelDate;

	@Schema(description = "دلیل ابطال")
	private LcCancellationReason cancellationReason;

	@Schema(description = "تاییدیه نوع تسویه و نوع حواله")
	private Acknowledgment acknowledgment = Acknowledgment.UNKNOWN;

	@Schema(description = "ایمیل تاییدیه ارسال شده است یا خیر")
	private boolean isReckoningSend = false;

	@Schema(description = "تاریخ ارسال ایمیل تاییدیه", name = "reckoningSendDate")
	private Date reckoningSendDate;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaBankBillDto.Info")
	public static class Info extends ProformaBankBillDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921076L;

		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaBankBillDto.Create")
	public static class Create extends ProformaBankBillDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921077L;
	}
}