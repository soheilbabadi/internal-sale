package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemittanceGoodItemDto extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -4013840544922077545L;
	@Schema(description = "شناسه آیتم کالا", name = "id", example = "1")
	private Long id;
	@Schema(description = "شناسه کالا", name = "goodId", example = "1001")
	private Long goodId;
	@Schema(description = "نام کالا", name = "goodName", example = "کالا نمونه")
	private String goodName;
	@Schema(description = "شناسه واحد اندازه گیری", name = "unitId", example = "10")
	private Long unitId;

	@Schema(description = "وزن", name = "quantity", example = "14000")
	private BigDecimal quantity;
	@Schema(description = "مقدار اعتباری", name = "creditAmount", example = "5000.00")
	private BigDecimal creditAmount;
	@Schema(description = "مقدار نقدی", name = "cashAmount", example = "9000.00")
	private BigDecimal cashAmount;
	@Schema(description = "مبلغ کل", name = "totalAmount", example = "14000.00")
	private BigDecimal totalAmount;
	@Schema(description = "مبلغ مالیات بر ارزش افزوده", name = "vatAmount", example = "1400.00")
	private BigDecimal vatAmount;
	@Schema(description = "قیمت واحد اعتباری", name = "unitPriceCredit", example = "0.36")
	private BigDecimal unitPriceCredit;
	@Schema(description = "قیمت واحد نقدی", name = "unitPriceCash", example = "0.64")
	private BigDecimal unitPriceCash;
	@Schema(description = "وزن خالص", name = "netQuantity", example = "13500")
	private BigDecimal netQuantity;
	@Schema(description = "مبلغ نهایی", name = "finalAmount", example = "15400.00")
	private BigDecimal finalAmount;
	@Schema(description = "قیمت واحد", name = "unitPrice", example = "1.00")
	private BigDecimal unitPrice;
	@Schema(description = "مقدار اعتباری", name = "creditQuantity", example = "7000")
	private BigDecimal creditQuantity;
	@Schema(description = "شماره سریال/لات", name = "lotNumber", example = "LOT123456")
	private String lotNumber;
	@Schema(description = "درصد اعتباری", name = "creditPercentage", example = "50.00")
	private BigDecimal creditPercentage;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@ApiModel("RemittanceGoodItemDto.Info")
	public static class Info extends RemittanceGoodItemDto {
		@Serial
		private static final long serialVersionUID = -4013840544922077545L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@ApiModel("RemittanceGoodItemDto.Create")
	public static class Create extends RemittanceGoodItemDto {
		@Serial
		private static final long serialVersionUID = -4013840544922077545L;
	}
}
