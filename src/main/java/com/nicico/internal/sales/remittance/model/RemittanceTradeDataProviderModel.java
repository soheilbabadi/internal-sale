package com.nicico.internal.sales.remittance.model;

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

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
				SELECT DISTINCT 
		    tit.ID                                                               AS ID,
		    tit.PAYMENT_CODE                                                     AS C_PAYMENT_CODE,
		    tic.ID                                                               AS N_CUSTOMER_ID,
		    tit.BUYER_NAME                                                       AS C_CUSTOMER_NAME,
		    CASE 
		        WHEN LENGTH(
		            NVL(
		                TRIM(
		                    REGEXP_SUBSTR(
		                        DBMS_LOB.SUBSTR(tio.DESCRIPTION, 4000, 1),
		                        '([A-Za-z]+-)?[0-9]+(-[0-9]+)*',
		                        1, 1, NULL, 0
		                    )
		                ),
		                '-'
		            )
		        ) = 1 
		        AND REGEXP_LIKE(
		            NVL(
		                TRIM(
		                    REGEXP_SUBSTR(
		                        DBMS_LOB.SUBSTR(tio.DESCRIPTION, 4000, 1),
		                        '([A-Za-z]+-)?[0-9]+(-[0-9]+)*',
		                        1, 1, NULL, 0
		                    )
		                ),
		                '-'
		            ),
		            '^[0-9]$'
		        )
		        THEN '-'
		        ELSE NVL(
		            TRIM(
		                REGEXP_SUBSTR(
		                    DBMS_LOB.SUBSTR(tio.DESCRIPTION, 4000, 1),
		                    '([A-Za-z]+-)?[0-9]+(-[0-9]+)*',
		                    1, 1, NULL, 0
		                )
		            ),
		            '-'
		        )
		    END                                                                   AS C_LOT_NUMBER,
		    tic.C_ECONOMIC_CODE                                                  AS C_ECONOMIC_CODE,
		    tit.BUYER_NATIONAL_CODE                                              AS C_NATIONAL_CODE,
		    tig.ID                                                               AS N_GOOD_ID,
		    tig.C_NAME                                                           AS C_GOOD_NAME,
		    tit.COMMODITY_CODE                                                   AS N_IME_COMMODITY_ID,
		    tig.C_IME_COMMODITY_SYMBOL,
		    tit.CONTRACT_DATE                                                    AS C_CONTRACT_DATE,
		    tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO                   AS C_CONTRACT_NO,
		    tit.BUYER_BROKER_CODE                                                AS N_BUYER_BROKER_ID,
		    buyerBroker.PERSIAN_NAME                                             AS C_BUYER_BROKER_NAME,
		    tit.SELLER_BROKER_CODE                                               AS N_SELLER_BROKER_ID,
		    sellerBroker.PERSIAN_NAME                                            AS C_SELLER_BROKER_NAME,
		    tit.UNIT_COUNT * 1000                                                AS N_UNIT_COUNT,
		    tit.UNIT_PRICE                                                       AS N_UNIT_PRICE,
		    COALESCE(tigb.N_CASH_PERCENTAGE, 0)                                 AS N_CASH_PERCENTAGE,
		    COALESCE(100 - tigb.N_CASH_PERCENTAGE, 0)                           AS N_CREDIT_PERCENTAGE,
		    COALESCE(tigb.N_COMMISSION, 0)                                       AS N_COMMISSION,
		    tit.UNIT_PRICE * (1 + COALESCE(tigb.N_COMMISSION, 0))               AS N_CREDIT_UNIT_PRICE,
		    COALESCE(
		        (100 - tigb.N_CASH_PERCENTAGE) / 100.0 *
		        (tit.UNIT_COUNT * 1000) *
		        ((tigb.N_COMMISSION + 100.0) / 100.0) *
		        tit.UNIT_PRICE, 0)                                               AS N_CREDIT_AMOUNT,
		    COALESCE(
		        tigb.N_CASH_PERCENTAGE / 100.0 *
		        (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE, 0)                    AS N_CASH_AMOUNT,
		    COALESCE(
		        (100 - tigb.N_CASH_PERCENTAGE) / 100.0 *
		        (tit.UNIT_COUNT * 1000) *
		        ((tigb.N_COMMISSION + 100.0) / 100.0) *
		        tit.UNIT_PRICE +
		        (tigb.N_CASH_PERCENTAGE / 100.0 *
		         (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE), 0)                  AS N_FINAL_AMOUNT,
		    tit.SETTLEMENT_TYPE                                                  AS C_SETTLEMENT_TYPE,
		    tis.SETTLEMENT_TYPE                                                  AS C_SETTLEMENT_TYPE_DESC,
		    tit.CONTRACT_TYPE_CODE                                               AS C_CONTRACT_TYPE_CODE,
		    tipct.DESCRIPTION                                                    AS C_CONTRACT_TYPE_DESCRIPTION,
		    tis.SETTLEMENT_DATE,
		    tit.DELIVERY_DATE,
		    tis.IS_DELAY_PENALTY,
		    CASE
		        WHEN tio.DESCRIPTION IS NULL THEN 'بدون توضیحات'
		        ELSE DBMS_LOB.SUBSTR(tio.DESCRIPTION, 4000, 1)
		    END                                                                  AS C_OFFER_DESCRIPTION,
		    COALESCE(
		        (SELECT C_WORKFLOW_APPROVE_STATUS 
		         FROM T_INS_REMITTANCE_MASTER tirm
		         WHERE tirm.N_TRADE_ID = tit.ID
		         ORDER BY tirm.ID DESC
		         FETCH FIRST 1 ROW ONLY), 
		        'NOT_STARTED'
		    )                                                                   AS C_WORKFLOW_APPROVE_STATUS
		FROM TBL_IME_SETTLEMENT tis
		    INNER JOIN TBL_IME_TRADE tit
		        ON  tis.PAYMENT_CODE        = tit.PAYMENT_CODE
		        AND tis.CONTRACT_NO         = tit.CONTRACT_NO
		        AND tis.CONTRACT_DETAIL_NO  = tit.CONTRACT_DETAIL_NO
		        AND tit.CONTRACT_DATE       > '1405/01/01'
		        AND tit.CURRENCY_CODE       = 1
		        AND tis.SETTLEMENT_TYPE     != 'نقدی/اعتباری'
		    INNER JOIN T_INS_GOODS tig
		        ON tit.COMMODITY_CODE = tig.N_IME_COMMODITY_ID
		    INNER JOIN TBL_IME_PS_CONTRACT_TYPES tipct
		        ON tit.CONTRACT_TYPE_CODE = tipct.ID
		    INNER JOIN T_INS_CUSTOMER tic
		        ON tic.C_NATIONAL_CODE = tit.BUYER_NATIONAL_CODE
		    LEFT JOIN (
		        SELECT 
		            N_GOOD_ID,
		            N_CASH_PERCENTAGE,
		            N_COMMISSION,
		            D_START_DATE
		        FROM (
		            SELECT 
		                tigb.*,
		                ROW_NUMBER() OVER (
		                    PARTITION BY N_GOOD_ID 
		                    ORDER BY D_START_DATE DESC NULLS LAST
		                ) AS rn
		            FROM T_INS_GOODS_BUCKET tigb
		            WHERE D_EXPIRE_DATE IS NULL
		        )
		        WHERE rn = 1
		    ) tigb
		        ON tig.ID = tigb.N_GOOD_ID
		        AND (tigb.D_START_DATE IS NULL
		             OR tigb.D_START_DATE <= TRUNC(TO_DATE(tit.CONTRACT_DATE, 'YYYY/MM/DD', 'NLS_CALENDAR=PERSIAN')))
		    LEFT JOIN TBL_IME_PS_OFFERS tio 
		        ON tit.OFFER_CODE = tio.ID
		    LEFT JOIN TBL_IME_PS_BROKERS sellerBroker 
		        ON sellerBroker.ID = tit.SELLER_BROKER_CODE
		    LEFT JOIN TBL_IME_PS_BROKERS buyerBroker 
		        ON buyerBroker.ID = tit.BUYER_BROKER_CODE
		WHERE NOT EXISTS (
		    SELECT 1  
		    FROM T_INS_REMITTANCE_MASTER tirm 
		    WHERE tirm.N_TRADE_ID = tit.ID 
		    AND tirm.C_WORKFLOW_APPROVE_STATUS = 'ACCEPTED'
		)
		""")
public class RemittanceTradeDataProviderModel implements Serializable {

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

	@Schema(description = "شناسه کالا در بورس کالا", name = "imeCommodityId", example = "2001")
	@Column(name = "N_IME_COMMODITY_ID")
	private Long imeCommodityId;

	@Schema(description = "نماد کالا در بورس کالا", name = "imeCommoditySymbol", example = "SYMBOL123")
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

	@Schema(description = "کمیسیون", name = "commission")
	@Column(name = "N_COMMISSION")
	private BigDecimal commission;

	@Schema(description = "درصد نقدی", name = "cashPercentage")
	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;

	@Schema(description = "درصد اعتباری", name = "creditPercentage")
	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;

	@Schema(description = "قیمت واحد اعتباری", name = "creditUnitPrice")
	@Column(name = "N_CREDIT_UNIT_PRICE")
	private BigDecimal creditUnitPrice;

	@Schema(description = "مبلغ اعتباری", name = "creditAmount")
	@Column(name = "N_CREDIT_AMOUNT")
	private BigDecimal creditAmount;

	@Schema(description = "مبلغ نقدی", name = "cashAmount")
	@Column(name = "N_CASH_AMOUNT")
	private BigDecimal cashAmount;

	@Schema(description = "مبلغ نهایی", name = "finalAmount")
	@Column(name = "N_FINAL_AMOUNT")
	private BigDecimal finalAmount;

	@Schema(name = "settlementType", description = "شناسه نوع تسویه")
	@Column(name = "C_SETTLEMENT_TYPE")
	private String settlementType;

	@Schema(name = "settlementTypeDesc", description = "نوع تسویه")
	@Column(name = "C_SETTLEMENT_TYPE_DESC")
	private String settlementTypeDesc;

	@Schema(name = "contractTypeCode", description = "کد نوع قرارداد")
	@Column(name = "C_CONTRACT_TYPE_CODE")
	private String contractTypeCode;

	@Schema(name = "contractTypeDescription", description = "شرح نوع قرارداد")
	@Column(name = "C_CONTRACT_TYPE_DESCRIPTION")
	private String contractTypeDescription;

	@Schema(name = "settlementDate", description = "تاریخ تسویه")
	@Column(name = "SETTLEMENT_DATE")
	private String settlementDate;

	@Schema(name = "deliveryDate", description = "تاریخ تحویل")
	@Column(name = "DELIVERY_DATE")
	private String deliveryDate;

	@Schema(description = "وضعیت جریمه تاخیر تسویه", name = "isDelayPenalty", example = "1")
	@Column(name = "IS_DELAY_PENALTY")
	private boolean isDelayPenalty;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100)
	private WorkflowApproveStatus workflowApproveStatus;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	@Column(name = "C_OFFER_DESCRIPTION", length = 4000)
	private String offerDescription;


	@Schema(description = "شماره لات")
	@Column(name = "C_LOT_NUMBER")
	private String lotNumber;
}