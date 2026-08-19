package com.nicico.internal.sales.extrabill.model;


import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.util.Date;

@Audited
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "T_INS_PROFORMA_BANK_BILL")
public class ProformaBankBillModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_PROFORMA_BANK_BILL")
	@SequenceGenerator(name = "SEQ_INS_PROFORMA_BANK_BILL", sequenceName = "SEQ_INS_PROFORMA_BANK_BILL", allocationSize = 1)
	private Long id;


	@Schema(description = "نام بانک صادر کننده برات")
	@Column(name = "C_ISSUER_BANK_NAME", length = 200, nullable = false)
	private String issuerBankName;


	@Schema(description = "کد بانک صادر کننده برات")
	@Column(name = "N_ISSUER_BANK_ID")
	private Long issuerBankId;


	@Schema(description = "کد شعبه")
	@Column(name = "C_BRANCH_CODE", length = 50)
	private String branchCode;

	@Schema(description = "نام شعبه")
	@Column(name = "C_BRANCH_NAME", length = 200)
	private String branchName;

	@Schema(description = "شهر محل پرداخت")
	@Column(name = "C_PAYMENT_CITY", length = 100)
	private String paymentCity;

	@Schema(description = "بانک عامل")
	@Column(name = "C_AGENT_BANK_NAME", length = 100)
	private String agentBankName;

	@Schema(description = "شناسه بانک عامل")
	@Column(name = "N_AGENT_BANK_ID")
	private Long agentBankId;

	@Schema(description = "شناسه فایل پیوست برات")
	@Column(name = "C_EXTRA_BILL_FILE_ID", length = 100)
	private String extraBillFileId;

	@Schema(description = "شناسه فایل اصلاحیه")
	@Column(name = "C_DISPATCH_FILE_ID", length = 100)
	private String dispatchAttachmentId;

	@Schema(description = "کد تفصیلی")
	@Column(name = "C_NOSA_CODE", length = 50)
	private String nosaCode;

	@Schema(description = "کد سپام")
	@Column(name = "C_SEPAM_CODE", length = 50)
	private String sepamCode;

	@Schema(description = "شناسه خزانه داری")
	@Column(name = "C_TREASURY_ID", length = 50)
	private String treasuryId;

	@Schema(description = "تاریخ صدور برات")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_ISSUE_DATE")
	private Date issueDate;

	@Schema(description = "تاریخ سررسید")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_DUE_DATE")
	private Date dueDate;

	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "52")
	@Column(name = "F_PROFORMA_DETAIL_ID")
	private Long proformaDetailId;

	@Column(name = "F_PERFORMA_MASTER_ID")
	private Long proformaMasterId;


	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100)
	private WorkflowApproveStatus workflowApproveStatus;


	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	@Column(name = "C_PROCESS_ID", length = 50, nullable = false)
	private String processId;

	@Schema(description = "کد فرایندابطال", name = "reversalProcessId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	@Column(name = "C_REVERSAL_PROCESS_ID", length = 50)
	private String reversalProcessId;

	@Schema(description = "شماره قرارداد", name = "contractNo", example = "123456", nullable = false)
	@Column(name = "N_CONTRACT_NO")
	private Long contractNo;

	@Column(name = "F_TRADE_ID", nullable = false)
	private Long tradeId;


	@Enumerated(EnumType.STRING)
	@Column(name = "C_ACKNOWLEDGMENT", length = 50, nullable = false)
	@Schema(description = "تاییدیه نوع تسویه و نوع حواله")
	private Acknowledgment acknowledgment = Acknowledgment.UNKNOWN;

	@Schema(description = "ایمیل تاییدیه ارسال شده است یا خیر")
	@Column(name = "IS_RECKONING_SEND", nullable = false)
	private boolean isReckoningSend = false;

	@Schema(description = "تاریخ ارسال ایمیل تاییدیه", name = "reckoningSendDate")
	@Column(name = "D_RECKONING_SEND_DATE")
	private Date reckoningSendDate;


	@Column(name = "C_PMS_BILL_ID")
	private String pmsBillId;

	@Schema(description = "تاریخ ابطال ال سی")
	@Column(name = "D_CANCEL_DATE")
	private Date cancelDate;

	@Schema(description = "دلیل ابطال")
	@Column(name = "C_CANCELLATION_REASON", length = 50)
	@Enumerated(EnumType.STRING)
	private LcCancellationReason cancellationReason;


}