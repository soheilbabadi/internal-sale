package com.nicico.internal.sales.history.dto;


import com.nicico.internal.sales.lc.enums.Acknowledgment;
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
public class HistoryExtractMasterDto implements Serializable {


	@Serial
	private static final long serialVersionUID = -2562752336835329769L;

	@Schema(name = "id", description = "شناسه")
	private Long id;

	@Schema(name = "buyerName", description = "نام خریدار", example = "شرکت ملی نفت ایران")
	private String buyerName;

	@Schema(name = "buyerNationalCode", description = "شناسه ملی خریدار", example = "1234567890")
	private String buyerNationalCode;

	@Schema(name = "commodityCode", description = "کد کالا", example = "123456")
	private Long commodityCode;

	@Schema(name = "commoditySymbol", description = "نماد در بورس کالا", example = "HABVC")
	private String commoditySymbol;

	@Schema(name = "commodityName", description = "نام کالا", example = "کالا 1")
	private String commodityName;

	@Schema(description = "توضیحات اطلاعیه عرضه")
	private String offerDescription;

	@Schema(name = "cashPercentage", description = "درصد نقدی", example = "50.0")
	private BigDecimal cashPercentage;

	@Schema(description = "درصد فروش اعتباری", name = "creditPercentage", example = "15")
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
	private String settlementType;

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

	@Schema(name = "vatCreditAmount", description = "مبلغ بخش اعتباری", example = "5600000000")
	private Double creditAmount;

	@Schema(name = "cashAmount", description = "مبلغ بخش نقدی", example = "5600000000")
	private Double cashAmount;

	@Schema(name = "finalAmount", description = "مبلغ نهایی", example = "5600000000")
	private Double finalAmount;

	@Schema(name = "preInvoiceStatus", description = "وضعیت پیش فاکتور", example = "APPROVED")
	private String preinvoiceStatus;


	@Schema(name = "lcStatus", description = "وضعیت اعتبار اسنادی", example = "APPROVED")
	private String lcStatus;

	private String remittanceStatus;

	private Long masterId;

	private Long remittanceId;

	private String performaNo;

	private String performaDate;

	private String lcNo;

	private String lcDate;

	private Date remittanceDate;

	private String remittanceNo;

	private String remittancePmsId;

	private String lcPmsId;

	@Schema(description = "تاییدیه نوع تسویه و نوع حواله", name = "acknowledgment")
	private Acknowledgment acknowledgment;


	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("HistoryExtractMasterDto.Info")
	@NoArgsConstructor
	public static class Info extends HistoryExtractMasterDto {
		@Serial
		private static final long serialVersionUID = 446441151362666829L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("HistoryExtractMasterDto.Create")
	@NoArgsConstructor
	public static class Create extends HistoryExtractMasterDto {
		@Serial
		private static final long serialVersionUID = 5113007817865605640L;
	}
}
