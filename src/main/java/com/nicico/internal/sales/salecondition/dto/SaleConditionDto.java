package com.nicico.internal.sales.salecondition.dto;

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

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleConditionDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 7539347627481994584L;
	private long id;
	@Schema(description = "تاریخ شروع اعتبار")
	private Date startDate;
	@Schema(description = "تاریخ انقضای اعتبار")
	private Date expireDate;
	@Schema(description = "مهلت بارگیری پس از مهلت مجاز بورس کالا-روز")
	private Integer storageDeadline;
	@Schema(description = "هزینه انبارداری روزشمار-درصد")
	private BigDecimal storageCost;
	@Schema(description = "سررسید انقضای اعتبار-ماه")
	private Integer creditExpirePeriod;
	@Schema(description = "مهلت بارگیری-روز")
	private Integer shippingDeadline;
	@Schema(description = "مهلت پرداخت وجه اعتبار اسنادی-روز")
	private Integer paymentDeferral;
	@Schema(description = "شناسه کالا")
	private Long goodId;
	@Schema(description = "نام کالا")
	private String goodName;

	@Schema(description = "شناسه کالا در بورس کالا", name = "imeCommodityId", example = "2001")
	private Long imeCommodityId;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	@Schema(description = "درصد اضافه برات")
	private BigDecimal extraBillOfExchangePercent;

	@Schema(description = "درصد اضافه اوراق گام")
	private BigDecimal extraGamCertificatePercent;


	@ApiModel("SaleConditionDto.Info")
	public static class Info extends SaleConditionDto {
		@Serial
		private static final long serialVersionUID = -1559891514322818599L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("SaleConditionDto.Create")
	public static class Create extends SaleConditionDto {
		@Serial
		private static final long serialVersionUID = -155989351432281859L;
	}
}