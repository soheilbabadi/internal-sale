package com.nicico.internal.sales.pms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
		select BANKLCID,b.BANKGROUPID,g.BANKGROUPDESC,BANKLCCODESHOBEH,lpad(BANKLCCODESHOBEH,10,'0')  C_FIXED_BRANCH_CODE,
		       BANKLCDESCSHOBEH from  PMS.BANKLCTBL@DBL_DS2_COPLINK b
		left join PMS.BANKGROUPTBL@DBL_DS2_COPLINK g on b.BANKGROUPID=g.BANKGROUPID
		""")
public class PMSBankLcModel implements Serializable {
	@Id
	@Column(name = "BANKLCID")
	private String id;
	@Column(name = "BANKGROUPID")
	private String bankGroupId;
	@Column(name = "BANKGROUPDESC")
	private String bank;
	@Column(name = "BANKLCCODESHOBEH")
	private String branchCode;
	@Column(name = "C_FIXED_BRANCH_CODE")
	private String fixedBranchCode;
	@Column(name = "BANKLCDESCSHOBEH")
	private String branch;
}
