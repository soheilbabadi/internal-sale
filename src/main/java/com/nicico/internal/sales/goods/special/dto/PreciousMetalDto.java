package com.nicico.internal.sales.goods.special.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreciousMetalDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -534387196218264079L;
	private Long id;
	private String name;

	@Schema(description = "شناسه کالا در بورس کالا", name = "imeCommodityId", example = "2001")
	private Long imeCommodityId;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("PreciousMetalDto.Info")
	public static class Info extends PreciousMetalDto {
		@Serial
		private static final long serialVersionUID = 1338821466234532601L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("PreciousMetalDto.Create")
	public static class Create extends PreciousMetalDto {
		@Serial
		private static final long serialVersionUID = 5259994421976302320L;
	}
}
