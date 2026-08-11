package com.nicico.internal.sales.bill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Subselect("""
		SELECT DISTINCT
		    ROW_NUMBER() OVER (ORDER BY b.N_REMITTANCE_ID, b.N_TRADE_ID) AS ID,
		    b.N_TRADE_ID AS N_TRADE_ID,
		    b.N_REMITTANCE_ID AS N_REMITTANCE_ID,
		    rm.C_PMS_ID AS C_REMITTANCE_PMS_ID,
		    b.N_CUSTOMER_ID AS N_CUSTOMER_ID,
		    rm.C_CUSTOMER_NAME AS C_CUSTOMER_NAME,
		    rm.C_ECONOMIC_CODE AS C_ECONOMIC_CODE,
		    rm.C_NATIONAL_CODE AS C_NATIONAL_CODE,
		    rm.C_CONTRACT_NO AS C_CONTRACT_NO,
		    rm.C_REMITTANCE_NO AS C_REMITTANCE_NO,
		    rm.D_REMITTANCE_DATE AS D_REMITTANCE_DATE,
		    rm.D_VALIDITY_DATE AS D_VALIDITY_DATE,
		    rm.C_PAYMENT_CODE AS C_PAYMENT_CODE,
		    rm.C_SETTLEMENT_TYPE AS C_SETTLEMENT_TYPE,
		    rm.C_SETTLEMENT_TYPE_DESC AS C_SETTLEMENT_TYPE_DESC,
		    rm.C_OFFER_DESCRIPTION AS C_OFFER_DESCRIPTION,
		    rm.N_CASH_PERCENTAGE AS N_CASH_PERCENTAGE,
		    rm.N_CREDIT_PERCENTAGE AS N_CREDIT_PERCENTAGE,
		    rm.N_REMITTANCE_QUANTITY AS N_TOTAL_QUANTITY,
		    rm.N_GOOD_ID AS N_GOOD_ID,
		    rm.C_GOOD_NAME AS C_GOOD_NAME,
		    rm.N_TAX_AMOUNT AS N_TOTAL_FINAL_AMOUNT
		FROM T_INS_BILLING b
		         INNER JOIN T_INS_REMITTANCE_MASTER rm
		                    ON rm.ID = b.N_REMITTANCE_ID
		""")
@Synchronize({"T_INS_BILLING", "T_INS_REMITTANCE_MASTER"})
public class BillingExtractMasterModel implements Serializable {

	@Serial
	private static final long serialVersionUID = 7294227287250316351L;

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "N_TRADE_ID")
	private Long tradeId;

	@Column(name = "N_REMITTANCE_ID")
	private Long remittanceId;

	@Column(name = "C_REMITTANCE_PMS_ID")
	private String remittancePmsId;

	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;

	@Column(name = "C_CUSTOMER_NAME")
	private String customerName;

	@Column(name = "C_ECONOMIC_CODE")
	private String economicCode;

	@Column(name = "C_NATIONAL_CODE", length = 15)
	private String nationalCode;

	@Column(name = "C_CONTRACT_NO")
	private String contractNo;

	@Column(name = "C_REMITTANCE_NO")
	private String remittanceNo;

	@Column(name = "D_REMITTANCE_DATE")
	private Date remittanceDate;

	@Column(name = "D_VALIDITY_DATE")
	private Date validityDate;

	@Column(name = "C_PAYMENT_CODE")
	private String paymentCode;

	@Column(name = "C_SETTLEMENT_TYPE")
	private String settlementType;

	@Column(name = "C_SETTLEMENT_TYPE_DESC")
	private String settlementTypeDesc;

	@Column(name = "C_OFFER_DESCRIPTION")
	private String offerDescription;

	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;

	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;

	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;

	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "N_GOOD_ID")
	private Long goodId;

	@Column(name = "C_GOOD_NAME")
	private String goodName;
}
