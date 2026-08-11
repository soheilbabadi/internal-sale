package com.nicico.internal.sales.crm.model;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.SaleType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Subselect("""
		    SELECT 
		        lc.ID AS LC_ID,
		        lc.C_PERFORMA_NO,
		        lc.C_PERFORMA_DATE,
		        lc.N_CONTRACT_NO,
		        lc.C_LC_NO,
		        lc.D_LC_DATE,
		        lc.N_TRADING_BANK_ID,
		        lc.C_TRADING_BANK_TITLE,
		        lc.C_TRADING_BRANCH_TITLE,
		        lc.N_CREDIT_EXPIRE_PERIOD,
		        lc.N_PAYMENT_DEFERRAL,
		        lc.N_DEADLINE_DAYS,
		        lc.C_WORKFLOW_APPROVE_STATUS,
		        lc.D_LC_EXPIRY_DATE,
		        lc.D_SETTLEMENT_DUE_DATE,
		        lc.N_ISSUER_BANK_ID,
		        lc.C_ISSUER_BANK_NAME,
		        lc.C_ISSUER_BANK_BRANCH_NAME,
		        lc.C_ISSUER_BANK_CODE,
		        lc.C_LC_FILE_ID,
		        lc.C_DISPATCH_FILE_ID,
		        lc.C_PROFORMA_FILE_ID,
		        lc.N_PROFORMA_MASTER_ID,
		        lc.N_PROFORMA_DETAIL_ID,
		        lc.C_PROCESS_ID AS  C_LC_INSTANCE_ID,
		        lc.C_NOTIFICATION_DOCUMENT_ID,
		        lc.C_ACKNOWLEDGMENT,
		        lc.IS_RECKONING_SEND,
		        lc.D_RECKONING_SEND_DATE,
		        lc.D_CANCEL_DATE,
		        lc.C_CANCELLATION_REASON,
		        lc.C_CONTRACT_DATE,
		        lc.N_BROKER_ID,
		        lc.C_BROKER_NAME,    
		        lc.N_TOTAL_QUANTITY,
		        lc.N_TOTAL_FINAL_AMOUNT,
		        lc.C_OFFER_DESCRIPTION,
		        lc.C_IME_COMMODITY_SYMBOL,
		        lc.N_GOOD_ID,
		        lc.C_GOOD_NAME,    
		        pm.N_CASH_PERCENTAGE,
		        pm.N_CREDIT_PERCENTAGE,
		        pm.N_TOTAL_VAT_AMOUNT,
		        pm.N_TOTAL_CASH_AMOUNT,
		        pm.N_TOTAL_CREDIT_AMOUNT,
		        pm.N_CUSTOMER_ID,
		        pm.C_CUSTOMER_NAME,
		        pm.C_NATIONAL_CODE,
		        pm.C_PHONE,
		        pm.C_ECONOMIC_CODE,
		        pm.C_REGISTER_NUMBER,
		        pm.C_POST_CODE,
		        pm.C_ADDRESS,
		        pm.F_TRADE_ID,
		        pd.ID AS PD_ID,
		        pd.N_JALAALI_YEAR,
		        pd.N_FINAL_PRICE,
		        pd.D_PERFORMA_DATE AS PD_D_PERFORMA_DATE,
		        pd.C_SALE_TYPE,
		        pd.N_TOTAL_AMOUNT,
		        pd.N_STORAGE_DEADLINE,
		        pd.D_ORDER_DATE,
		        pd.N_STORAGE_COST
		    FROM T_INS_LC lc 
		    INNER JOIN T_INS_PERFORMA_MASTER pm ON lc.N_PROFORMA_MASTER_ID = pm.ID 
		    INNER JOIN T_INS_PERFORMA_DETAIL pd ON pd.F_PERFORMA_MASTER_ID = pm.ID
		""")
@Synchronize({"T_INS_LC", "T_INS_PERFORMA_MASTER", "T_INS_PERFORMA_DETAIL"})
public class LcWithProformaView {

	@Id
	@Column(name = "LC_ID")
	private Long lcId;

	@Column(name = "C_PERFORMA_NO")
	private String performaNo;

	@Column(name = "C_PERFORMA_DATE")
	private String performaDate;

	@Column(name = "N_CONTRACT_NO")
	private Long contractNo;

	@Column(name = "C_LC_NO")
	private String lcNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_LC_DATE")
	private Date lcDate;

