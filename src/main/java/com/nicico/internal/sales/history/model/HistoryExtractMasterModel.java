package com.nicico.internal.sales.history.model;


import com.nicico.internal.sales.lc.enums.Acknowledgment;
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
		            SELECT DISTINCT
		    tit.ID,
		    tit.BUYER_NAME AS C_BUYER_NAME,
		    tit.BUYER_NATIONAL_CODE AS C_BUYER_NATIONAL_CODE,
		    tit.COMMODITY_CODE AS N_COMMODITY_CODE,
		    COALESCE(tig.SYMBOL, '-') AS C_COMMODITY_SYMBOL,
		    tig.PERSIAN_NAME AS C_NAME,
		    COALESCE(DBMS_LOB.SUBSTR(tio.DESCRIPTION, 4000, 1), 'بدون توضیحات') AS C_OFFER_DESCRIPTION,
		    COALESCE(tigb.N_CASH_PERCENTAGE, 0) AS N_CASH_PERCENTAGE,
		    COALESCE(100 - tigb.N_CASH_PERCENTAGE, 0) AS N_CREDIT_PERCENTAGE,
		    COALESCE(tigb.N_COMMISSION, 0) AS N_COMMISSION,
		    tit.CONTRACT_DATE,
		    tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO AS CONTRACT_NO,
		    tit.PAYMENT_CODE,
		    COALESCE(ts.SETTLEMENT_TYPE, 'نامشخص')  AS SETTLEMENT_TYPE,
		    COALESCE(ts.SETTLEMENT_TYPE, 'نامشخص') AS SETTLEMENT_TYPE_DESC,
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
		    COALESCE(tilc.C_WORKFLOW_APPROVE_STATUS, 'NOT_STARTED') AS LC_STATUS,
		    COALESCE(rim.C_WORKFLOW_APPROVE_STATUS, 'NOT_STARTED') AS REMITTANCE_STATUS,
		    COALESCE(tipm.ID, 0) AS N_MASTER_ID,
		    COALESCE(rim.ID, 0) AS N_REMITTANCE_ID,
		    COALESCE(tipd.C_PERFORMA_NO, '-') AS C_PERFORMA_NO,
		    COALESCE(tipd.D_PERFORMA_DATE, DATE '1970-01-01') AS C_PERFORMA_DATE,
		    COALESCE(tilc.C_LC_NO, '-') AS C_LC_NO,
		    COALESCE(tilc.D_LC_DATE, DATE '1970-01-01') AS D_LC_DATE,
		    COALESCE(tilc.C_ACKNOWLEDGMENT,'UNKNOWN') AS C_ACKNOWLEDGMENT,
		    COALESCE(rim.D_REMITTANCE_DATE, DATE '1970-01-01') AS D_REMITTANCE_DATE,
		    COALESCE(rim.C_REMITTANCE_NO, '-') AS C_REMITTANCE_NO,
		    COALESCE(rim.C_PMS_ID, '-') AS REMITTANCE_PMS_ID,
		    COALESCE(tilc.C_PMS_LC_ID, '-') AS LC_PMS_ID
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
		    AND s.CONTRACT_NO = tit.CONTRACT_NO
		    AND s.CONTRACT_DETAIL_NO = tit.CONTRACT_DETAIL_NO
		         LEFT JOIN (
		    SELECT tipm.*,
		           ROW_NUMBER() OVER (PARTITION BY C_PAYMENT_CODE, N_CONTRACT_NO ORDER BY
		               CASE C_WORKFLOW_APPROVE_STATUS
		                   WHEN 'ACCEPTED' THEN 1
		                   WHEN 'IN_PROGRESS' THEN 2
		                   WHEN 'EXCEPTION' THEN 3
		                   WHEN 'REVERSAL' THEN 4
		                   WHEN 'DRAFT' THEN 5
		                   ELSE 6
		                   END) AS rn
		    FROM T_INS_PERFORMA_MASTER tipm
		) tipm ON tit.PAYMENT_CODE = tipm.C_PAYMENT_CODE
		    AND tipm.N_CONTRACT_NO = tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO
		    AND tipm.rn = 1
		         LEFT JOIN (
		    SELECT tilc.*,
		           ROW_NUMBER() OVER (PARTITION BY N_PROFORMA_MASTER_ID ORDER BY
		               CASE C_WORKFLOW_APPROVE_STATUS
		                   WHEN 'ACCEPTED' THEN 1
		                   WHEN 'IN_PROGRESS' THEN 2
		                   WHEN 'EXCEPTION' THEN 3
		                   WHEN 'DRAFT' THEN 5
		                   ELSE 6
		                   END) AS rn
		    FROM T_INS_LC tilc
		) tilc ON tilc.N_PROFORMA_MASTER_ID = tipm.ID AND tilc.rn = 1
		         LEFT JOIN TBL_IME_PS_OFFERS tio ON tit.OFFER_CODE = tio.ID
		         LEFT JOIN T_INS_REMITTANCE_MASTER rim ON rim.C_PAYMENT_CODE = tit.PAYMENT_CODE
		         LEFT JOIN TBL_IME_SETTLEMENT ts ON ts.PAYMENT_CODE=tit.PAYMENT_CODE
		LEFT JOIN T_INS_PERFORMA_DETAIL tipd ON tipm.ID=tipd.F_PERFORMA_MASTER_ID
		WHERE tit.CONTRACT_DATE > '1404/01/01'
		  AND tit.CURRENCY_CODE = 1
		  AND (tipm.ID IS NOT NULL OR rim.ID IS NOT NULL)
		ORDER BY tit.CONTRACT_DATE DESC
		""")
public class HistoryExtractMasterModel implements Serializable {


	@Serial
	private static final long serialVersionUID = 8738545686257069738L;
	@Id
	private Long id;

	@Column(name = "C_BUYER_NAME")
	private String buyerName;

	@Column(name = "C_BUYER_NATIONAL_CODE")
	private String buyerNationalCode;

	@Column(name = "N_COMMODITY_CODE")
	private Long commodityCode;

	@Column(name = "C_COMMODITY_SYMBOL")
	private String commoditySymbol;

	@Column(name = "C_NAME")
	private String commodityName;

	@Column(name = "C_OFFER_DESCRIPTION")
	private String offerDescription;

	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;

	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;

	@Column(name = "N_COMMISSION")
	private Double commission;

	@Column(name = "CONTRACT_DATE")
	private String contractDate;

	@Column(name = "CONTRACT_NO")
	private String contractNo;

	@Column(name = "PAYMENT_CODE")
	private String paymentCode;

	@Column(name = "SETTLEMENT_TYPE")
	private String settlementType;

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
	private String preinvoiceStatus;

	@Column(name = "LC_STATUS")
	private String lcStatus;

	@Column(name = "REMITTANCE_STATUS")
	private String remittanceStatus;

	@Column(name = "N_MASTER_ID")
	private Long masterId;

	@Column(name = "N_REMITTANCE_ID")
	private Long remittanceId;

	@Column(name = "C_PERFORMA_NO")
	private String performaNo;

	@Column(name = "C_PERFORMA_DATE")
	private String performaDate;


	@Column(name = "C_LC_NO")
	private String lcNo;

	@Column(name = "D_LC_DATE")
	private String lcDate;

	@Column(name = "D_REMITTANCE_DATE")
	private Date remittanceDate;

	@Column(name = "C_REMITTANCE_NO")
	private String remittanceNo;

	@Column(name = "REMITTANCE_PMS_ID")
	private String remittancePmsId;

	@Column(name = "LC_PMS_ID")
	private String lcPmsId;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_ACKNOWLEDGMENT", length = 50, nullable = false)
	@Schema(description = "تاییدیه نوع تسویه و نوع حواله")
	private Acknowledgment acknowledgment = Acknowledgment.UNKNOWN;

}
