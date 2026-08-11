package com.nicico.internal.sales.goods.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodBucketDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -6359293904161363769L;
	private long id;
	@Schema(name = "goodId", description = "شناسه کالا", example = "1")
	private long goodId;
	@Schema(description = "شناسه کالا در بورس کالا", name = "imeCommodityId", example = "2001")
	private Long imeCommodityId;
	@Schema(name = "goodName", description = "نام کالا", example = "کالا 1")
	private String goodName;
	@Schema(name = "startDate", description = "تاریخ شروع", example = "2023-01-01")
	private Date startDate;
	@Schema(name = "expireDate", description = "تاریخ انقضا", example = "2023-12-31")
	private Date expireDate;
	@Schema(name = "packagingSize", description = "ظرفیت بسته بندی هر واحد", example = "500")
	private BigDecimal packagingSize;
	@Schema(name = "packingId", description = "شناسه بسته بندی", example = "156")
	private Integer packingId;
	@Schema(name = "packingName", description = "نام بسته بندی", example = "بشکه")
	private String packingName;
	@Schema(name = "cashPercentage", description = "درصد نقدی", example = "53")
	private BigDecimal cashPercentage;
	@Schema(name = "commission", description = "درصد کمسیون مس", example = "6")
	private Double commission;
	@Schema(name = "divisibilityCheck", description = "قابلیت تقسیم-حداقل مقداری که میتوان فروخت", example = "1")
	private BigDecimal divisibilityCheck;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("GoodBucketDto.Create")
	@NoArgsConstructor
	public static class Create extends GoodBucketDto {
		@Serial
		private static final long serialVersionUID = -7747104093940814509L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("GoodBucketDto.Info")
	@NoArgsConstructor
	public static class Info extends GoodBucketDto {
		@Serial
		private static final long serialVersionUID = -6955842500879602565L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
