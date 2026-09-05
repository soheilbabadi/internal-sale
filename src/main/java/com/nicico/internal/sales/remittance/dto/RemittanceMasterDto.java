package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.remittance.enums.IssueSourceType;
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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemittanceMasterDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -5990888298560371449L;
	private Long id;

	@Schema(description = "شماره حواله", name = "remittanceNumber", example = "RM123456789")
	private String remittanceNumber;

	@Schema(description = "تاریخ حواله", name = "remittanceDate", example = "2023-10-01T00:00:00Z")
	private Date remittanceDate;

	@Schema(description = "تاریخ اعتبار", name = "validityDate", example = "2023-12-31T00:00:00Z")
	private Date validityDate;

	@Schema(description = "شناسه مشتری", name = "customerId", example = "12345")
	private Long customerId;

	@Schema(description = "نام مشتری", name = "customerName", example = "شرکت نمونه")
	private String customerName;

	@Schema(description = "کد اقتصادی", name = "economicCode", example = "1234567890")
	private String economicCode;

	@Schema(description = "شناسه ملی", name = "nationalCode", example = "0012345678")
	private String nationalCode;

	@Schema(description = "آدرس مقصد", name = "targetAddress", example = "تهران، خیابان نمونه، پلاک ۱۲۳")
	private String targetAddress;

	@Schema(description = "شناسه محل بارگیری", name = "loadingPortId", example = "67890")
	private Long loadingPortId;

	@Schema(description = "محل بارگیری", name = "loadingPort", example = "بندر عباس")
	private String loadingPort;

	@Schema(description = "شناسه کالا", name = "goodId", example = "54321")
	private Long goodId;

	@Schema(description = "نام کالا", name = "goodName", example = "مس کاتد")
	private String goodName;

	@Schema(name = "packingName", description = "نام بسته بندی", example = "بشکه")
	private String packingName;

	@Schema(name = "packingId", description = "شناسه بسته بندی", example = "2")
	private Integer packingId;

	@Schema(description = "شماره لات", name = "lotNumber", example = "7630")
	private String lotNumber;

	@Schema(description = "تاریخ قرارداد", name = "contractDate", example = "2023-09-15T00:00:00Z")
	private Date contractDate;
	@Schema(description = "شماره قرارداد", name = "contractNo", example = "CT20230915001")
	private String contractNo;

	@Schema(description = "نام کارگزار فروشنده", name = "sellerBrokerName", example = "کارگزار نمونه")
	private String sellerBrokerName;
	@Schema(description = "نام کارگزار خریدار", name = "buyerBrokerName", example = "کارگزار نمونه ۲")
	private String buyerBrokerName;
	@Schema(description = "درصد نقدی", name = "cashPercentage", example = "60.5")
	private BigDecimal cashPercentage;
	@Schema(description = "درصد اعتباری", name = "creditPercentage", example = "39.5")
	private BigDecimal creditPercentage;
	@Schema(description = "مقدار حواله", name = "remittanceQuantity", example = "1000.00")
	private BigDecimal remittanceQuantity;
	@Schema(description = "مقدار نقدی حواله", name = "remittanceQuantityCash", example = "250.75")
	private BigDecimal remittanceQuantityCash;
	@Schema(description = "مقدار اعتباری حواله", name = "remittanceQuantityCredit", example = "749.25")
	private BigDecimal remittanceQuantityCredit;
	@Schema(description = "قیمت واحد حواله نقدی", name = "remittanceUnitPriceCash", example = "5000.00")
	private BigDecimal remittanceUnitPriceCash;
	@Schema(description = "قیمت واحد حواله اعتباری", name = "remittanceUnitPriceCredit", example = "4800.00")
	private BigDecimal remittanceUnitPriceCredit;
	@Schema(description = "مابه تفاوت مالیات ارزش افزوده", name = "taxAmount", example = "4800.00")
	private BigDecimal taxAmount;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	private Date settlementDueDate;
	@Schema(name = "شناسه بانک")
	private Long tradingBankId;
	@Schema(name = "نام بانک")
	private String tradingBankTitle;
	@Schema(name = "عنوان شعبه")
	private String tradingBankBranchTitle;
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;
	@Schema(name = "نام بانک بانک گشایش اعتبار اسنادی")
	private String issuerBankName;
	@Schema(name = "نام شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchName;
	@Schema(name = "کد شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchCode;

	@Schema(description = "شناسه فایل حواله", name = "remittanceFileId", example = "FILE123456")
	private String remittanceFileId;

	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String processId;
	@Schema(description = "وضعیت تایید فرایند", name = "workflowApproveStatus", example = "IN_PROGRESS")
	@Enumerated(EnumType.STRING)
	private WorkflowApproveStatus workflowApproveStatus;
	@Schema(description = "شناسه پیش فاکتور اصلی", name = "proformaMasterId", example = "12345")
	private Long proformaMasterId;
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "67890")
	private Long proformaDetailId;
	@Schema(description = "شناسه اعتبار اسنادی", name = "lcId", example = "98765")
	private Long lcId;
	@Schema(description = "شماره اعتبار اسنادی", name = "lcNo", example = "LC123456789")
	private String lcNo;
	@Schema(description = " شناسه محل صدور حواله", name = "issuePlaceId", example = "1")
	private Long issuePlaceId;

	@Schema(description = " محل صدور حواله", name = "issuePlaceId", example = "فروش تهران")
	private String issuePlace;


	@Schema(description = "شماره پیش فاکتور", name = "proformaNo", example = "10440000")
	private String proformaNo;

	@Schema(description = "تاریخ صدور پیش فاکتور", name = "proformaDate", example = "2023-10-01T00:00:00Z")
	private Date proformaDate;

	@Schema(description = "نوع صدور پیش فاکتور", name = "proformaIssueType", example = "INTERNAL")
	@Enumerated(EnumType.STRING)
	private ProformaIssueType proformaIssueType;

	@Schema(description = "نوع منبع صدور حواله", name = "issuSourceType", example = "TRADE")
	@Enumerated(EnumType.STRING)
	private IssueSourceType issueSourceType;

	private List<RemittanceGoodItemDto.Info> remittanceGoodItemDtos;

	@Schema(description = "آیا فرایند به پایان رسیده است؟", name = "isProcessFinal", example = "true")
	private boolean isProcessFinal;

	@Schema(description = "کد پرداخت", name = "paymentCode", example = "PC123456")
	private String paymentCode;

	@Schema(description = "شناسه معامله", name = "tradeId", example = "54321")
	private Long tradeId;


	@Schema(description = "جریمه تاخیر در تحویل", name = "isDelayPenalty", example = "false")
	private boolean isDelayPenalty;


	@Schema(name = "settlementType", description = "شناسه نوع تسویه")
	private String settlementType;

	@Schema(name = "settlementTypeDesc", description = "نوع تسویه")
	private String settlementTypeDesc;

	@Schema(name = "pmsId", description = "شناسه لجستیک", example = "126-11")
	private String pmsId;


	@Schema(description = "شناسه کارگزار", name = "brokerId", example = "12")
	private Long brokerId;

	@Schema(description = "نام کارگزار", name = "brokerName", example = "والکس")
	private String brokerName;

	@Schema(description = "شناسه ملی کارگزار", name = "brokerNationalCode", example = "12345678900")
	private String brokerNationalCode;

	@Schema(description = "وزن کل", name = "totalQuantity", example = "44000")
	private BigDecimal totalQuantity;

	@Schema(description = "مبلغ نهایی", name = "totalFinalAmount", example = "44000000")
	private BigDecimal totalFinalAmount;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;


	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("RemittanceMasterDto.Info")
	public static class Info extends RemittanceMasterDto {
		@Serial
		private static final long serialVersionUID = -5990888298560371449L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("RemittanceMasterDto.Create")
	public static class Create extends RemittanceMasterDto {
		@Serial
		private static final long serialVersionUID = -5990888298560371449L;
	}
}
