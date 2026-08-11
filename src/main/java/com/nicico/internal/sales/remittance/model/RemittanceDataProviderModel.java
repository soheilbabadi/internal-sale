package com.nicico.internal.sales.remittance.model;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
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
import java.util.Date;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
		WITH 
		filtered_trades AS (
		    SELECT 
		        tit.ID,
		        tit.PAYMENT_CODE,
		        tit.COMMODITY_CODE,
		        tit.BUYER_NAME,
		        tit.BUYER_NATIONAL_CODE,
		        tit.CONTRACT_DATE,
		        tit.CONTRACT_NO,
		        tit.CONTRACT_DETAIL_NO,
		        tit.BUYER_BROKER_CODE,
		        tit.SELLER_BROKER_CODE,
		        tit.UNIT_COUNT,
		        tit.UNIT_PRICE,
		        tit.SETTLEMENT_TYPE,
		        tit.SETTLEMENT_TYPE_DESC,
		        tit.CONTRACT_TYPE_CODE,
		        tit.DELIVERY_DATE
		    FROM TBL_IME_TRADE tit
		    WHERE tit.CONTRACT_DATE > '1405/01/01'
		      AND tit.CURRENCY_CODE = 1
		),
		latest_buckets AS (
		    SELECT 
		        N_GOOD_ID,
		        N_CASH_PERCENTAGE,
		        N_COMMISSION,
		        C_PACKING_NAME,
		        N_PACKING_ID,
		        D_START_DATE
		    FROM (
		        SELECT 
		            N_GOOD_ID,
		            N_CASH_PERCENTAGE,
		            N_COMMISSION,
		            C_PACKING_NAME,
		            N_PACKING_ID,
		            D_START_DATE,
		            ROW_NUMBER() OVER (
		                PARTITION BY N_GOOD_ID 
		                ORDER BY 
		                    CASE WHEN D_EXPIRE_DATE IS NULL THEN 0 ELSE 1 END,
		                    D_START_DATE DESC
		            ) as rn
		        FROM T_INS_GOODS_BUCKET
		        WHERE D_EXPIRE_DATE IS NULL OR D_EXPIRE_DATE > SYSDATE
		    )
		    WHERE rn = 1
		),
		latest_proformas AS (
		    SELECT 
		        C_PAYMENT_CODE,
		        N_CONTRACT_NO,
		        ID,
		        C_PROFORMA_ISSUE_TYPE,
		        C_PERFORMA_NO,
		        D_PERFORMA_DATE,
		        F_PERFORMA_MASTER_ID
		    FROM (
		        SELECT 
		            tipm.C_PAYMENT_CODE,
		            tipm.N_CONTRACT_NO,
		            tipm.ID,
		            tipm.C_PROFORMA_ISSUE_TYPE,
		            tipd.C_PERFORMA_NO,
		            tipd.D_PERFORMA_DATE,
		            tipd.F_PERFORMA_MASTER_ID,
		            ROW_NUMBER() OVER (
		                PARTITION BY tipm.C_PAYMENT_CODE, tipm.N_CONTRACT_NO 
		                ORDER BY tipd.D_PERFORMA_DATE DESC NULLS LAST
		            ) as rn
		        FROM T_INS_PERFORMA_MASTER tipm
		        INNER JOIN T_INS_PERFORMA_DETAIL tipd 
		            ON tipm.ID = tipd.F_PERFORMA_MASTER_ID
		    )
		    WHERE rn = 1
		),
		latest_lcs AS (
		    SELECT 
		        C_PAYMENT_CODE,
		        N_CONTRACT_NO,
		        ID,
		        D_LC_EXPIRY_DATE,
		        D_SETTLEMENT_DUE_DATE,
		        N_ISSUER_BANK_ID,
		        C_ISSUER_BANK_NAME,
		        C_ISSUER_BANK_BRANCH_NAME,
		        C_ISSUER_BANK_CODE,
		        C_TRADING_BANK_TITLE,
		        C_TRADING_BRANCH_TITLE,
		        N_PROFORMA_DETAIL_ID,
		        C_LC_NO,
		        D_LC_DATE
		    FROM (
		        SELECT 
		            C_PAYMENT_CODE,
		            N_CONTRACT_NO,
		            ID,
		            D_LC_EXPIRY_DATE,
		            D_SETTLEMENT_DUE_DATE,
		            N_ISSUER_BANK_ID,
		            C_ISSUER_BANK_NAME,
		            C_ISSUER_BANK_BRANCH_NAME,
		            C_ISSUER_BANK_CODE,
		            C_TRADING_BANK_TITLE,
		            C_TRADING_BRANCH_TITLE,
		            N_PROFORMA_DETAIL_ID,
		            C_LC_NO,
		            D_LC_DATE,
		            ROW_NUMBER() OVER (
		                PARTITION BY C_PAYMENT_CODE, N_CONTRACT_NO 
		                ORDER BY D_LC_EXPIRY_DATE DESC NULLS LAST
		            ) as rn
		        FROM T_INS_LC
		    )
		    WHERE rn = 1
		)
		SELECT
		    tit.ID as id,
		    tit.PAYMENT_CODE AS C_PAYMENT_CODE,
		    tic.ID AS N_CUSTOMER_ID,
		    tit.BUYER_NAME AS C_CUSTOMER_NAME,
		    tic.C_ECONOMIC_CODE AS C_ECONOMIC_CODE,
		    tit.BUYER_NATIONAL_CODE AS C_NATIONAL_CODE,
		    tig.ID AS N_GOOD_ID,
		    tig.C_NAME AS C_GOOD_NAME,
		    tit.COMMODITY_CODE AS N_IME_COMMODITY_ID,
		    tig.C_IME_COMMODITY_SYMBOL,
		    tigb.C_PACKING_NAME,
		    tigb.N_PACKING_ID,
		    tit.CONTRACT_DATE AS C_CONTRACT_DATE,
		    tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO AS C_CONTRACT_NO,
		    tit.BUYER_BROKER_CODE AS N_BUYER_BROKER_ID,
		    bb.PERSIAN_NAME AS C_BUYER_BROKER_NAME,
		    tit.SELLER_BROKER_CODE AS N_SELLER_BROKER_ID,
		    sb.PERSIAN_NAME AS C_SELLER_BROKER_NAME,
		    tit.UNIT_COUNT * 1000 AS N_UNIT_COUNT,
		    tit.UNIT_PRICE AS N_UNIT_PRICE,
		    NVL(tigb.N_CASH_PERCENTAGE, 0) AS N_CASH_PERCENTAGE,
		    NVL(100 - tigb.N_CASH_PERCENTAGE, 0) AS N_CREDIT_PERCENTAGE,
		    NVL(tigb.N_COMMISSION, 0) AS N_COMMISSION,
		    tit.UNIT_PRICE * (1 + NVL(tigb.N_COMMISSION, 0)) AS N_CREDIT_UNIT_PRICE,
		    NVL((100 - NVL(tigb.N_CASH_PERCENTAGE, 0)) / 100.0 * (tit.UNIT_COUNT * 1000) * 
		        ((NVL(tigb.N_COMMISSION, 0) + 100.0) / 100.0) * tit.UNIT_PRICE, 0) AS N_CREDIT_AMOUNT,
		    NVL(NVL(tigb.N_CASH_PERCENTAGE, 0) / 100.0 * (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE, 0) AS N_CASH_AMOUNT,
		    NVL((100 - NVL(tigb.N_CASH_PERCENTAGE, 0)) / 100.0 * (tit.UNIT_COUNT * 1000) * 
		        ((NVL(tigb.N_COMMISSION, 0) + 100.0) / 100.0) * tit.UNIT_PRICE +
		        (NVL(tigb.N_CASH_PERCENTAGE, 0) / 100.0 * (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE), 0) AS N_FINAL_AMOUNT,
		    tit.SETTLEMENT_TYPE AS C_SETTLEMENT_TYPE,
		    tit.SETTLEMENT_TYPE_DESC AS C_SETTLEMENT_TYPE_DESC,
		    NVL(lp.ID, 0) AS N_PROFORMA_MASTER_ID,
		    NVL(lp.C_PROFORMA_ISSUE_TYPE, 'UNKNOWN') AS C_PROFORMA_ISSUE_TYPE,
		    NVL(lp.C_PERFORMA_NO, '-') AS C_PROFORMA_NO,
		    NVL(lp.D_PERFORMA_DATE, TIMESTAMP '1970-01-01 00:00:00') AS D_PROFORMA_DATE,
		    tit.CONTRACT_TYPE_CODE AS C_CONTRACT_TYPE_CODE,
		    tipct.DESCRIPTION AS C_CONTRACT_TYPE_DESCRIPTION,
		    tis.SETTLEMENT_DATE,
		    tit.DELIVERY_DATE,
		    NVL(lc.D_LC_EXPIRY_DATE, TIMESTAMP '1970-01-01 00:00:00') AS D_LC_EXPIRY_DATE,
		    NVL(lc.D_SETTLEMENT_DUE_DATE, TIMESTAMP '1970-01-01 00:00:00') AS D_SETTLEMENT_DUE_DATE,
		    NVL(lc.N_ISSUER_BANK_ID, 0) AS N_ISSUER_BANK_ID,
		    NVL(lc.C_ISSUER_BANK_NAME, '-') AS C_ISSUER_BANK_NAME,
		    NVL(lc.C_ISSUER_BANK_BRANCH_NAME, '-') AS C_ISSUER_BANK_BRANCH_NAME,
		    NVL(lc.C_ISSUER_BANK_CODE, '-') AS C_ISSUER_BANK_CODE,
		    NVL(lc.C_TRADING_BANK_TITLE, '-') AS C_TRADING_BANK_NAME,
		    NVL(lc.C_TRADING_BRANCH_TITLE, '-') AS C_TRADING_BRANCH_NAME,
		    NVL(lc.N_PROFORMA_DETAIL_ID, 0) AS N_PROFORMA_DETAIL_ID,
		    NVL(lc.ID, 0) AS N_LC_ID,
		    NVL(lc.C_LC_NO, '-') AS C_LC_NO,
		    NVL(lc.D_LC_DATE, TIMESTAMP '1970-01-01 00:00:00') AS D_LC_DATE,
		    tis.IS_DELAY_PENALTY
		FROM filtered_trades tit
		INNER JOIN T_INS_GOODS tig ON tit.COMMODITY_CODE = tig.N_IME_COMMODITY_ID
		INNER JOIN TBL_IME_PS_CONTRACT_TYPES tipct ON tit.CONTRACT_TYPE_CODE = tipct.ID
		INNER JOIN TBL_IME_SETTLEMENT tis ON tis.PAYMENT_CODE = tit.PAYMENT_CODE
		INNER JOIN T_INS_CUSTOMER tic ON tic.C_NATIONAL_CODE = tit.BUYER_NATIONAL_CODE
		LEFT JOIN latest_buckets tigb ON tig.ID = tigb.N_GOOD_ID
		LEFT JOIN TBL_IME_PS_BROKERS sb ON sb.ID = tit.SELLER_BROKER_CODE
		LEFT JOIN TBL_IME_PS_BROKERS bb ON bb.ID = tit.BUYER_BROKER_CODE
		LEFT JOIN latest_proformas lp ON tit.PAYMENT_CODE = lp.C_PAYMENT_CODE
		    AND tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO = lp.N_CONTRACT_NO
		LEFT JOIN latest_lcs lc ON tit.PAYMENT_CODE = lc.C_PAYMENT_CODE
		    AND tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO = lc.N_CONTRACT_NO
		ORDER BY tit.CONTRACT_DATE DESC
		""")
public class RemittanceDataProviderModel implements Serializable {
	@Serial
	private static final long serialVersionUID = 788276480203056653L;
	@Id
	@Column(name = "ID")
	private Long id;
	@Column(name = "C_PAYMENT_CODE")
	private String paymentCode;
	@Schema(description = "شناسه مشتری", name = "customerId", example = "12345")
	@Column(name = "N_CUSTOMER_ID")
	private long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "شرکت نمونه")
	@Column(name = "C_CUSTOMER_NAME", length = 200)
	private String customerName;
	@Schema(description = "کد اقتصادی", name = "economicCode", example = "1234567890")
	@Column(name = "C_ECONOMIC_CODE", length = 50)
	private String economicCode;
	@Schema(description = "شناسه ملی", name = "nationalCode", example = "0012345678")
	@Column(name = "C_NATIONAL_CODE", length = 15)
	private String nationalCode;

	@Column(name = "N_GOOD_ID")
	private long goodId;
	@Column(name = "C_GOOD_NAME", length = 200)
	private String goodName;
	@Column(name = "N_IME_COMMODITY_ID", unique = true)
	private Long imeCommodityId;
	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

	@Column(name = "C_CONTRACT_DATE")
	private String contractDate;
	@Column(name = "C_CONTRACT_NO", length = 50)
	private String contractNo;
	@Column(name = "N_BUYER_BROKER_ID")
	private Long buyerBrokerId;
	@Column(name = "C_BUYER_BROKER_NAME", length = 200)
	private String buyerBrokerName;
	@Column(name = "N_SELLER_BROKER_ID")
	private Long sellerBrokerId;
	@Column(name = "C_SELLER_BROKER_NAME", length = 200)
	private String sellerBrokerName;
	@Column(name = "N_UNIT_COUNT")
	private Integer unitCount;
	@Column(name = "N_UNIT_PRICE")
	private Double unitPrice;

	@Column(name = "C_SETTLEMENT_TYPE")
	private String settlementType;
	@Column(name = "C_SETTLEMENT_TYPE_DESC")
	private String settlementTypeDesc;
	@Column(name = "N_PROFORMA_MASTER_ID")
	private long proformaMasterId;
	@Enumerated(EnumType.STRING)
	@Column(name = "C_PROFORMA_ISSUE_TYPE")
	private ProformaIssueType proformaIssueType;
	@Column(name = "C_PROFORMA_NO", length = 50)
	private String proformaNo;
	@Column(name = "D_PROFORMA_DATE")
	private Date proformaDate;
	@Column(name = "C_CONTRACT_TYPE_CODE")
	private String contractTypeCode;
	@Column(name = "C_CONTRACT_TYPE_DESCRIPTION")
	private String contractTypeDescription;
	@Column(name = "SETTLEMENT_DATE")
	private String settlementDate;
	@Column(name = "DELIVERY_DATE")
	private String deliveryDate;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_LC_EXPIRY_DATE")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_SETTLEMENT_DUE_DATE")
	private Date settlementDueDate;
	@Column(name = "N_ISSUER_BANK_ID")
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;
	@Column(name = "C_ISSUER_BANK_NAME", length = 200)
	@Schema(name = "نام بانک بانک گشایش اعتبار اسنادی")
	private String issuerBankName;
	@Column(name = "C_ISSUER_BANK_BRANCH_NAME", length = 200)
	@Schema(name = "نام شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchName;
	@Column(name = "C_ISSUER_BANK_CODE", length = 200)
	@Schema(name = "کد شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchCode;
	@Column(name = "C_TRADING_BANK_NAME", length = 200)
	@Schema(name = "نام بانک معامله کننده")
	private String tradingBankName;
	@Column(name = "C_TRADING_BRANCH_NAME", length = 200)
	@Schema(name = "نام شعبه بانک معامله کننده")
	private String tradingBankBranchName;
	@Column(name = "N_PROFORMA_DETAIL_ID")
	private long proformaDetailId;
	@Column(name = "N_LC_ID")
	private Long lcId;
	@Schema(description = "شماره اعتبار اسنادی", name = "lcNo", example = "LC123456789")
	@Column(name = "C_LC_NO")
	private String lcNo;
	@Schema(description = "تاریخ گشایش اعتبار اسنادی", name = "lcDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_LC_DATE")
	private Date lcDate;

	@Schema(description = "وضعیت جریمه تاخیر تسویه", name = "isDelayPenalty", example = "1")
	@Column(name = "IS_DELAY_PENALTY")
	private boolean isDelayPenalty;
}

