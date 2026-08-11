package com.nicico.internal.sales.remittance.model;


import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.remittance.enums.IssueSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "t_ins_remittance_master")
public class RemittanceMasterModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 788276480203056653L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ins_remittance")
	@SequenceGenerator(name = "seq_ins_remittance", sequenceName = "seq_ins_remittance", allocationSize = 1)
	@Column(name = "ID")
	private Long id;

	@Schema(description = "شماره حواله", name = "remittanceNumber", example = "RM123456789")
	@Column(name = "C_REMITTANCE_NO", nullable = false)
	private String remittanceNumber;
	@Schema(description = "تاریخ حواله", name = "remittanceDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_REMITTANCE_DATE", nullable = false)
	private Date remittanceDate;
	@Schema(description = "تاریخ اعتبار", name = "validityDate", example = "2023-12-31T00:00:00Z")
	@Column(name = "D_VALIDITY_DATE", nullable = false)
	private Date validityDate;
	@Schema(description = "شناسه مشتری", name = "customerId", example = "12345")
	@Column(name = "N_CUSTOMER_ID", nullable = false)
	private long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "شرکت نمونه")
	@Column(name = "C_CUSTOMER_NAME", length = 200, nullable = false)
	private String customerName;
	@Schema(description = "کد اقتصادی", name = "economicCode", example = "1234567890")
	@Column(name = "C_ECONOMIC_CODE", length = 50, nullable = false)
	private String economicCode;
	@Schema(description = "شناسه ملی", name = "nationalCode", example = "0012345678")
	@Column(name = "C_NATIONAL_CODE", length = 15, nullable = false)
	private String nationalCode;
	@Schema(description = "آدرس مقصد", name = "targetAddress", example = "تهران، خیابان نمونه، پلاک ۱۲۳")
	@Column(name = "C_TARGET_ADDRESS", length = 500, nullable = false)
	private String targetAddress;
	@Schema(description = "شناسه محل بارگیری", name = "loadingPortId", example = "67890")
	@Column(name = "N_LOADING_PORT_ID", length = 50, nullable = false)
	private Long loadingPortId;
	@Column(name = "C_LOADING_PORT", nullable = false)
	private String loadingPort;
	@Column(name = "N_GOOD_ID", nullable = false)
	private Long goodId;
	@Column(name = "C_GOOD_NAME", length = 200, nullable = false)
	private String goodName;
	@Column(name = "C_PACKING_NAME", length = 100)
	private String packingName;
	@Column(name = "N_PACKING_ID")
	private Integer packingId;
	@Column(name = "C_LOT_NUMBER")
	private String lotNumber;
	@Column(name = "D_CONTRACT_DATE", nullable = false)
	private Date contractDate;
	@Column(name = "C_CONTRACT_NO", length = 50, nullable = false)
	private String contractNo;
	@Column(name = "C_SELLER_BROKER_NAME", length = 200, nullable = false)
	private String sellerBrokerName;
	@Column(name = "C_BUYER_BROKER_NAME", length = 200, nullable = false)
	private String buyerBrokerName;
	@Column(name = "N_CASH_PERCENTAGE", nullable = false)
	private BigDecimal cashPercentage;
	@Column(name = "N_CREDIT_PERCENTAGE", nullable = false)
	private BigDecimal creditPercentage;
	@Column(name = "N_REMITTANCE_QUANTITY", nullable = false)
	private BigDecimal remittanceQuantity;
	@Column(name = "N_REMITTANCE_QUANTITY_CASH", nullable = false)
	private BigDecimal remittanceQuantityCash;
	@Column(name = "N_REMITTANCE_QUANTITY_CREDIT", nullable = false)
	private BigDecimal remittanceQuantityCredit;
	@Column(name = "N_REMITTANCE_UNIT_PRICE_CASH", nullable = false)
	private BigDecimal remittanceUnitPriceCash;
	@Column(name = "N_REMITTANCE_UNIT_PRICE_CREDIT", nullable = false)
	private BigDecimal remittanceUnitPriceCredit;
	@Column(name = "N_TAX_AMOUNT", nullable = false)
	private BigDecimal taxAmount;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_LC_EXPIRY_DATE")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	@Column(name = "D_SETTLEMENT_DUE_DATE")
	private Date settlementDueDate = new Date();
	@Schema(name = "شناسه بانک")
	@Column(name = "N_TRADING_BANK_ID")
	private Long tradingBankId;
	@Schema(name = "نام بانک")
	@Column(name = "C_TRADING_BANK_TITLE", length = 100)
	private String tradingBankTitle;
	@Schema(name = "عنوان شعبه")
	@Column(name = "C_TRADING_BRANCH_TITLE", length = 100)
	private String tradingBankBranchTitle;
	@Column(name = "N_ISSUER_BANK_ID")
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;
	@Column(name = "C_ISSUER_BANK_NAME", length = 200)
	@Schema(name = "نام بانک بانک گشایش اعتبار اسنادی")
	private String issuerBankName;

	@Column(name = "C_ISSUER_BANK_BRANCH_NAME", length = 200)
	@Schema(name = "نام شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchName;

	@Column(name = "C_ISSUER_BANK_CODE", length = 200)
	@Schema(name = "کد شعبه بانک گشایش اعتبار اسنادی")
	private String issuerBankBranchCode;

	@Column(name = "C_PROCESS_ID", length = 50, nullable = false)
	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	private String processId;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100, nullable = false)
	private WorkflowApproveStatus workflowApproveStatus;

	@Column(name = "N_PROFORMA_MASTER_ID")
	private Long proformaMasterId;

	@Column(name = "N_PROFORMA_DETAIL_ID")
	private Long proformaDetailId;

	@Column(name = "N_LC_ID")
	private Long lcId;
	@Schema(description = "شماره اعتبار اسنادی", name = "lcNo", example = "LC123456789")
	@Column(name = "C_LC_NO")
	private String lcNo;

	@Column(name = "N_ISSUE_PLACE_ID", length = 50)
	private Long issuePlaceId;
	@Column(name = "C_ISSUE_PLACE", length = 50)
	private String issuePlace;

	@Schema(name = "proformaNo", description = "شماره پیش فاکتور")
	@Column(name = "C_PROFORMA_NO", length = 50)
	private String proformaNo;

	@Column(name = "D_PROFORMA_DATE")
	private Date proformaDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_PROFORMA_ISSUE_TYPE")
	private ProformaIssueType proformaIssueType;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_ISSUE_SOURCE_TYPE", length = 50, nullable = false)
	private IssueSourceType issueSourceType;

	@Column(name = "C_ISSUER_NAME", length = 100, nullable = false)
	private String issuerName;

	@Column(name = "N_ISSUER_ID", nullable = false)
	private Long issuerId;

	@Column(name = "IS_PROCESS_FINAL")
	private boolean isProcessFinal;

	@Column(name = "C_PAYMENT_CODE", length = 100, nullable = false)
	private String paymentCode;

	@Column(name = "N_TRADE_ID", nullable = false)
	private Long tradeId;

	@Column(name = "IS_DELAY_PENALTY", nullable = false)
	private boolean isDelayPenalty;

	@Column(name = "C_PMS_ID", length = 50)
	private String pmsId;

	@Schema(name = "settlementType", description = "شناسه نوع تسویه")
	@Column(name = "C_SETTLEMENT_TYPE", length = 100)
	private String settlementType;

	@Schema(name = "settlementTypeDesc", description = "نوع تسویه")
	@Column(name = "C_SETTLEMENT_TYPE_DESC", length = 100)
	private String settlementTypeDesc;

	@Column(name = "N_BROKER_ID")
	private Long brokerId;

	@Column(name = "C_BROKER_NAME", length = 100)
	private String brokerName;

	@Column(name = "C_BROKER_NATIONAL_CODE", length = 100)
	private String brokerNationalCode;

	@Schema(description = "مقدار کل")
	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;

	@Schema(description = "قیمت نهایی")
	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;

	@Column(name = "C_OFFER_DESCRIPTION", length = 4000)
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

//    @OneToMany(mappedBy = "remittanceMaster", fetch = FetchType.LAZY, targetEntity = BillingMasterModel.class)
//    private List<BillingMasterModel> billingMasterModels;

	@OneToMany(mappedBy = "remittanceMasterModel", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = RemittanceGoodItemModel.class)
	private List<RemittanceGoodItemModel> remittanceGoodItemModels;

}
