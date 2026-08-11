package com.nicico.internal.sales.bill.model;

import com.nicico.internal.sales.bill.dto.BillingStatus;
import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "T_INS_BILLING_MASTER")
@AllArgsConstructor
@NoArgsConstructor
public class BillingMasterModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = -4016144959922697684L;

	@Id
	@Column(name = "ID")
	private UUID id;

	@Schema(description = "شماره سریال", name = "serialNo", example = "BL123456789")
	@Column(name = "C_SERIAL_NO", length = 50)
	private String serialNo;

	@Schema(description = "تاریخ صدور", name = "billingDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_BILLING_DATE")
	private Date billingDate;


	@Schema(description = "وزن نهایی", name = "totalFinalQuantity", example = "100000")
	@Column(name = "N_TOTAL_FINAL_QUANTITY")
	private BigDecimal totalFinalQuantity;

	@Schema(description = "مبلغ نهایی", name = "totalFinalAmount", example = "1000000")
	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "C_BILLING_STATUS", length = 50)
	private BillingStatus billingStatus;

	@Column(name = "N_TAX_AMOUNT")
	private BigDecimal taxAmount;

	@Column(name = "N_PENALTY_AMOUNT")
	private BigDecimal penaltyAmount;

	@Column(name = "N_EMISSION_TAX_AMOUNT")
	private BigDecimal emissionTaxAmonut;

	@Column(name = "N_EMISSION_TAX_PERCENTAGE")
	private BigDecimal emissionTaxPercentage;

	@Column(name = "IS_DELAY_PENALTY_APPLIED")
	private boolean isDelayPenaltyApplied;

	@Column(name = "IS_FINAL")
	private boolean isFinal;

	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	@Column(name = "C_PROCESS_ID", length = 50, nullable = false)
	private String processId;

	@Schema(description = "کد فرایندابطال", name = "reversalProcessId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	@Column(name = "C_REVERSAL_PROCESS_ID", length = 50)
	private String reversalProcessId;


	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100)
	private WorkflowApproveStatus workflowApproveStatus;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = RemittanceMasterModel.class)
	@JoinColumn(name = "F_REMITTANCE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private RemittanceMasterModel remittanceMaster;


	@OneToMany(mappedBy = "billingMaster", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = BillingWeightingDetailModel.class)
	@EqualsAndHashCode.Exclude
	private List<BillingWeightingDetailModel> billingWeighingDetails;
}
