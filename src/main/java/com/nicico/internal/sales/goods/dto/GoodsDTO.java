package com.nicico.internal.sales.goods.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class GoodsDTO {

	@Schema(description = "شناسه کالا", name = "id", example = "1")
	private Long id;

	@Schema(description = "نام کالا", name = "name", example = "کاتد مس")
	private String name;

	@Schema(description = "نام انگلیسی کالا", name = "nameEn", example = "Copper Cathode")
	private String nameEn;

	@Schema(description = "شناسه کالا در PMS", name = "pmsGoodsId", example = "1001")
	private Long pmsGoodsId;

	@Schema(description = "شناسه کالا در بورس کالا", name = "imeCommodityId", example = "2001")
	private Long imeCommodityId;

	@Schema(description = "شناسه کالای والد در بورس کالا", name = "imeCommodityParentId", example = "100")
	private Long imeCommodityParentId;

	@Schema(description = "نماد کالا در بورس کالا", name = "imeCommoditySymbol", example = "CATHODE")
	private String imeCommoditySymbol;

	@Schema(description = "توضیحات", name = "description", example = "کالای عرضه شده در بورس کالا")
	private String description;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("GoodDTO.Create")
	@NoArgsConstructor
	public static class Create extends GoodsDTO {
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("GoodDTO.Info")
	@NoArgsConstructor
	public static class Info extends GoodsDTO {

		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}