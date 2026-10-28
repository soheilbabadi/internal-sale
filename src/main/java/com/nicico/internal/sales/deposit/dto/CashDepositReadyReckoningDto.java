package com.nicico.internal.sales.deposit.dto;

import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashDepositReadyReckoningDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 2583268658804044631L;
	@Id
	@Schema(description = "شناسه")
	private Long id;

	private Long masterId;

	@Schema(description = "شماره پیش فاکتور")
	private String proformaNo;
	@Schema(description = "قیمت نهایی")
	private BigDecimal totalFinalAmount;
	@Schema(description = "تاریخ پیش فاکتور")
	private Date performaDate;
	@Schema(description = "تاریخ قرارداد")
	private String contractDate;
	@Schema(description = "شماره قرارداد")
	private Long contractNo;
	@Schema(description = "کد ملی")
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
	@Schema(description = "شناسه معامله")
	private Long tradeId;
	@Schema(description = "نوع پیش فاکتور")
	private String proformaIssueType;
	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	private WorkflowApproveStatus workflowApproveStatus;

	@Schema(description = "شناسه کارگزار", name = "brokerId", example = "15")
	private Long brokerId;

	@Schema(description = "نام کارگزار", name = "brokerName", example = "شرکت فاسابا")
	private String brokerName;

	@Schema(description = "کد ملی کارگزار")
	private String brokerNationalCode;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	@Schema(description = "وضعیت ارسال تسویه", name = "isReckoningSend", example = "true")
	private boolean isReckoningSend;

	@Schema(description = "تاریخ ارسال تسویه", name = "reckoningSendDate", example = "2024-07-15")
	private Date reckoningSendDate;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("CashDepositReadyRevokingDto.Info")
	public static class Info extends CashDepositReadyReckoningDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921076L;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("CashDepositReadyRevokingDto.Create")
	public static class Create extends CashDepositReadyReckoningDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921077L;
	}
}
