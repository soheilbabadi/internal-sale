package com.nicico.internal.sales.lc.model;

import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

@Audited
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "T_INS_LC", uniqueConstraints = @UniqueConstraint(columnNames = "C_LC_NO"))
public class LcModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 6313070493614504250L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_LC")
	@SequenceGenerator(name = "SEQ_INS_LC", sequenceName = "SEQ_INS_LC", allocationSize = 1, initialValue = 1000)
	private Long id;

	@Schema(description = "شماره پیش فاکتور")
	@Column(name = "C_PERFORMA_NO", nullable = false)
	private String performaNo;

	@Schema(description = "تاریخ پیش فاکتور")
	@Column(name = "C_PERFORMA_DATE")
	private String performaDate;

	@Schema(description = "تاریخ قرارداد")
	@Column(name = "C_CONTRACT_DATE", length = 20)
	private String contractDate;


	@Column(name = "N_CONTRACT_NO", unique = true)
	private Long contractNo;

	@Column(name = "C_PAYMENT_CODE")
	private String paymentCode;

	@Schema(description = "شماره اعتبار اسنادی")
	@Column(name = "C_LC_NO")
	private String lcNo;

	@Schema(description = "تاریخ گشایش اعتبار")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_LC_DATE")
	private Date lcDate;

	@Schema(name = "شناسه بانک")
	@Column(name = "N_TRADING_BANK_ID")
	private Long tradingBankId;

	@ManyToOne(targetEntity = TradingBankModel.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "N_TRADING_BANK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private TradingBankModel tradingBankModel;

	@Schema(name = "نام بانک")
	@Column(name = "C_TRADING_BANK_TITLE")
	private String tradingBankTitle;

	@Schema(name = "عنوان شعبه")
	@Column(name = "C_TRADING_BRANCH_TITLE")
	private String tradingBankBranchTitle;

	@Schema(description = "سررسید انقضای اعتبار-ماه")
	@Column(name = "N_CREDIT_EXPIRE_PERIOD", nullable = false)
	private Integer creditExpirePeriod;

	@Schema(description = "مهلت پرداخت وجه اعتبار اسنادی-روز")
	@Column(name = "N_PAYMENT_DEFERRAL", nullable = false)
	private Integer paymentDeferral;

	@Schema(description = "مدت اعتبار پیش فاکتور-روز")
	@Column(name = "N_DEADLINE_DAYS", nullable = false)
	private Integer deadlineDays = 7;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100)
	private WorkflowApproveStatus workflowApproveStatus;

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

	@Column(name = "C_PROCESS_ID", length = 50, nullable = false)
	private String processId;

	@Column(name = "D_LC_EXPIRY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	private Date lcExpiryDate;

	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_SETTLEMENT_DUE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date settlementDueDate;

	@Column(name = "N_ISSUER_BANK_ID")
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;

	@Column(name = "C_ISSUER_BANK_NAME", length = 100)
	@Schema(name = "نام بانک بانک گشایش اعتبار اسنادی")
	private String issuerBankName;

	@Column(name = "C_ISSUER_BANK_BRANCH_NAME", length = 200)
	@Schema(name = "نام شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchName;

	@Column(name = "C_ISSUER_BANK_CODE", length = 200)
	@Schema(name = "کد شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchCode;

	@Column(name = "C_LC_FILE_ID", length = 100)
	private String lcAttachmentId;

	@Column(name = "C_DISPATCH_FILE_ID", length = 100)
	private String dispatchAttachmentId;

	@Column(name = "C_PROFORMA_FILE_ID", length = 100)
	private String proformaFileId;

	@JoinColumn(name = "N_PROFORMA_MASTER_ID", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@EqualsAndHashCode.Exclude
	private ProformaMasterModel ProformaMasterModel;

	@Column(name = "N_PROFORMA_MASTER_ID")
	private long proformaMasterId;

	@Column(name = "N_PROFORMA_DETAIL_ID")
	private long proformaDetailId;

	@Column(name = "C_LC_INSTANCE_ID", length = 100)
	private String lcInstanceId;

	@Column(name = "IS_REQUIRE_DISPATCH_FILE")
	private Boolean requireDispatchFile;

	@Schema(description = "شناسه سند ابلاغیه", name = "notificationDocumentId", example = "12345")
	@Column(name = "C_NOTIFICATION_DOCUMENT_ID", length = 100)
	private String notificationDocumentId;

	@Column(name = "C_NOSA_CODE", length = 100)
	private String nosaCode;

	@ManyToOne(targetEntity = IssuingBankModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "N_ISSUER_BANK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private IssuingBankModel issuingBankModel;

	@Column(name = "C_PMS_LC_ID")
	private String pmsLcId;

	@Schema(description = "تاریخ ابطال ال سی")
	@Column(name = "D_CANCEL_DATE")
	private Date cancelDate;

	@Schema(description = "دلیل ابطال")
	@Column(name = "C_CANCELLATION_REASON", length = 50)
	@Enumerated(EnumType.STRING)
	private LcCancellationReason lcCancellationReason;


	@Column(name = "N_BROKER_ID")
	private Long brokerId;

	@Column(name = "C_BROKER_NAME", length = 100)
	private String brokerName;

	@Column(name = "C_BROKER_NATIONAL_CODE", length = 100)
	private String brokerNationalCode;


	@Schema(description = "مقدار کل")
	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;


	@Schema(description = "قیمت نهایی")
	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "C_OFFER_DESCRIPTION", length = 4000)
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

	@Schema(description = "کد مشتری", name = "customerId", example = "1")
	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;

	@Schema(description = "نام مشتری", name = "customerName", example = "مهدی حسینی")
	@Column(name = "C_CUSTOMER_NAME")
	private String customerName;


	@Column(name = "N_GOOD_ID")
	@Schema(description = "شناسه کالا", name = "goodId")
	private Long goodId;

	@Schema(description = "نام کالا", name = "goodName")
	@Column(name = "C_GOOD_NAME", length = 100)
	private String goodName;

}
