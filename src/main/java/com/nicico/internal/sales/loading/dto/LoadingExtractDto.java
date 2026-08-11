package com.nicico.internal.sales.loading.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadingExtractDto implements java.io.Serializable {
	@Serial
	private static final long serialVersionUID = -5659483848575464166L;
	private Long id;
	private Long goodId;
	private String goodName;
	private String goodDescription;
	private Long loadingPlaceId;
	private String loadingPlaceTitle;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LoadingExtractDto.Create")
	@NoArgsConstructor
	public static class Create extends LoadingExtractDto {
		@Serial
		private static final long serialVersionUID = -5795890566551545102L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LoadingExtractDto.Info")
	@NoArgsConstructor
	public static class Info extends LoadingExtractDto {
		@Serial
		private static final long serialVersionUID = -5033796553298005700L;
	}
}
