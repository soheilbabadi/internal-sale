package com.nicico.internal.sales.proforma.dto;

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
@AllArgsConstructor
@NoArgsConstructor
public class ProformaGoodItemDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1071096340568594886L;
	@Schema(description = "شناسه کالا", name = "goodsId", example = "2001")
	private long goodsId;
	@Schema(description = "نام کالا", name = "goodsName", example = "Laptop")
	private String goodsName;
	@Schema(description = "نام واحد شمارش", name = "unitName", example = "Piece")
	private String unitName;
	@Schema(description = "شناسه واحد شمارش", name = "unitId", example = "3001")
	private long unitId;
	@Schema(description = "مبلغ نقدی", name = "cashAmount", example = "2000.00")
	private BigDecimal cashAmount;
	@Schema(description = "مبلغ کل", name = "totalAmount", example = "7000.00")
	private BigDecimal totalAmount;
	@Schema(description = "درصد مالیات ارزش افزوده", name = "vatPercent", example = "9.0")
	private BigDecimal vatPercent;
	@Schema(description = "مبلغ مالیات ارزش افزوده", name = "vatAmount", example = "630.00")
	private BigDecimal vatAmount;
	@Schema(description = "مبلغ نهایی", name = "finalPrice", example = "7630.00")
	private BigDecimal finalAmount;
	@Schema(description = "شماره لات", name = "lotNumber", example = "7630")
	private String lotNumber;
	@Schema(description = "قیمت واحد", name = "unitPrice", example = "423000")
	private BigDecimal unitPrice;
	@Schema(description = "تعداد/مقدار", name = "quantity", example = "10")
	private BigDecimal quantity;
	@Schema(description = "مبلغ اعتباری", name = "creditAmount", example = "5000.00")
	private BigDecimal creditAmount;
	@Schema(description = "مالیات ارزش افزوده نقدی", name = "vatCashAmount", example = "76300000")
	private BigDecimal vatCashAmount;
	@Schema(description = "مالیات ارزش افزوده اعتباری", name = "vatCreditAmount", example = "763000000")
	private BigDecimal vatCreditAmount;
	@Schema(description = "قیمت واحد اعتباری", name = "unitPriceCredit", example = "76300000")
	private BigDecimal unitPriceCredit;
	@Schema(description = "قیمت واحد نقدی", name = "unitPriceCash", example = "76300000")
	private BigDecimal unitPriceCash;
	@Schema(description = "درصد کارمزد قیمت اعتباری", name = "interestPercent", example = "76300000")
	private BigDecimal interestPercent;
	@Schema(description = "تعداد/مقدار خالص", name = "netQuantity", example = "76300000")
	private BigDecimal netQuantity;
	@Schema(description = "تعداد/مقدار اعتباری", name = "creditQuantity", example = "76300000")
	private BigDecimal creditQuantity;
	@Schema(description = "درصد اعتباری", name = "commissionPercentage", example = "10")
	private BigDecimal creditPercentage;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("PerformaGoodItemDto.Info")
	public static class Info extends ProformaGoodItemDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921075L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("PerformaGoodItemDto.Create")
	public static class Create extends ProformaGoodItemDto {
		@Serial
		private static final long serialVersionUID = -6875168459138326735L;
	}
}
