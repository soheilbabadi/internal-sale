package com.nicico.internal.sales.bank.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Audited
@Table(name = "T_INS_ISSUING_BANKS", indexes = {@Index(name = "idx_branch_code", columnList = "C_BRANCH_CODE")})
public class IssuingBankModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -3439337282925988249L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_BANK_BRANCH")
	@SequenceGenerator(name = "SEQ_INS_BANK_BRANCH", sequenceName = "SEQ_INS_BANK_BRANCH", allocationSize = 1)
	@Schema(name = "شناسه بانک")
	private Long id;
	@Column(name = "C_BANK_NAME", nullable = false, length = 200)
	@Schema(name = "نام بانک")
	private String bankName;
	@Column(name = "C_BRANCH_NAME", nullable = false, length = 200)
	@Schema(name = "نام شعبه")
	private String branchName;
	@Column(name = "C_BRANCH_CODE", length = 200)
	@Schema(name = "کد شعبه")
	private String branchCode;
	@Column(name = "C_PROVINCE", length = 200)
	@Schema(name = "استان شعبه")
	private String province;
	@Column(name = "C_CITY", length = 200)
	@Schema(name = "شهر شعبه")
	private String city;
	@Schema(name = "کد بانک")
	@Column(name = "C_BANK_CODE", length = 50)
	private String bankCode;
	@NotAudited
	@ManyToOne(targetEntity = BaseBankModel.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "C_BANK_CODE", referencedColumnName = "C_BANK_CODE", insertable = false, updatable = false)
	@EqualsAndHashCode.Exclude
	private BaseBankModel baseBankModel;

	@Column(name = "C_BASE_NOSA_CODE", length = 50, nullable = false)
	private String baseNosaCode;
	@NotAudited
	@Formula("(lpad(C_BRANCH_CODE,10,'0'))")
	private String fixedBranchCode;

}
