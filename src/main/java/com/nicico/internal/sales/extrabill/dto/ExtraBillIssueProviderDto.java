package com.nicico.internal.sales.extrabill.dto;

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

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtraBillIssueProviderDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 2583268658804044631L;
	@Schema(description = "شناسه")
	private Long id;
	@Schema(description = "شناسه اصلی")
	private Long masterId;
	@Schema(description = "شماره پیش فاکتور")
	private String proformaNo;
	@Schema(description = "مبلغ نهایی کل")
	private BigDecimal totalFinalAmount;
	@Schema(description = "تاریخ پیش فاکتور")
	private Date performaDate;
	@Schema(description = "تاریخ قرارداد")
	private String contractDate;
	@Schema(description = "شماره قرارداد")
	private Long contractNo;
	@Schema(description = "شناسه ملی")
	private String nationalCode;
	@Schema(description = "کد پرداخت")
	private String paymentCode;
	@Schema(description = "شناسه مشتری")
	private Long customerId;
	@Schema(description = "نام مشتری")
	private String customerName;
	@Schema(description = "کد اقتصادی")
	private String economicCode;
	@Schema(description = "شناسه کالا")
	private Long goodId;
	@Schema(description = "نام کالا")
	private String goodName;
	@Schema(description = "درصد اعتباری")
	private BigDecimal creditPercentage;
	@Schema(description = "درصد نقدی")
	private BigDecimal cashPercentage;
	@Schema(description = "مبلغ کل نقدی")
	private BigDecimal totalCashAmount;
	@Schema(description = "مبلغ کل اعتباری")
	private BigDecimal totalCreditAmount;
	@Schema(description = "مبلغ کل مالیات")
	private BigDecimal totalVatAmount;
	@Schema(description = "مقدار کل")
	private BigDecimal totalQuantity;
	@Schema(description = "شناسه آگهی عرضه", name = "tradeId", example = "12")
	private Long tradeId;
	@Schema(description = "نوع پیش فاکتور")
	private String proformaIssueType;
	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	private WorkflowApproveStatus workflowApproveStatus;


	@Schema(description = "شناسه کارگزار", name = "brokerId", example = "12")
	private Long brokerId;

	@Schema(description = "نام کارگزار", name = "brokerName", example = "والکس")
	private String brokerName;

	@Schema(description = "شناسه ملی کارگزار", name = "brokerNationalCode", example = "12345678900")
	private String brokerNationalCode;


	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;


	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaLcIssueProviderDto.Info")
	public static class Info extends ExtraBillIssueProviderDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921076L;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaLcIssueProviderDto.Create")
	public static class Create extends ExtraBillIssueProviderDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921077L;
	}
}
