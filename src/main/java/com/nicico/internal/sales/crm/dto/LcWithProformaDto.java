package com.nicico.internal.sales.crm.dto;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.SaleType;
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
@NoArgsConstructor
@AllArgsConstructor

public class LcWithProformaDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -702671335129654053L;

	@Schema(description = "شناسه اعتبار اسنادی", name = "lcId", example = "1")
	private Long lcId;
	@Schema(description = "شماره پیش فاکتور", name = "performaNo", example = "PF-2024-001")
	private String performaNo;
	@Schema(description = "تاریخ پیش فاکتور", name = "performaDate", example = "1403-03-15")
	private String performaDate;
	@Schema(description = "شماره قرارداد", name = "contractNo", example = "100")
	private Long contractNo;
	@Schema(description = "شماره اعتبار اسنادی", name = "lcNo", example = "LC-2024-001")
	private String lcNo;
	@Schema(description = "تاریخ گشایش اعتبار", name = "lcDate", example = "2024-06-09")
	private Date lcDate;
	@Schema(description = "شناسه بانک معامله گر", name = "tradingBankId", example = "5")
	private Long tradingBankId;
	@Schema(description = "نام بانک معامله گر", name = "tradingBankTitle", example = "بانک ملی")
	private String tradingBankTitle;
	@Schema(description = "عنوان شعبه بانک معامله گر", name = "tradingBankBranchTitle", example = "شعبه مرکزی")
	private String tradingBankBranchTitle;
	@Schema(description = "دوره اعتبار", name = "creditExpirePeriod", example = "120")
	private Integer creditExpirePeriod;
	@Schema(description = "تعویق پرداخت", name = "paymentDeferral", example = "60")
	private Integer paymentDeferral;
	@Schema(description = "روزهای مهلت", name = "deadlineDays", example = "45")
	private Integer deadlineDays;
	@Schema(description = "وضعیت تایید گردش کار", name = "workflowApproveStatus", example = "APPROVED")
	private WorkflowApproveStatus workflowApproveStatus;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2024-09-07")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2024-08-24")
	private Date settlementDueDate;
	@Schema(description = "شناسه بانک صادر کننده", name = "issuerBankId", example = "10")
	private Long issuerBankId;
	@Schema(description = "نام بانک صادر کننده", name = "issuerBankName", example = "بانک صادرات")
	private String issuerBankName;
	@Schema(description = "نام شعبه بانک صادر کننده", name = "issuerBankBranchName", example = "شعبه تهران")
	private String issuerBankBranchName;
	@Schema(description = "کد بانک صادر کننده", name = "issuerBankCode", example = "0013")
	private String issuerBankCode;
	@Schema(description = "شناسه فایل اعتبار اسنادی", name = "lcAttachmentId", example = "FILE-LC-123")
	private String lcAttachmentId;
	@Schema(description = "شناسه فایل ارسال", name = "dispatchAttachmentId", example = "FILE-DISP-456")
	private String dispatchAttachmentId;
	@Schema(description = "شناسه فایل پیش فاکتور", name = "proformaFileId", example = "FILE-PROF-789")
	private String proformaFileId;
	@Schema(description = "شناسه اصلی پیش فاکتور", name = "proformaMasterId", example = "50")
	private Long proformaMasterId;
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "51")
	private Long proformaDetailId;
	@Schema(description = "شناسه نمونه اعتبار اسنادی", name = "lcInstanceId", example = "INST-LC-001")
	private String lcInstanceId;
	@Schema(description = "شناسه سند اطلاعیه", name = "notificationDocumentId", example = "DOC-NOT-123")
	private String notificationDocumentId;
	@Schema(description = "تاییدیه", name = "acknowledgment", example = "ACKNOWLEDGED")
	private Acknowledgment acknowledgment;
	@Schema(description = "وضعیت ارسال تسویه", name = "isReckoningSend", example = "true")
	private Boolean isReckoningSend;
	@Schema(description = "تاریخ ارسال تسویه", name = "reckoningSendDate", example = "2024-07-15")
	private Date reckoningSendDate;
	@Schema(description = "تاریخ ابطال ال سی", name = "cancelDate", example = "2024-08-01")
	private Date cancelDate;
	@Schema(description = "دلیل ابطال", name = "lcCancellationReason", example = "BUYER_WITHDRAWAL")
	private LcCancellationReason lcCancellationReason;
	@Schema(description = "تاریخ قرارداد", name = "contractDate", example = "1403-02-20")
	private String contractDate;
	@Schema(description = "شناسه کارگزار", name = "brokerId", example = "15")
	private Long brokerId;
	@Schema(description = "نام کارگزار", name = "brokerName", example = "شرکت فاسابا")
	private String brokerName;
	@Schema(description = "مقدار کل", name = "totalQuantity", example = "1000.50")
	private BigDecimal totalQuantity;
	@Schema(description = "مبلغ نهایی کل", name = "totalFinalAmount", example = "50000000")
	private BigDecimal totalFinalAmount;
	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;
	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;
	@Schema(description = "شناسه کالا", name = "goodId", example = "25")
	private Long goodId;
	@Schema(description = "نام کالا", name = "goodName", example = "نفت خام")
	private String goodName;
	@Schema(description = "درصد نقدی", name = "cashPercentage", example = "30")
	private BigDecimal cashPercentage;
	@Schema(description = "درصد اعتباری", name = "creditPercentage", example = "70")
	private BigDecimal creditPercentage;
	@Schema(description = "مبلغ کل مالیات بر ارزش افزوده", name = "totalVatAmount", example = "5000000")
	private BigDecimal totalVatAmount;
	@Schema(description = "مبلغ کل نقدی", name = "totalCashAmount", example = "15000000")
	private BigDecimal totalCashAmount;
	@Schema(description = "مبلغ کل اعتباری", name = "totalCreditAmount", example = "35000000")
	private BigDecimal totalCreditAmount;
	@Schema(description = "شناسه مشتری", name = "customerId", example = "3458")
	private Long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "دنیای مس کاشان")
	private String customerName;
	@Schema(description = "کد ملی", name = "nationalCode", example = "1234567890")
	private String nationalCode;
	@Schema(description = "تلفن", name = "phone", example = "021-12345678")
	private String phone;
	@Schema(description = "کد اقتصادی", name = "economicCode", example = "411111111111")
	private String economicCode;
	@Schema(description = "شماره ثبت", name = "registerNumber", example = "1234567")
	private String registerNumber;
	@Schema(description = "کد پستی", name = "postCode", example = "1234567890")
	private String postCode;
	@Schema(description = "آدرس", name = "address", example = "تهران، خیابان فردوسی")
	private String address;
	@Schema(description = "شناسه آگهی عرضه", name = "tradeId", example = "12")
	private Long tradeId;
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "performaDetailId", example = "52")
	private Long performaDetailId;
	@Schema(description = "سال مالی", name = "jalaaliYear", example = "1403")
	private Integer jalaaliYear;
	@Schema(description = "قیمت نهایی", name = "finalPrice", example = "50000.75")
	private BigDecimal finalPrice;
	@Schema(description = "تاریخ جزئیات پرفرما", name = "performaDetailDate", example = "2024-06-05")
	private Date performaDetailDate;
	@Schema(description = "نوع فروش-صرفا EXWORKS", name = "saleType", example = "EXWORKS")
	private SaleType saleType;
	@Schema(description = "مبلغ کل", name = "totalAmount", example = "50000000")
	private BigDecimal totalAmount;
	@Schema(description = "مهلت بارگیری پس از مهلت مجاز بورس کالا-روز", name = "storageDeadline", example = "30")
	private Integer storageDeadline;
	@Schema(description = "تاریخ سفارش", name = "orderDate", example = "2024-05-20")
	private Date orderDate;
	@Schema(description = "هزینه انبارداری روزشمار-درصد", name = "storageCost", example = "500000")
	private BigDecimal storageCost;


	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LcWithProformaDto.Create")
	@NoArgsConstructor
	public static class Create extends LcWithProformaDto {
		@Serial
		private static final long serialVersionUID = -702671335129654053L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LcWithProformaDto.Info")
	@NoArgsConstructor
	public static class Info extends LcWithProformaDto {
		@Serial
		private static final long serialVersionUID = -702671335129654053L;
		@Schema(description = "تاریخ ایجاد", name = "createdDate", example = "2024-06-09")
		private Date createdDate;
		@Schema(description = "تاریخ آخرین ویرایش", name = "lastModifiedDate", example = "2024-06-09")
		private Date lastModifiedDate;
		@Schema(description = "کاربر ایجاد کننده", name = "createdBy", example = "admin")
		private String createdBy;
		@Schema(description = "کاربر ویرایش کننده", name = "lastModifiedBy", example = "admin")
		private String lastModifiedBy;
		@Schema(description = "نظر", name = "comment", example = "تایید شده")
		private String comment;
	}
}