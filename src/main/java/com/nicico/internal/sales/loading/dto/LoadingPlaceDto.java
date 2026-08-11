package com.nicico.internal.sales.loading.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoadingPlaceDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 6313070493614504250L;
	private Long id;
	private String placeTitle;
	private String placeValue;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LoadingPlaceDto.Create")
	@NoArgsConstructor
	public static class Create extends LoadingPlaceDto {
		@Serial
		private static final long serialVersionUID = -5795890566551545102L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LoadingPlaceDto.Info")
	@NoArgsConstructor
	public static class Info extends LoadingPlaceDto {
		@Serial
		private static final long serialVersionUID = -5033796553298005700L;
	}
}
