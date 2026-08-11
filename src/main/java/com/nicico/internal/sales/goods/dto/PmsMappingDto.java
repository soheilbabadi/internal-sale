package com.nicico.internal.sales.goods.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PmsMappingDto {
	private Long pmsId;
	private String goodName;
	private String pmsName;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("PmsMappingDto.Create")
	@NoArgsConstructor
	public static class Create extends PmsMappingDto {
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("PmsMappingDto.Info")
	@NoArgsConstructor
	public static class Info extends PmsMappingDto {
	}
}
