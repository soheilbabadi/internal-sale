package com.nicico.internal.sales.bill.model;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.remittance.enums.IssueSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//خواندن اطلاعات حواله قبل از صورتحساب
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Subselect("""
		    SELECT
		    RM.ID,
		    RM.C_DESCRIPTION,
		    RM.C_REMITTANCE_NO,
		    RM.D_REMITTANCE_DATE,
		    RM.D_VALIDITY_DATE,
		    RM.N_CUSTOMER_ID,
		    RM.C_CUSTOMER_NAME,
		    RM.C_ECONOMIC_CODE,
		    RM.C_NATIONAL_CODE,
		    '01/' || RM.C_NATIONAL_CODE AS CUSTOMER_NOSA_CODE,  -- ✅ Added concatenated field
		    RM.C_TARGET_ADDRESS,
		    RM.N_LOADING_PORT_ID,
		    RM.C_LOADING_PORT,
		    RM.N_GOOD_ID,
		    RM.C_GOOD_NAME,
		    RM.C_PACKING_NAME,
		    RM.N_PACKING_ID,
		    RM.C_LOT_NUMBER,
		    RM.D_CONTRACT_DATE,
		    RM.C_CONTRACT_NO,
		    RM.C_SELLER_BROKER_NAME,
		    RM.C_BUYER_BROKER_NAME,
		    RM.N_CASH_PERCENTAGE,
		    RM.N_CREDIT_PERCENTAGE,
		    RM.N_REMITTANCE_QUANTITY,
		    RM.N_REMITTANCE_QUANTITY_CASH,
		    RM.N_REMITTANCE_QUANTITY_CREDIT,
		    RM.N_REMITTANCE_UNIT_PRICE_CASH,
		    RM.N_REMITTANCE_UNIT_PRICE_CREDIT,
		    RM.D_LC_EXPIRY_DATE,
		    RM.D_SETTLEMENT_DUE_DATE,
		    RM.N_ISSUER_BANK_ID,
		    RM.C_ISSUER_BANK_NAME,
		    RM.C_ISSUER_BANK_BRANCH_NAME,
		    RM.C_ISSUER_BANK_CODE,
		    RM.C_LC_NO,
		    RM.N_ISSUE_PLACE_ID,
		    RM.C_ISSUE_PLACE,
		    RM.C_PROFORMA_NO,
		    RM.D_PROFORMA_DATE,
		    RM.C_PROFORMA_ISSUE_TYPE,
		    RM.C_ISSUE_SOURCE_TYPE,
		    RM.C_ISSUER_NAME,
		    RM.C_PAYMENT_CODE,
		    RM.IS_DELAY_PENALTY,
		    RM.N_TRADING_BANK_ID,
		    RM.C_TRADING_BANK_TITLE,
		    RM.C_TRADING_BRANCH_TITLE,
		    RM.C_PMS_ID,
		    RM.N_TAX_AMOUNT,
		    RM.C_SETTLEMENT_TYPE,
		    RM.C_SETTLEMENT_TYPE_DESC,
		    RM.N_BROKER_ID,
		    RM.C_BROKER_NAME,
		    RM.N_TOTAL_QUANTITY,
		    RM.N_TOTAL_FINAL_AMOUNT,
		    RM.C_OFFER_DESCRIPTION,
		    RM.C_IME_COMMODITY_SYMBOL,
		    TO_NUMBER(SUBSTR(TO_CHAR(SYSDATE, 'YYYY/MM/DD', 'nls_calendar=persian'), 1, 4)) AS N_JALAALI_YEAR,
		    TITV.EMISSION_TAX,
		    60 AS N_STORAGE_DEADLINE,
		    0.05 AS N_STORAGE_COST,
		    6 AS N_CREDIT_EXPIRE_PERIOD,
		    60 AS N_SHIPPING_DEADLINE,
		    21 AS N_PAYMENT_DEFERRAL
		FROM T_INS_REMITTANCE_MASTER RM
		INNER JOIN T_INS_TAX_VAT TITV ON TITV.JALALI_YEAR = TO_NUMBER(SUBSTR(TO_CHAR(SYSDATE, 'YYYY/MM/DD', 'nls_calendar=persian'), 1, 4))
		WHERE RM.C_PMS_ID IS NOT NULL
		  AND RM.C_WORKFLOW_APPROVE_STATUS = 'ACCEPTED'
		""")
public class BillingTradeExtractModel implements Serializable {

