package com.nicico.internal.sales.pricing.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Schema(description = "فرمول های قیمت گذاری")
@Entity
@Table(name = "T_INS_PRICE_FORMULA")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PricingFormulaModel extends BaseClassModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRICE_FORMULA")
	@SequenceGenerator(name = "SEQ_PRICE_FORMULA", sequenceName = "SEQ_PRICE_FORMULA", allocationSize = 1)
	private Long id;

	@Schema(description = "کد فرمول")
	@Column(name = "c_formula_code", length = 100, nullable = false, unique = true)
	private String code;

	@Schema(description = "نام فرمول")
	@Column(name = "c_formula_name", length = 200)
	private String name;

	@Schema(description = "عبارت SpEL")
	@Column(name = "c_spel_expression", length = 4000)
	private String spelExpression;


}
