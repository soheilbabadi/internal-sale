package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
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
public class RemittanceTradeDataProviderDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 788276480203056653L;

	private Long id;
	private String paymentCode;
	@Schema(description = "شناسه مشتری", name = "customerId", example = "12345")
	private long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "شرکت نمونه")
	private String customerName;
	@Schema(description = "کد اقتصادی", name = "economicCode", example = "1234567890")
	private String economicCode;
	@Schema(description = "شناسه ملی", name = "nationalCode", example = "0012345678")
	private String nationalCode;
	@Schema(name = "goodId", description = "شناسه کالا", example = "1")
	private long goodId;
	@Schema(name = "goodName", description = "نام کالا", example = "کالا 1")
	private String goodName;
	@Schema(name = "contractDate", description = "تاریخ قرارداد", example = "1402/01/01")
	private String contractDate;
	@Schema(name = "contractNo", description = "شماره قرارداد", example = "123456")
	private String contractNo;
	@Schema(name = "buyerBrokerId", description = "شناسه کارگزار خریدار")
	private Long buyerBrokerId;
	@Schema(name = "buyerBrokerName", description = "نام کارگزار خریدار")
	private String buyerBrokerName;
	@Schema(name = "sellerBrokerId", description = "شناسه کارگزار فروشنده")
	private Long sellerBrokerId;
	@Schema(name = "sellerBrokerName", description = "نام کارگزار فروشنده")
	private String sellerBrokerName;
	@Schema(name = "unitCount", description = "مقدار قرارداد", example = "100")
	private Integer unitCount;
	@Schema(description = "قیمت واحد", name = "unitPrice")
	private Double unitPrice;

	@Schema(description = "مبلغ نهایی", name = "finalAmount")
	private BigDecimal finalAmount;

	@Schema(name = "settlementType", description = "شناسه نوع تسویه")
	private String settlementType;
	@Schema(name = "settlementTypeDesc", description = "نوع تسویه")
	private String settlementTypeDesc;

	@Schema(name = "contractTypeCode", description = "کد نوع قرارداد")
	private String contractTypeCode;
	@Schema(name = "contractTypeDescription", description = "شرح نوع قرارداد")
	private String contractTypeDescription;
	@Schema(name = "settlementDate", description = "تاریخ تسویه")
	private String settlementDate;
	@Schema(name = "deliveryDate", description = "تاریخ تحویل")
	private String deliveryDate;

	@Schema(description = "وضعیت جریمه تاخیر تسویه", name = "isDelayPenalty", example = "1")
	private boolean isDelayPenalty;
	private BigDecimal commission;
	@Schema(description = "نماد کالا در بورس کالا", name = "imeCommoditySymbol", example = "SYMBOL123")
	private String imeCommoditySymbol;
	private WorkflowApproveStatus workflowApproveStatus;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "شماره لات")
	private String lotNumber;


	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@ApiModel("RemittanceTradeDataProviderDto.Info")
	public static class Info extends RemittanceTradeDataProviderDto {
		@Serial
		private static final long serialVersionUID = 788276480203056653L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@ApiModel("RemittanceTradeDataProviderDto.Create")
	public static class Create extends RemittanceTradeDataProviderDto {
		@Serial
		private static final long serialVersionUID = 788276480203056653L;
	}
}
