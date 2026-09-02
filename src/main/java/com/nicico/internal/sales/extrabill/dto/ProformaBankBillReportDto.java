package com.nicico.internal.sales.extrabill.dto;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProformaBankBillReportDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -7326805160058556690L;

	private Long id;

	private String issuerBankName;

	private String branchCode;

	private String branchName;

	private String paymentCity;

	private String agentBankName;

	private String nosaCode;

	private String sepamCode;

	private String treasuryId;

	private Date issueDate;

	private Date dueDate;

	private Long contractNo;

	private Long tradeId;

	private Long proformaMasterId;

	private WorkflowApproveStatus billStatus;

	private String processId;

	private Acknowledgment acknowledgment;

	private boolean isReckoningSend;

	private Date reckoningSendDate;

	private String pmsBillId;

	private Date cancelDate;

	private LcCancellationReason cancellationReason;

	private String buyerName;

	private String buyerNationalCode;

	private Long commodityCode;

	private String tradeContractDate;

	private String paymentCode;

	private String customerName;

	private String customerNationalCode;

	private String goodName;

	private BigDecimal totalFinalAmount;

	private BigDecimal totalCashAmount;

	private BigDecimal totalCreditAmount;

	private WorkflowApproveStatus proformaStatus;

	// فیلدهای جدید اضافه شده از پیش فاکتور
	@Schema(description = "شناسه کارگزار")
	private Long brokerId;

	@Schema(description = "نام کارگزار")
	private String brokerName;

	@Schema(description = "کد ملی کارگزار")
	private String brokerNationalCode;

	@Schema(description = "مقدار کل")
	private BigDecimal totalQuantity;

	@Schema(description = "متن آگهی عرضه")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس")
	private String imeCommoditySymbol;

	@Schema(description = "شناسه کالا")
	private Long goodId;

	@Schema(description = "شماره پیش فاکتور")
	private String performaNo;

	@Schema(description = "تاریخ پیش فاکتور")
	private Date performaDate;


	@Schema(description = "شناسه فایل پیوست برات")
	private String extraBillFileId;

	@Schema(description = "شناسه فایل اصلاحیه")
	private String dispatchAttachmentId;


	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaBankBillReportDto.Info")
	public static class Info extends ProformaBankBillReportDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921076L;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("ProformaBankBillReportDto.Create")
	public static class Create extends ProformaBankBillReportDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921077L;
	}
}
