package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemittanceProformaDataProviderDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1110682536465115127L;
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
	@Schema(name = "unitPrice", description = "قیمت واحد", example = "5600000")
	private Double unitPrice;

	@Schema(description = "درصد نقدی", name = "cashPercentage")
	private BigDecimal cashPercentage;
	@Schema(description = "درصد اعتباری", name = "creditPercentage")
	private BigDecimal creditPercentage;
	@Schema(description = "قیمت واحد اعتباری", name = "creditUnitPrice")
	private BigDecimal creditUnitPrice;
	@Schema(description = "مبلغ اعتباری", name = "creditAmount")
	private BigDecimal creditAmount;
	@Schema(description = "مبلغ نقدی", name = "cashAmount")
	private BigDecimal cashAmount;
	@Schema(description = "مبلغ نهایی", name = "finalAmount")
	private BigDecimal finalAmount;

	@Schema(name = "settlementType", description = "شناسه نوع تسویه")
	private String settlementType;
	@Schema(name = "settlementTypeDesc", description = "نوع تسویه")
	private String settlementTypeDesc;
	@Schema(name = "proformaMasterId", description = "شناسه قرارداد پیش فاکتور")
	private long proformaMasterId;
	@Schema(name = "proformaIssueType", description = "نوع صدور پیش فاکتور")
	@Enumerated(EnumType.STRING)
	private ProformaIssueType proformaIssueType;
	@Schema(description = "شماره پیش فاکتور", name = "proformaNo", example = "PF123456")
	private String proformaNo;
	@Schema(description = "تاریخ پیش فاکتور", name = "proformaDate", example = "2023-10-01T00:00:00Z")
	private Date proformaDate;
	@Schema(name = "contractTypeCode", description = "کد نوع قرارداد")
	private String contractTypeCode;
	@Schema(name = "contractTypeDescription", description = "شرح نوع قرارداد")
	private String contractTypeDescription;
	@Schema(name = "settlementDate", description = "تاریخ تسویه")
	private String settlementDate;
	@Schema(name = "deliveryDate", description = "تاریخ تحویل")
	private String deliveryDate;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	private Date settlementDueDate;
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;
	@Schema(name = "نام بانک بانک گشایش اعتبار اسنادی")
	private String issuerBankName;
	@Schema(name = "نام شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchName;
	@Schema(name = "کد شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchCode;
	@Schema(name = "نام بانک معامله کننده")
	private String tradingBankName;
	@Schema(name = "نام شعبه بانک معامله کننده")
	private String tradingBankBranchName;
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "67890")
	private long proformaDetailId;
	@Schema(description = "شناسه اعتبار اسنادی", name = "lcId", example = "98765")
	private Long lcId;
	@Schema(description = "شماره اعتبار اسنادی", name = "lcNo", example = "LC123456789")
	private String lcNo;
	@Schema(description = "تاریخ گشایش اعتبار اسنادی", name = "lcDate", example = "2023-10-01T00:00:00Z")
	private Date lcDate;
	@Schema(description = "وضعیت جریمه تاخیر تسویه", name = "isDelayPenalty", example = "1")
	private boolean isDelayPenalty;
	@Enumerated(EnumType.STRING)
	private WorkflowApproveStatus workflowApproveStatus;

	@Schema(description = "مقدار کل")
	private BigDecimal totalQuantity;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "شماره لات")
	private String lotNumber;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@ApiModel("RemittanceProformaDataProviderDto.Info")
	public static class Info extends RemittanceProformaDataProviderDto {
		@Serial
		private static final long serialVersionUID = 1110682536465115127L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)

	@ApiModel("RemittanceProformaDataProviderDto.Create")
	public static class Create extends RemittanceProformaDataProviderDto {
		@Serial
		private static final long serialVersionUID = 1110682536465115127L;
	}
}

