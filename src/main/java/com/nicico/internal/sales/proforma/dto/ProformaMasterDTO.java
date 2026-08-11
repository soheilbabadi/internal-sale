package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProformaMasterDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = -1630168681630111761L;

	private Long id;
	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String processId;
	@Schema(description = "کد فرایندابطال", name = "reversalProcessId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String reversalProcessId;
	@Schema(description = "شماره قرارداد", name = "contractNo", example = "123456")
	private Long contractNo;
	@Schema(description = "کد پرداخت", name = "paymentCode", example = "1354376")
	private String paymentCode;
	@Schema(description = "درصد فروش نقدی ", name = "cashPercentage", example = "25")
	private BigDecimal cashPercentage;
	@Schema(description = "درصد کمیسیون", name = "commissionPercentage", example = "10")
	@Digits(integer = 8, fraction = 4)
	private BigDecimal commissionPercentage;
	@Schema(description = "مدت اعتبار", name = "deadlineDays", example = "30")
	private Integer deadlineDays;
	@Schema(description = "درصد فروش اعتباری", name = "creditPercentage", example = "15")
	private BigDecimal creditPercentage;
	@Schema(description = "جمع کل مالیات ارزش افزوده", name = "totalVatAmount", example = "1000000")
	private BigDecimal totalVatAmount;
	@Schema(description = "جمع کل مبلغ نهایی", name = "totalFinalAmount", example = "1000000")
	private BigDecimal totalFinalAmount;
	@Schema(description = "جمع کل مبلغ نقدی", name = "totalCashAmount", example = "1000000")
	private BigDecimal totalCashAmount;
	@Schema(description = "جمع کل مبلغ اعتباری", name = "totalCreditAmount", example = "1000000")
	private BigDecimal totalCreditAmount;
	@Schema(description = "تعداد کل", name = "totalQuantity", example = "1000000")
	private BigDecimal totalQuantity;
	@Schema(description = "کد مشتری", name = "customerId", example = "1")
	private Long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "مهدی حسینی")
	private String customerName;
	@Schema(description = "شماره ملی مشتری", name = "customerPhone", example = "02145678910")
	private String nationalCode;
	@Schema(description = "شماره تلفن مشتری", name = "customerPhone", example = "02145678910")
	private String phone;
	@Schema(description = "کد اقتصادی", name = "economicCode", example = "123456789")
	private String economicCode;
	@Schema(description = "شماره ثبت", name = "registerNumber", example = "123456789")
	private String registerNumber;
	@Schema(description = "کد پستی", name = "postCode", example = "123456789")
	private String postCode;
	@Schema(description = "آدرس", name = "address", example = "تهران")
	private String address;
	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	private WorkflowApproveStatus workflowApproveStatus;
	private ProformaIssueType proformaIssueType;
	@Schema(description = "شناسه کالا", name = "goodId")
	private Long goodId;
	@Schema(description = "نام کالا", name = "goodName")
	private String goodName;

	@Schema(description = "آیا فرایند به اتمام رسیده است یا خیر", name = "isProcessFinal")
	private Boolean isProcessFinal;

	@Schema(description = "آیا فرایند ابطال به اتمام رسیده است یا خیر", name = "isReversalProcessFinal")
	private Boolean isReversalProcessFinal;


	@Schema(name = "contractDate", description = "تاریخ قرارداد", example = "1402/01/01")
	private String contractDate;


	@Schema(description = "شناسه کارگزار", name = "brokerId", example = "15")
	private Long brokerId;

	@Schema(description = "نام کارگزار", name = "brokerName", example = "شرکت فاسابا")
	private String brokerName;

	private String brokerNationalCode;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	@Schema(name = "settlementType", description = "نوع تسویه")
	private String settlementType;

	@Schema(description = "شناسه آگهی عرضه", name = "tradeId", example = "12")
	private Long tradeId;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("PerformaMasterDTO.Info")
	public static class Info extends ProformaMasterDTO {
		@Serial
		private static final long serialVersionUID = 4899701044712042162L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("PerformaMasterDTO.Create")
	public static class Create extends ProformaMasterDTO {
		@Serial
		private static final long serialVersionUID = 4899701044712042162L;
	}
}