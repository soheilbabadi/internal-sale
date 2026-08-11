package com.nicico.internal.sales.bank.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "t_ins_bank_base")
public class BaseBankModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 3368409053301370808L;
	@Id
	private long id;

	@Column(name = "C_BANK_CODE", length = 50, nullable = false)
	private String bankCode;

	@Column(name = "C_BANK_TITLE", length = 100, nullable = false, unique = true)
	private String bankTitle;

	@Column(name = "C_BASE_NOSA_CODE", length = 50, nullable = false)
	private String baseNosaCode;


	@Column(name = "C_NATIONAL_CODE", length = 15, nullable = false)
	private String nationalCode;

}
