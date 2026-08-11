package com.nicico.internal.sales.bill.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.remittance.enums.IssueSourceType;
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

//خواندن اطلاعات حواله قبل از صورتحساب
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BillingTradeExtractDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -8364682081445078704L;

	private Long id;

	private String description;

	private String remittanceNo;

	private Date remittanceDate;

	private Date validityDate;

	private BigDecimal customerId;

	private String customerName;

	private String economicCode;

	private String nationalCode;

	private String targetAddress;

	private Long loadingPortId;

	private String loadingPort;

	private Long goodId;

	private String goodName;

	private String packingName;

	private Long packingId;

	private String lotNumber;

	private Date contractDate;

	private String contractNo;

	private String sellerBrokerName;

	private String buyerBrokerName;

	private Double cashPercentage;

	private Double creditPercentage;

	private BigDecimal remittanceQuantity;

	private BigDecimal remittanceQuantityCash;

	private BigDecimal remittanceQuantityCredit;

	private BigDecimal remittanceUnitPriceCash;

	private BigDecimal remittanceUnitPriceCredit;

	private Date lcExpiryDate;

	private Date settlementDueDate;

	private Long issuerBankId;

	private String issuerBankName;

	private String issuerBankBranchName;

	private String issuerBankCode;

	private String lcNo;

	private Long issuePlaceId;

	private String issuePlace;

	private String proformaNo;

	private Date proformaDate;

	private ProformaIssueType proformaIssueType;

	private IssueSourceType issueSourceType;

	private String issuerName;

	private String paymentCode;

	private Integer isDelayPenalty;

	private Long tradingBankId;

	private String tradingBankTitle;

	private String tradingBranchTitle;

	@Schema(name = "pmsId", description = "شناسه لجستیک", example = "126-11")
	private String pmsId;

	private BigDecimal taxAmount;

	@Schema(name = "settlementType", description = "نوع تسویه")
	private String settlementType;

	private String settlementTypeDesc;

	private Long brokerId;

	@Schema(description = "نام کارگزار", name = "brokerName", example = "شرکت فاسابا")
	private String brokerName;

	private BigDecimal totalQuantity;

	private BigDecimal totalFinalAmount;

	@Schema(description = "متن آگهی عرضه", name = "offerDescription", example = "کیفیت عالی")
	private String offerDescription;

	@Schema(description = "نماد کالا در بورس", name = "imeCommoditySymbol", example = "OIL")
	private String imeCommoditySymbol;

	private BigDecimal jalaliYear;

	private BigDecimal emissionTax;

	private Integer storageDeadline;

	private BigDecimal storageCost;

	private Integer creditExpirePeriod;

	private Integer shippingDeadline;

	private Integer paymentDeferral;

	private String customerNosaCode;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("BillingTradeExtractDto.Create")
	@NoArgsConstructor
	public static class Create extends BillingTradeExtractDto {
		@Serial
		private static final long serialVersionUID = -7026713351296540537L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("BillingTradeExtractDto.Info")
	@NoArgsConstructor
	public static class Info extends BillingTradeExtractDto {
		@Serial
		private static final long serialVersionUID = 8641387470770159481L;
	}
}