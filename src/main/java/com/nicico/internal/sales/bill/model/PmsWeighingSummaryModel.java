package com.nicico.internal.sales.bill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

// خواندن اطلاعات توزین از لجستیک
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Subselect("""
		
		SELECT
		            rm.ID AS ID,
		            rm.C_CONTRACT_NO AS RM_CONTRACT_NO,
		            t.HAVCODE AS C_REMITTANCE_NO,
		            rm.N_CUSTOMER_ID AS N_CUSTOMER_ID,
		            rm.C_CUSTOMER_NAME AS C_CUSTOMER_NAME,
		            rm.C_ECONOMIC_CODE AS C_ECONOMIC_CODE,
		            rm.C_NATIONAL_CODE AS C_NATIONAL_CODE,
		            rm.C_LOADING_PORT AS C_LOADING_PORT,
		            TO_CHAR(rm.D_CONTRACT_DATE, 'YYYY/MM/DD', 'NLS_CALENDAR=Persian') AS D_CONTRACT_DATE,
		            SUM(t.WAZN) AS N_TOTAL_REAL_WEIGHT,
		            SUM(t.TEDAD) AS N_WEIGHING_COUNT,
		            t.PACKNAME AS C_PACK_NAME,
		            t.GDSNAME AS C_GOOD_NAME,
		            COALESCE(MAX(t.SAL_DATE2), TO_CHAR(SYSDATE, 'YYYY/MM/DD', 'NLS_CALENDAR=Persian')) AS D_ISSUE_DATE,
		            r.HAV_ISFINAL AS IS_FINAL,
		            MAX(r.HAV_MEGHDAR) AS N_REMITTANCE_QUANTITY,
		            SUM(t.WAZN) - MAX(r.HAV_MEGHDAR) AS N_WEIGHT_DIFF,
		            MAX(r.HAV_GHARADADNO) AS C_CONTRACT_NO,
		            MAX(r.HAV_DATE) KEEP (DENSE_RANK LAST ORDER BY COALESCE(t.SAL_DATE2, TO_CHAR(SYSDATE, 'YYYY/MM/DD', 'NLS_CALENDAR=Persian'))) AS D_REMITTANCE_DATE
		        FROM PMS.HAVALEH_FG@DBL_DS2_COPLINK r
		        LEFT JOIN PMS.V_TOZINE_CONTENT_M2@DBL_DS2_COPLINK t ON r.HAV_CODE = t.HAVCODE
		        INNER JOIN T_INS_REMITTANCE_MASTER rm ON r.HAV_GHARADADNO = rm.C_CONTRACT_NO
		        WHERE t.HAVCODE IS NOT NULL
		        GROUP BY
		            rm.ID,
		            rm.N_CUSTOMER_ID,
		            rm.C_CUSTOMER_NAME,
		            rm.C_ECONOMIC_CODE,
		            rm.C_NATIONAL_CODE,
		            rm.C_LOADING_PORT,
		            rm.D_CONTRACT_DATE,
		            rm.C_CONTRACT_NO,
		            t.PACKNAME,
		            t.HAVCODE,
		            t.GDSNAME,
		            r.HAV_ISFINAL
		"""
)
public class PmsWeighingSummaryModel implements Serializable {

	@Serial
	private static final long serialVersionUID = -2269528829524435712L;

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "RM_CONTRACT_NO")
	private Long rmContractNo;

	@Column(name = "C_REMITTANCE_NO")
	private String remittanceNo;

	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;

	@Column(name = "C_CUSTOMER_NAME")
	private String customerName;

	@Column(name = "C_ECONOMIC_CODE")
	private String economicCode;

	@Column(name = "C_NATIONAL_CODE", length = 15)
	private String nationalCode;

	@Column(name = "C_LOADING_PORT")
	private String loadingPort;

	@Column(name = "D_CONTRACT_DATE")
	private String contractDate;

	@Column(name = "N_REMITTANCE_QUANTITY")
	private BigDecimal remittanceQuantity;

	@Column(name = "N_TOTAL_REAL_WEIGHT")
	private BigDecimal totalRealWeight;

	@Column(name = "N_WEIGHING_COUNT")
	private BigDecimal weighingCount;

	@Column(name = "C_PACK_NAME")
	private String packName;

	@Column(name = "C_GOOD_NAME")
	private String goodName;

	@Column(name = "D_ISSUE_DATE")
	private String issueDate;

	@Column(name = "IS_FINAL")
	private boolean isFinal;

	@Column(name = "N_WEIGHT_DIFF")
	private BigDecimal weightDifference;

	@Column(name = "C_CONTRACT_NO")
	private String contractNo;

	@Column(name = "D_REMITTANCE_DATE")
	private String remittanceDate;
}