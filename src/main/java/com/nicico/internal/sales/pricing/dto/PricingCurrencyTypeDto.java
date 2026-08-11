package com.nicico.internal.sales.pricing.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class PricingCurrencyTypeDto implements Serializable {

	private long id;
	private String rateType;


	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PricingCurrencyTypeDto.Info")
	public static class Info extends PricingCurrencyTypeDto {
		@Serial
		private static final long serialVersionUID = -66063360509711533L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PricingCurrencyTypeDto.Create")
	public static class Create extends PricingCurrencyTypeDto {
		@Serial
		private static final long serialVersionUID = 4679779982658696994L;
	}
}