	@Column(name = "N_TRADING_BANK_ID")
	private Long tradingBankId;

	@Column(name = "C_TRADING_BANK_TITLE")
	private String tradingBankTitle;

	@Column(name = "C_TRADING_BRANCH_TITLE")
	private String tradingBankBranchTitle;

	@Column(name = "N_CREDIT_EXPIRE_PERIOD")
	private Integer creditExpirePeriod;

	@Column(name = "N_PAYMENT_DEFERRAL")
	private Integer paymentDeferral;

	@Column(name = "N_DEADLINE_DAYS")
	private Integer deadlineDays;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS")
	private WorkflowApproveStatus workflowApproveStatus;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_LC_EXPIRY_DATE")
	private Date lcExpiryDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_SETTLEMENT_DUE_DATE")
	private Date settlementDueDate;

	@Column(name = "N_ISSUER_BANK_ID")
	private Long issuerBankId;

	@Column(name = "C_ISSUER_BANK_NAME")
	private String issuerBankName;

	@Column(name = "C_ISSUER_BANK_BRANCH_NAME")
	private String issuerBankBranchName;

	@Column(name = "C_ISSUER_BANK_CODE")
	private String issuerBankCode;

	@Column(name = "C_LC_FILE_ID")
	private String lcAttachmentId;

	@Column(name = "C_DISPATCH_FILE_ID")
	private String dispatchAttachmentId;

	@Column(name = "C_PROFORMA_FILE_ID")
	private String proformaFileId;

	@Column(name = "N_PROFORMA_MASTER_ID")
	private Long proformaMasterId;

	@Column(name = "N_PROFORMA_DETAIL_ID")
	private Long proformaDetailId;

	@Column(name = "C_LC_INSTANCE_ID")
	private String lcInstanceId;

	@Column(name = "C_NOTIFICATION_DOCUMENT_ID")
	private String notificationDocumentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_ACKNOWLEDGMENT")
	private Acknowledgment acknowledgment;

	@Column(name = "IS_RECKONING_SEND")
	private Boolean isReckoningSend;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_RECKONING_SEND_DATE")
	private Date reckoningSendDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_CANCEL_DATE")
	private Date cancelDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_CANCELLATION_REASON")
	private LcCancellationReason lcCancellationReason;

	@Column(name = "C_CONTRACT_DATE")
	private String contractDate;

	@Column(name = "N_BROKER_ID")
	private Long brokerId;

	@Column(name = "C_BROKER_NAME")
	private String brokerName;

	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;

	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "C_OFFER_DESCRIPTION")
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL")
	private String imeCommoditySymbol;

	@Column(name = "N_GOOD_ID")
	private Long goodId;

	@Column(name = "C_GOOD_NAME")
	private String goodName;

	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;

	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;

	@Column(name = "N_TOTAL_VAT_AMOUNT")
	private BigDecimal totalVatAmount;

	@Column(name = "N_TOTAL_CASH_AMOUNT")
	private BigDecimal totalCashAmount;

	@Column(name = "N_TOTAL_CREDIT_AMOUNT")
	private BigDecimal totalCreditAmount;

	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;

	@Column(name = "C_CUSTOMER_NAME")
	private String customerName;

	@Column(name = "C_NATIONAL_CODE")
	private String nationalCode;

	@Column(name = "C_PHONE")
	private String phone;

	@Column(name = "C_ECONOMIC_CODE")
	private String economicCode;

	@Column(name = "C_REGISTER_NUMBER")
	private String registerNumber;

	@Column(name = "C_POST_CODE")
	private String postCode;

	@Column(name = "C_ADDRESS")
	private String address;

	@Column(name = "F_TRADE_ID")
	private Long tradeId;

	// Proforma Detail fields
	@Column(name = "PD_ID")
	private Long performaDetailId;

	@Column(name = "N_JALAALI_YEAR")
	private Integer jalaaliYear;

	@Column(name = "N_FINAL_PRICE")
	private BigDecimal finalPrice;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PD_D_PERFORMA_DATE")
	private Date performaDetailDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_SALE_TYPE")
	private SaleType saleType;

	@Column(name = "N_TOTAL_AMOUNT")
	private BigDecimal totalAmount;

	@Column(name = "N_STORAGE_DEADLINE")
	private Integer storageDeadline;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_ORDER_DATE")
	private Date orderDate;

	@Column(name = "N_STORAGE_COST")
	private BigDecimal storageCost;
}