package com.nicico.internal.sales.pricing.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Schema(description = "عنوان ارز تخصیص یافته")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "T_INS_PRICE_CURRENCY_TYPE")
public class PricingCurrencyTypeModel extends BaseClassModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ins_price_currency")
	@SequenceGenerator(name = "seq_ins_price_currency", sequenceName = "seq_ins_price_currency", allocationSize = 1)
	private long id;

	@Column(name = "C_RATE_TYPE", nullable = false, length = 100)
	private String rateType;
}