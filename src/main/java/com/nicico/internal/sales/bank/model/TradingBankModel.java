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
import javax.validation.constraints.Pattern;
import java.io.Serial;

@Audited
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "T_INS_TRADING_BANK")
public class TradingBankModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -460056109686678728L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_BANK_BRANCH")
	@SequenceGenerator(name = "SEQ_INS_BANK_BRANCH", sequenceName = "SEQ_INS_BANK_BRANCH", allocationSize = 1)
	@Schema(name = "شناسه بانک")
	private long id;
	@Schema(name = "نام بانک")
	@Column(name = "C_BANK_TITLE", nullable = false)
	private String bankTitle;
	@Schema(name = "عنوان شعبه")
	@Column(name = "C_BRANCH_TITLE", nullable = false)
	private String bankBranchTitle;
	@Schema(name = "کد شعبه")
	@Column(name = "C_BRANCH_CODE", nullable = false)
	private String branchCode;
	@Schema(name = "حساب سپرده")
	@Column(name = "c_account_number", length = 100)
	private String accountNumber;
	@Schema(name = "شماره شبا", example = "IR0696000000010324200001")
	@Pattern(regexp = "^IR\\d{24}$", message = "شماره شبا باید با IR شروع شود و 24 رقم بعد از آن باشد")
	@Column(name = "C_IBAN", length = 26)
	private String iban;
	@NotAudited
	@Formula("(lpad(C_BRANCH_CODE,10,'0'))")
	private String fixedBranchCode;
}