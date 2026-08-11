package com.nicico.internal.sales.bill.model;

import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBL_INS_BILLING_WEIGHING_DETAIL")
@Entity
public class BillingWeightingDetailModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = -226952882952435713L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_BILLING")
	@SequenceGenerator(name = "SEQ_INS_BILLING", sequenceName = "SEQ_INS_BILLING", allocationSize = 1)
	@Column(name = "ID")
	private Long id;


	@ManyToOne(targetEntity = RemittanceMasterModel.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "F_REMITTANCE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private RemittanceMasterModel remittanceMaster;

	@ManyToOne(targetEntity = BillingMasterModel.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "F_BILLING_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private BillingMasterModel billingMaster;

	@Column(name = "IS_FINAL")
	private boolean isFinal;

	@Column(name = "D_REMITTANCE_DATE")
	private Date remittanceDate;

	@Column(name = "D_WEIGHING_DATE")
	private Date weighingDate;

	@Column(name = "D_EXPECTED_DATE")
	private Date expectedDate;

	@Column(name = "N_DELAY_IN_DAYS")
	private int delayInDays;

	@Column(name = "N_DELAY_IN_WORKING_DAYS")
	private int delayInWorkingDays;

	@Column(name = "IS_DELAY_PENALTY_APPLIED")
	private boolean isDelayPenaltyApplied;

	@Column(name = "IS_CANCELED")
	private boolean isCanceled;

	@Column(name = "N_APPLICABLE_DELAY_IN_DAYS")
	private int applicableDelayInDays;

	@Column(name = "N_FINAL_QUANTITY")
	private BigDecimal finalQuantity;

}