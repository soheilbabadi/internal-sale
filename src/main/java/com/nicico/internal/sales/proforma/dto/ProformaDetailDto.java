package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.SaleType;
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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProformaDetailDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -8232856503968765193L;
	private long id;
	private int jalaaliYear;
	private BigDecimal finalPrice;
	@Schema(description = "شماره پیش فاکتور")
	private String performaNo;
	@Schema(description = "تاریخ پیش فاکتور")
	private Date performaDate;
	@Schema(description = "مبنای فروش")
	private SaleType saleType;

	@Schema(name = "settlementType", description = "نوع تسویه")
	private String settlementType;
	@Schema(description = "تاریخ گشایش اعتبار")
	private BigDecimal vatAmount;
	private BigDecimal totalAmount;
	@Schema(description = "مهلت بارگیری پس از مهلت مجاز بورس کالا-روز")
	private Integer storageDeadline;
	@Schema(description = "هزینه انبارداری روزشمار-درصد")
	private BigDecimal storageCost;
	@Schema(description = "سررسید انقضای اعتبار-ماه")
	private Integer creditExpirePeriod;
	@Schema(description = "مهلت بارگیری-روز")
	private Integer shippingDeadline;
	@Schema(description = "مهلت پرداخت وجه اعتبار اسنادی-روز")
	private Integer paymentDeferral;
	@Schema(description = "مدت اعتبار پیش فاکتور-روز")
	private Integer deadlineDays;
	@Schema(description = "وضعیت در فرایند", name = "proformaIssueType", example = "PENDING")
	private ProformaIssueType proformaIssueType;
	@Schema(description = "وضعیت ابطال پیش فاکتور", name = "proformaReversalStatus", example = "CANCEL")
	private ProformaReversalStatus proformaReversalStatus;
	@Schema(description = "تاریخ سفارش")
	private Date orderDate;
	@Schema(description = "تاریخ قرارداد")
	private String contractDate;


	@Schema(name = "pmsId", description = "شناسه لجستیک", example = "126-11")
	private String pmsId;


	@Schema(description = "تعداد اوراق گام")
	private Integer gamCertificateCount;


	@Schema(description = "مبلغ اضافه شده به مبلغ کل")
	private BigDecimal extraBillOfExchangeAmount;


	@Schema(description = "درصد اضافه شده به مبلغ کل")
	private BigDecimal extraBillOfPercent;

	private List<ProformaGoodItemDto> proformaGoodItemDtos;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PerformaDetailDto.Info")
	public static class Info extends ProformaDetailDto {
		@Serial
		private static final long serialVersionUID = -1559891514322818599L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@ApiModel("PerformaDetailDto.Create")
	public static class Create extends ProformaDetailDto {
		@Serial
		private static final long serialVersionUID = -155989351432281859L;
	}
}