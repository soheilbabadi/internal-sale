package com.nicico.internal.sales.vat.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "t_ins_tax_vat")
@Setter
@Getter
@Audited
public class TaxVatModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 590214217382453171L;
	@Id
	private Long id;
	@Column(name = "jalali_year", nullable = false)
	private Integer jalaliYear;
	@Column(name = "EMISSION_TAX")
	@DecimalMax(value = "100", message = "percent between 0 and 100")
	@DecimalMin(value = "0", message = "percent between 0 and 100")
	private BigDecimal emissionTax;
	@Column(name = "tax_coefficient")
	@DecimalMax(value = "100", message = "percent between 0 and 100")
	@DecimalMin(value = "0", message = "percent between 0 and 100")
	private BigDecimal taxCoefficient;
	@Column(name = "vat_coefficient")
	@DecimalMax(value = "100", message = "percent between 0 and 100")
	@DecimalMin(value = "0", inclusive = false, message = "percent between 0 and 100")
	private BigDecimal vatCoefficient;
}
