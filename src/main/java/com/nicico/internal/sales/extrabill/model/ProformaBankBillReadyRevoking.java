package com.nicico.internal.sales.extrabill.model;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
SELECT
    tpbb.ID,
    tpbb.C_ISSUER_BANK_NAME,
    tpbb.C_BRANCH_CODE,
    tpbb.C_BRANCH_NAME,
    tpbb.C_PAYMENT_CITY,
    tpbb.C_AGENT_BANK_NAME,
    tpbb.C_NOSA_CODE,
    tpbb.C_SEPAM_CODE,
    tpbb.C_TREASURY_ID,
    tpbb.D_ISSUE_DATE,
    tpbb.D_DUE_DATE,
    tpbb.N_CONTRACT_NO,
    tpbb.F_TRADE_ID,
    tpbb.F_PERFORMA_MASTER_ID,
    tpbb.C_WORKFLOW_APPROVE_STATUS AS BILL_STATUS,
    tpbb.C_PROCESS_ID,
    tpbb.C_ACKNOWLEDGMENT,
    tpbb.IS_RECKONING_SEND,
    tpbb.D_RECKONING_SEND_DATE,
    tpbb.C_PMS_BILL_ID,
    tpbb.D_CANCEL_DATE,
    tpbb.C_CANCELLATION_REASON,
    tit.BUYER_NAME,
    tit.BUYER_NATIONAL_CODE,
    tit.COMMODITY_CODE,
    tit.CONTRACT_DATE AS TRADE_CONTRACT_DATE,
    tit.PAYMENT_CODE,
    tipm.C_CUSTOMER_NAME,
    tipm.c_national_code AS CUSTOMER_NATIONAL_CODE,
    tipm.C_GOOD_NAME,
    tipm.N_TOTAL_FINAL_AMOUNT,
    tipm.N_TOTAL_CASH_AMOUNT,
    tipm.N_TOTAL_CREDIT_AMOUNT,
    tipm.C_WORKFLOW_APPROVE_STATUS AS PROFORMA_STATUS,
    tipm.N_BROKER_ID,
    tipm.C_BROKER_NAME,
    tipm.C_BROKER_NATIONAL_CODE,
    tipm.N_TOTAL_QUANTITY,
    tipm.C_OFFER_DESCRIPTION,
    tipm.C_IME_COMMODITY_SYMBOL,
    tipm.N_GOOD_ID,
    tipd.C_PERFORMA_NO,
    tipd.D_PERFORMA_DATE

FROM T_INS_EXTRA_BANK_BILL tpbb
         LEFT JOIN TBL_IME_TRADE tit ON tit.ID = tpbb.F_TRADE_ID
         INNER JOIN T_INS_PERFORMA_MASTER tipm ON tipm.ID = tpbb.F_PERFORMA_MASTER_ID
         INNER JOIN T_INS_PERFORMA_DETAIL tipd ON tipd.F_PERFORMA_MASTER_ID = tipm.ID
WHERE NOT EXISTS (
    SELECT 1
    FROM TBL_IME_SETTLEMENT tis
    WHERE tis.PAYMENT_CODE = tipm.C_PAYMENT_CODE
      AND tis.SETTLEMENT_TYPE IN ('انفساخ', 'نقدی')
)


		""")
public class ProformaBankBillReadyRevoking implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column(name = "C_ISSUER_BANK_NAME")
	private String issuerBankName;

	@Column(name = "C_BRANCH_CODE")
	private String branchCode;

	@Column(name = "C_BRANCH_NAME")
	private String branchName;

	@Column(name = "C_PAYMENT_CITY")
	private String paymentCity;

	@Column(name = "C_AGENT_BANK_NAME")
	private String agentBankName;


	@Column(name = "C_NOSA_CODE")
	private String nosaCode;

	@Column(name = "C_SEPAM_CODE")
	private String sepamCode;

	@Column(name = "C_TREASURY_ID")
	private String treasuryId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_ISSUE_DATE")
	private Date issueDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_DUE_DATE")
	private Date dueDate;

	@Column(name = "N_CONTRACT_NO")
	private Long contractNo;

	@Column(name = "F_TRADE_ID")
	private Long tradeId;

	@Column(name = "F_PERFORMA_MASTER_ID")
	private Long proformaMasterId;

	@Enumerated(EnumType.STRING)
	@Column(name = "BILL_STATUS")
	private WorkflowApproveStatus billStatus;

	@Column(name = "C_PROCESS_ID")
	private String processId;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_ACKNOWLEDGMENT")
	private Acknowledgment acknowledgment;

	@Column(name = "IS_RECKONING_SEND")
	private boolean isReckoningSend;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_RECKONING_SEND_DATE")
	private Date reckoningSendDate;

	@Column(name = "C_PMS_BILL_ID")
	private String pmsBillId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_CANCEL_DATE")
	private Date cancelDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_CANCELLATION_REASON")
	private LcCancellationReason cancellationReason;

	@Column(name = "BUYER_NAME")
	private String buyerName;

	@Column(name = "BUYER_NATIONAL_CODE")
	private String buyerNationalCode;

	@Column(name = "COMMODITY_CODE")
	private Long commodityCode;

	@Column(name = "TRADE_CONTRACT_DATE")
	private String tradeContractDate;

	@Column(name = "PAYMENT_CODE")
	private String paymentCode;

	@Column(name = "C_CUSTOMER_NAME")
	private String customerName;

	@Column(name = "CUSTOMER_NATIONAL_CODE")
	private String customerNationalCode;

	@Column(name = "C_GOOD_NAME")
	private String goodName;

	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "N_TOTAL_CASH_AMOUNT")
	private BigDecimal totalCashAmount;

	@Column(name = "N_TOTAL_CREDIT_AMOUNT")
	private BigDecimal totalCreditAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "PROFORMA_STATUS")
	private WorkflowApproveStatus proformaStatus;

	// فیلدهای جدید اضافه شده از پیش فاکتور
	@Column(name = "N_BROKER_ID")
	private Long brokerId;

	@Column(name = "C_BROKER_NAME")
	private String brokerName;

	@Column(name = "C_BROKER_NATIONAL_CODE")
	private String brokerNationalCode;

	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;

	@Column(name = "C_OFFER_DESCRIPTION")
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL")
	private String imeCommoditySymbol;

	@Column(name = "N_GOOD_ID")
	private Long goodId;

	@Schema(description = "شماره پیش فاکتور")
	@Column(name = "C_PERFORMA_NO")
	private String performaNo;
	@Schema(description = "تاریخ پیش فاکتور")
	@Column(name = "D_PERFORMA_DATE")
	private Date performaDate;
}
