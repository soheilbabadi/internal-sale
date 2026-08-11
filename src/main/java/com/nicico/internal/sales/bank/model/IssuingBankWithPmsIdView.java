package com.nicico.internal.sales.bank.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
		SELECT  b.*, pms.BANKLCID c_pms_lc_id,g.BANKGROUPID c_pms_bank_base_id
		        FROM  t_ins_bank_base base
		            left join T_INS_ISSUING_BANKS b on base.C_BANK_CODE=b.C_BANK_CODE
		            LEFT JOIN PMS.BANKGROUPTBL@DBL_DS2_COPLINK g
		               ON base.C_BANK_TITLE = g.BANKGROUPDESC
		        LEFT JOIN PMS.BANKLCTBL@DBL_DS2_COPLINK  pms ON g.BANKGROUPID = pms.BANKGROUPID and  LPAD(pms.BANKLCCODESHOBEH,10,'0') = LPAD(b.C_BRANCH_CODE,10,'0')
		
		""")
public class IssuingBankWithPmsIdView implements Serializable {
	@Serial
	private static final long serialVersionUID = -3439337282925988249L;
	@Id
	private long id;
	@Column(name = "C_BANK_NAME")
	@Schema(name = "نام بانک")
	private String bankName;
	@Column(name = "C_BRANCH_NAME")
	@Schema(name = "نام شعبه")
	private String branchName;
	@Column(name = "C_BRANCH_CODE")
	@Schema(name = "کد شعبه")
	private String branchCode;
	@Column(name = "C_PROVINCE")
	@Schema(name = "استان شعبه")
	private String province;
	@Column(name = "C_CITY")
	@Schema(name = "شهر شعبه")
	private String city;
	@Schema(name = "کد بانک")
	@Column(name = "C_BANK_CODE")
	private String bankCode;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "C_BANK_CODE", referencedColumnName = "C_BANK_CODE", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private BaseBankModel baseBankModel;
	@Column(name = "C_BASE_NOSA_CODE")
	private String baseNosaCode;
	@Column(name = "c_pms_lc_id")
	private String pmsLcBankId;
	@Column(name = "c_pms_bank_base_id")
	private String pmsBaseBankId;

}
