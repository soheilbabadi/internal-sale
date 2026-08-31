package com.nicico.internal.sales.extrabill.model;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
				WITH filtered_master AS (
		    SELECT
		        ROW_NUMBER() OVER (ORDER BY tipm.ID) AS ID,
		        tipm.ID                              AS N_MASTER_ID,
		        tipd.C_PERFORMA_NO,
		        tipd.N_FINAL_PRICE,
		        tipd.D_PERFORMA_DATE,
		        tipm.C_CONTRACT_DATE,
		        tipm.N_CONTRACT_NO,
		        tipm.C_NATIONAL_CODE,
		        tipm.C_PAYMENT_CODE,
		        tipm.N_CUSTOMER_ID,
		        tipm.C_CUSTOMER_NAME,
		        tipm.C_ECONOMIC_CODE,
		        tipm.N_GOOD_ID,
		        tipm.C_GOOD_NAME,
		        tipm.N_CREDIT_PERCENTAGE,
		        tipm.N_CASH_PERCENTAGE,
		        tipm.N_TOTAL_CASH_AMOUNT,
		        tipm.N_TOTAL_CREDIT_AMOUNT,
		        tipm.N_TOTAL_VAT_AMOUNT,
		        tipm.N_TOTAL_QUANTITY,
		        tipm.N_TOTAL_FINAL_AMOUNT,
		        tipm.F_TRADE_ID,
		        tipm.C_PROFORMA_ISSUE_TYPE,
		        tipm.N_BROKER_ID,
		        tipm.C_BROKER_NAME,
		        tipm.C_BROKER_NATIONAL_CODE,
		        tipm.C_OFFER_DESCRIPTION,
		        tipm.C_IME_COMMODITY_SYMBOL,
		        NVL(tipm.C_WORKFLOW_APPROVE_STATUS, 'NOT_STARTED') AS C_WORKFLOW_APPROVE_STATUS
		    FROM T_INS_PERFORMA_MASTER tipm
		             INNER JOIN T_INS_PERFORMA_DETAIL tipd ON tipm.ID = tipd.F_PERFORMA_MASTER_ID
		    WHERE tipm.C_PROFORMA_ISSUE_TYPE = 'EXTRA_BILL_OF_EXCHANGE'
		      AND tipm.C_WORKFLOW_APPROVE_STATUS = 'ACCEPTED'
		      AND NOT EXISTS (
		        SELECT 1 FROM TBL_IME_SETTLEMENT
		        WHERE PAYMENT_CODE = tipm.C_PAYMENT_CODE
		          AND SETTLEMENT_TYPE IN ('انفساخ', 'نقدی')
		    )
		
		      AND NOT EXISTS (
		        SELECT 1 FROM T_INS_EXTRA_BANK_BILL tipbb WHERE tipbb.F_PERFORMA_MASTER_ID = tipm.ID AND tipbb.C_WORKFLOW_APPROVE_STATUS IN ('ACCEPTED','IN_PROGRESS')
		
		    )
		)
		SELECT fm.*
		FROM filtered_master fm
		WHERE NOT EXISTS (
		    SELECT 1
		    FROM T_INS_EXTRA_BANK_BILL tpbb
		    WHERE tpbb.F_PERFORMA_MASTER_ID = fm.N_MASTER_ID
		      AND tpbb.C_WORKFLOW_APPROVE_STATUS != 'CANCELED'
		)
		
		""")
public class ExtraBillIssueProviderModel implements Serializable {
	@Serial
	private static final long serialVersionUID = 2583268658804044631L;
	@Id
	@Schema(description = "شناسه")
	@Column(name = "ID")
	private Long id;

	@Column(name = "N_MASTER_ID")
	private Long masterId;

	@Schema(description = "شماره پیش فاکتور")
	@Column(name = "C_PERFORMA_NO")
	private String proformaNo;
	@Schema(description = "قیمت نهایی")
	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;
	@Schema(description = "تاریخ پیش فاکتور")
	@Column(name = "D_PERFORMA_DATE")
	private Date performaDate;
	@Schema(description = "تاریخ قرارداد")
	@Column(name = "C_CONTRACT_DATE", length = 20)
	private String contractDate;
	@Schema(description = "شماره قرارداد")
	@Column(name = "N_CONTRACT_NO")
	private Long contractNo;
	@Schema(description = "کد ملی")
	@Column(name = "C_NATIONAL_CODE", length = 10)
	private String nationalCode;
	@Schema(description = "کد پرداخت")
	@Column(name = "C_PAYMENT_CODE", length = 50)
	private String paymentCode;
	@Schema(description = "شناسه مشتری")
	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;
	@Schema(description = "نام مشتری")
	@Column(name = "C_CUSTOMER_NAME", length = 255)
	private String customerName;
	@Schema(description = "کد اقتصادی")
	@Column(name = "C_ECONOMIC_CODE", length = 12)
	private String economicCode;
	@Schema(description = "شناسه کالا")
	@Column(name = "N_GOOD_ID")
	private Long goodId;
	@Schema(description = "نام کالا")
	@Column(name = "C_GOOD_NAME", length = 255)
	private String goodName;
	@Schema(description = "درصد اعتباری")
	@Column(name = "N_CREDIT_PERCENTAGE", precision = 5, scale = 2)
	private BigDecimal creditPercentage;
	@Schema(description = "درصد نقدی")
	@Column(name = "N_CASH_PERCENTAGE", precision = 5, scale = 2)
	private BigDecimal cashPercentage;
	@Schema(description = "مبلغ کل نقدی")
	@Column(name = "N_TOTAL_CASH_AMOUNT")
	private BigDecimal totalCashAmount;
	@Schema(description = "مبلغ کل اعتباری")
	@Column(name = "N_TOTAL_CREDIT_AMOUNT")
	private BigDecimal totalCreditAmount;
	@Schema(description = "مبلغ کل مالیات")
	@Column(name = "N_TOTAL_VAT_AMOUNT")
	private BigDecimal totalVatAmount;
	@Schema(description = "مقدار کل")
	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;
	@Schema(description = "شناسه معامله")
	@Column(name = "F_TRADE_ID")
	private Long tradeId;
	@Schema(description = "نوع پیش فاکتور")
	@Column(name = "C_PROFORMA_ISSUE_TYPE")
	private String proformaIssueType;
	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100)
	private WorkflowApproveStatus workflowApproveStatus;


	@Column(name = "N_BROKER_ID")
	private Long brokerId;

	@Column(name = "C_BROKER_NAME", length = 100)
	private String brokerName;

	@Column(name = "C_BROKER_NATIONAL_CODE", length = 100)
	private String brokerNationalCode;

	@Column(name = "C_OFFER_DESCRIPTION", length = 4000)
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

}