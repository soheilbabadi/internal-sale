package com.nicico.internal.sales.trade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
		SELECT DISTINCT
		    tit.ID,
		    tit.BUYER_NAME,
		    tit.BUYER_NATIONAL_CODE,
		    tit.COMMODITY_CODE,
		    COALESCE(tig.SYMBOL, '-') AS COMMODITY_SYMBOL,
		    tig.PERSIAN_NAME AS C_NAME,
		    COALESCE(DBMS_LOB.SUBSTR(tio.DESCRIPTION, 4000, 1), 'بدون توضیحات') AS C_OFFER_DESCRIPTION,
		    COALESCE(tigb.N_CASH_PERCENTAGE, 0) AS N_CASH_PERCENTAGE,
		    COALESCE(100 - tigb.N_CASH_PERCENTAGE, 0) AS N_CREDIT_PERCENTAGE,
		    COALESCE(tigb.N_COMMISSION, 0) AS N_COMMISSION,
		    tit.CONTRACT_DATE,
		    tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO AS C_CONTRACT_NO,
		    tit.PAYMENT_CODE,
		    255 AS SETTLEMENT_TYPE,
		    COALESCE(s.SETTLEMENT_TYPE, 'نامشخص') AS SETTLEMENT_TYPE_DESC,
		    tit.UNIT_COUNT * 1000 AS UNIT_COUNT,
		    tit.UNIT_PRICE,
		    tit.UNIT_PRICE * (1 + COALESCE(tigb.N_COMMISSION, 0)) AS CREDIT_UNIT_PRICE,
		    COALESCE(titv.VAT_COEFFICIENT, 0) AS VAT_COEFFICIENT,
		    COALESCE((100 - COALESCE(tigb.N_CASH_PERCENTAGE, 0)) / 100.0 *
		             (tit.UNIT_COUNT * 1000) *
		             ((COALESCE(tigb.N_COMMISSION, 0) + 100.0) / 100.0) *
		             tit.UNIT_PRICE, 0) / NULLIF(titv.VAT_COEFFICIENT, 0) AS VAT_AMOUNT,
		    COALESCE((100 - COALESCE(tigb.N_CASH_PERCENTAGE, 0)) / 100.0 *
		             (tit.UNIT_COUNT * 1000) *
		             ((COALESCE(tigb.N_COMMISSION, 0) + 100.0) / 100.0) *
		             tit.UNIT_PRICE, 0) AS N_CREDIT_AMOUNT,
		    COALESCE(COALESCE(tigb.N_CASH_PERCENTAGE, 0) / 100.0 *
		             (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE, 0) AS N_CASH_AMOUNT,
		    COALESCE(
		            (100 - COALESCE(tigb.N_CASH_PERCENTAGE, 0)) / 100.0 *
		            (tit.UNIT_COUNT * 1000) *
		            ((COALESCE(tigb.N_COMMISSION, 0) + 100.0) / 100.0) *
		            tit.UNIT_PRICE +
		            (COALESCE(tigb.N_CASH_PERCENTAGE, 0) / 100.0 *
		             (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE), 0) AS FINAL_AMOUNT,
		    COALESCE(tipm.C_WORKFLOW_APPROVE_STATUS, 'NOT_STARTED') AS PREINVOCE_STATUS,
		    'NOT_STARTED' AS LC_STATUS,
		    COALESCE(tipm.ID, 0) AS MASTER_ID
		FROM TBL_IME_TRADE tit
		         INNER JOIN TBL_IME_PS_COMMODITIES tig ON tit.COMMODITY_CODE = tig.ID
		         LEFT JOIN (
		    SELECT tigb.*,
		           ROW_NUMBER() OVER (PARTITION BY C_IME_COMMODITY_SYMBOL ORDER BY D_START_DATE DESC) AS rn
		    FROM T_INS_GOODS_BUCKET tigb
		    WHERE tigb.D_EXPIRE_DATE IS NULL
		) tigb ON tigb.C_IME_COMMODITY_SYMBOL = tig.SYMBOL
		    AND tigb.rn = 1
		    AND TRUNC(tigb.D_START_DATE) <= TRUNC(TO_DATE(tit.CONTRACT_DATE, 'YYYY/MM/DD', 'NLS_CALENDAR=PERSIAN'))
		         LEFT JOIN T_INS_TAX_VAT titv ON SUBSTR(tit.CONTRACT_DATE, 1, 4) = titv.JALALI_YEAR
		         LEFT JOIN TBL_IME_SETTLEMENT s ON s.PAYMENT_CODE = tit.PAYMENT_CODE
		         LEFT JOIN (
		    SELECT x.*
		    FROM (
		        SELECT tipm.*,
		               ROW_NUMBER() OVER (PARTITION BY tipm.C_PAYMENT_CODE ORDER BY tipm.ID DESC) AS rn
		        FROM T_INS_PERFORMA_MASTER tipm
		    ) x
		    WHERE x.rn = 1
		      AND (x.C_WORKFLOW_APPROVE_STATUS IS NULL OR x.C_WORKFLOW_APPROVE_STATUS NOT IN ('ACCEPTED'))
		) tipm ON tit.PAYMENT_CODE = tipm.C_PAYMENT_CODE
		         LEFT JOIN TBL_IME_PS_OFFERS tio ON tit.OFFER_CODE = tio.ID
		WHERE tit.CONTRACT_DATE > '1405/01/01'
		  AND tit.CURRENCY_CODE = 1
		  AND s.PAYMENT_CODE IS NULL
		  AND NOT EXISTS (
		    SELECT 1
		    FROM (
		        SELECT tipm_check.*,
		               ROW_NUMBER() OVER (PARTITION BY tipm_check.C_PAYMENT_CODE ORDER BY tipm_check.ID DESC) AS rn
		        FROM T_INS_PERFORMA_MASTER tipm_check
		    ) latest
		    WHERE latest.C_PAYMENT_CODE = tit.PAYMENT_CODE  AND latest.rn = 1    AND latest.C_WORKFLOW_APPROVE_STATUS IN ('ACCEPTED')
		  )
		ORDER BY tit.CONTRACT_DATE DESC
		""")
public class TradeExtractStartProformaModel implements Serializable {

	@Serial
	private static final long serialVersionUID = 2725178981743094250L;
	@Id
	private Long id;
	@Column(name = "BUYER_NAME")
	private String buyerName;
	@Column(name = "BUYER_NATIONAL_CODE")
	private String buyerNationalCode;
	@Column(name = "COMMODITY_CODE")
	private Long commodityCode;
	@Column(name = "COMMODITY_SYMBOL")
	private String commoditySymbol;
	@Column(name = "C_NAME")
	private String commodityName;
	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;
	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;
	@Column(name = "N_COMMISSION")
	private Double commission;
	@Column(name = "CONTRACT_DATE")
	private String contractDate;
	@Column(name = "C_CONTRACT_NO")
	private String contractNo;
	@Column(name = "PAYMENT_CODE")
	private String paymentCode;
	@Column(name = "SETTLEMENT_TYPE")
	private Integer settlementType;
	@Column(name = "SETTLEMENT_TYPE_DESC")
	private String settlementTypeDesc;
	@Column(name = "UNIT_COUNT")
	private Integer unitCount;
	@Column(name = "UNIT_PRICE")
	private Double unitPrice;
	@Column(name = "CREDIT_UNIT_PRICE")
	private Double creditUnitPrice;
	@Column(name = "VAT_COEFFICIENT")
	private Double vatCoefficient;
	@Column(name = "VAT_AMOUNT")
	private Double vatAmount;
	@Column(name = "N_CREDIT_AMOUNT")
	private Double creditAmount;
	@Column(name = "N_CASH_AMOUNT")
	private Double cashAmount;
	@Column(name = "FINAL_AMOUNT")
	private Double finalAmount;
	@Column(name = "PREINVOCE_STATUS")
	private String preInvoiceStatus;
	@Column(name = "LC_STATUS")
	private String lcStatus;
	@Column(name = "MASTER_ID")
	private Long masterId;
	@Column(name = "C_OFFER_DESCRIPTION")
	private String offerDescription;
}