	@Serial
	private static final long serialVersionUID = -8364682081445078704L;

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "C_DESCRIPTION", length = 4000)
	private String description;

	@Column(name = "C_REMITTANCE_NO", length = 255)
	private String remittanceNo;

	@Column(name = "D_REMITTANCE_DATE")
	private Date remittanceDate;

	@Column(name = "D_VALIDITY_DATE")
	private Date validityDate;

	@Column(name = "N_CUSTOMER_ID")
	private BigDecimal customerId;

	@Column(name = "C_CUSTOMER_NAME", length = 200)
	private String customerName;

	@Column(name = "C_ECONOMIC_CODE", length = 50)
	private String economicCode;

	@Column(name = "C_NATIONAL_CODE", length = 15)
	private String nationalCode;

	@Column(name = "C_TARGET_ADDRESS", length = 500)
	private String targetAddress;

	@Column(name = "N_LOADING_PORT_ID")
	private Long loadingPortId;

	@Column(name = "C_LOADING_PORT", length = 255)
	private String loadingPort;

	@Column(name = "N_GOOD_ID")
	private Long goodId;

	@Column(name = "C_GOOD_NAME", length = 200)
	private String goodName;

	@Column(name = "C_PACKING_NAME", length = 100)
	private String packingName;

	@Column(name = "N_PACKING_ID")
	private Long packingId;

	@Column(name = "C_LOT_NUMBER", length = 255)
	private String lotNumber;

	@Column(name = "D_CONTRACT_DATE")
	private Date contractDate;

	@Column(name = "C_CONTRACT_NO", length = 50)
	private String contractNo;

	@Column(name = "C_SELLER_BROKER_NAME", length = 200)
	private String sellerBrokerName;

	@Column(name = "C_BUYER_BROKER_NAME", length = 200)
	private String buyerBrokerName;

	@Column(name = "N_CASH_PERCENTAGE")
	private Double cashPercentage;

	@Column(name = "N_CREDIT_PERCENTAGE")
	private Double creditPercentage;

	@Column(name = "N_REMITTANCE_QUANTITY")
	private BigDecimal remittanceQuantity;

	@Column(name = "N_REMITTANCE_QUANTITY_CASH")
	private BigDecimal remittanceQuantityCash;

	@Column(name = "N_REMITTANCE_QUANTITY_CREDIT")
	private BigDecimal remittanceQuantityCredit;

	@Column(name = "N_REMITTANCE_UNIT_PRICE_CASH")
	private BigDecimal remittanceUnitPriceCash;

	@Column(name = "N_REMITTANCE_UNIT_PRICE_CREDIT")
	private BigDecimal remittanceUnitPriceCredit;

	@Column(name = "D_LC_EXPIRY_DATE")
	private Date lcExpiryDate;

	@Column(name = "D_SETTLEMENT_DUE_DATE")
	private Date settlementDueDate;

	@Column(name = "N_ISSUER_BANK_ID")
	private Long issuerBankId;

	@Column(name = "C_ISSUER_BANK_NAME", length = 200)
	private String issuerBankName;

	@Column(name = "C_ISSUER_BANK_BRANCH_NAME", length = 200)
	private String issuerBankBranchName;

	@Column(name = "C_ISSUER_BANK_CODE", length = 200)
	private String issuerBankCode;

	@Column(name = "C_LC_NO", length = 255)
	private String lcNo;

	@Column(name = "N_ISSUE_PLACE_ID")
	private Long issuePlaceId;

	@Column(name = "C_ISSUE_PLACE", length = 50)
	private String issuePlace;

	@Column(name = "C_PROFORMA_NO", length = 50)
	private String proformaNo;

	@Column(name = "D_PROFORMA_DATE")
	private Date proformaDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_PROFORMA_ISSUE_TYPE", length = 255)
	private ProformaIssueType proformaIssueType;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_ISSUE_SOURCE_TYPE", length = 50)
	private IssueSourceType issueSourceType;

	@Column(name = "C_ISSUER_NAME", length = 100)
	private String issuerName;

	@Column(name = "C_PAYMENT_CODE", length = 100)
	private String paymentCode;

	@Column(name = "IS_DELAY_PENALTY")
	private Integer isDelayPenalty;

	@Column(name = "N_TRADING_BANK_ID")
	private Long tradingBankId;

	@Column(name = "C_TRADING_BANK_TITLE", length = 100)
	private String tradingBankTitle;

	@Column(name = "C_TRADING_BRANCH_TITLE", length = 100)
	private String tradingBranchTitle;

	@Column(name = "C_PMS_ID", length = 50)
	private String pmsId;

	@Column(name = "N_TAX_AMOUNT")
	private BigDecimal taxAmount;


	@Column(name = "C_SETTLEMENT_TYPE", length = 100)
	private String settlementType;

	@Column(name = "C_SETTLEMENT_TYPE_DESC", length = 100)
	private String settlementTypeDesc;

	@Column(name = "N_BROKER_ID")
	private Long brokerId;

	@Column(name = "C_BROKER_NAME", length = 100)
	private String brokerName;

	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;

	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "C_OFFER_DESCRIPTION", length = 4000)
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

	// Fields from joined tables
	@Column(name = "N_JALAALI_YEAR")
	private BigDecimal jalaliYear;

	@Column(name = "EMISSION_TAX")
	private BigDecimal emissionTax;

	@Schema(description = "مهلت بارگیری پس از مهلت مجاز بورس کالا-روز")
	@Column(name = "N_STORAGE_DEADLINE")
	private Integer storageDeadline;

	@Schema(description = "هزینه انبارداری روزشمار-درصد")
	@Column(name = "N_STORAGE_COST", precision = 10, scale = 2)
	private BigDecimal storageCost;

	@Schema(description = "سررسید انقضای اعتبار-ماه")
	@Column(name = "N_CREDIT_EXPIRE_PERIOD")
	private Integer creditExpirePeriod;

	@Schema(description = "مهلت بارگیری-روز")
	@Column(name = "N_SHIPPING_DEADLINE")
	private Integer shippingDeadline;

	@Schema(description = "مهلت پرداخت وجه اعتبار اسنادی-روز")
	@Column(name = "N_PAYMENT_DEFERRAL")
	private Integer paymentDeferral;

	@Column(name = "CUSTOMER_NOSA_CODE", length = 100)
	private String customerNosaCode;

}