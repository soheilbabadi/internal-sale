package com.nicico.internal.sales.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeExtractDto {
	@Schema(name = "id", description = "شناسه", example = "1")
	private Long id;
	@Schema(name = "buyerName", description = "نام خریدار", example = "شرکت ملی نفت ایران")
	private String buyerName;
	@Schema(name = "buyerNationalCode", description = "شناسه ملی خریدار", example = "1234567890")
	private String buyerNationalCode;
	@Schema(name = "commodityCode", description = "کد کالا", example = "123456")
	private Long commodityCode;
	private String commoditySymbol;
	@Schema(name = "commodityName", description = "نام کالا", example = "کالا 1")
	private String commodityName;
	@Schema(name = "cashPercentage", description = "درصد نقدی", example = "50.0")
	private BigDecimal cashPercentage;
	@Schema(name = "creditPercentage", description = "درصد اعتباری", example = "50.0")
	private BigDecimal creditPercentage;
	@Schema(name = "commission", description = "کارمزد", example = "10.0")
	private Double commission;
	@Schema(name = "contractDate", description = "تاریخ قرارداد", example = "1402/01/01")
	private String contractDate;
	@Schema(name = "contractNo", description = "شماره قرارداد", example = "123456")
	private String contractNo;
	@Schema(name = "paymentCode", description = "کد پرداخت", example = "123456")
	private String paymentCode;
	@Schema(name = "settlementType", description = "کد نوع تسویه", example = "1")
	private Integer settlementType;
	@Schema(name = "settlementTypeDesc", description = "شرح نوع تسویه", example = "نقدی")
	private String settlementTypeDesc;
	@Schema(name = "unitCount", description = "مقدار قرارداد", example = "100")
	private Integer unitCount;
	@Schema(name = "unitPrice", description = "قیمت واحد", example = "5600000")
	private Double unitPrice;
	@Schema(name = "creditUnitPrice", description = "قیمت واحد اعتباری", example = "6600000")
	private Double creditUnitPrice;
	@Schema(name = "vatCoefficient", description = "مالیات ارزش افزوده", example = "10")
	private Double vatCoefficient;
	@Schema(name = "vatAmount", description = "مبلغ مالیات ارزش افزوده", example = "5600000000")
	private Double vatAmount;
	@Schema(name = "creditAmount", description = "مبلغ بخش اعتباری", example = "5600000000")
	private Double creditAmount;
	@Schema(name = "cashAmount", description = "مبلغ بخش نقدی", example = "5600000000")
	private Double cashAmount;
	@Schema(name = "finalAmount", description = "مبلغ نهایی", example = "5600000000")
	private Double finalAmount;
	@Schema(name = "preInvoiceStatus", description = "وضعیت پیش فاکتور", example = "0")
	private String preInvoiceStatus;
	@Schema(name = "lcStatus", description = "وضعیت اعتبار اسنادی", example = "0")
	private String lcStatus;

	@Schema(description = "شناسه پیش فاکتور", name = "masterId", example = "1")
	private long masterId;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("TradeExtractDto.Create")
	@NoArgsConstructor
	public static class Create extends TradeExtractDto {
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("TradeExtractDto.Info")
	@NoArgsConstructor
	public static class Info extends TradeExtractDto {
	}
}
