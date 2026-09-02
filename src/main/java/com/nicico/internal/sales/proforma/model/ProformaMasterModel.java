package com.nicico.internal.sales.proforma.model;

import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "T_INS_PERFORMA_MASTER", indexes = {
		@Index(name = "idx_performa_master_comp", columnList = "C_PROFORMA_ISSUE_TYPE, C_WORKFLOW_APPROVE_STATUS, C_PAYMENT_CODE, ID")
})
@ToString(callSuper = true, exclude = {"performaDetailModelLists"})
public class ProformaMasterModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 690077308643995468L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_PERFORMA_MASTER")
	@SequenceGenerator(name = "SEQ_INS_PERFORMA_MASTER", sequenceName = "SEQ_INS_PERFORMA_MASTER", allocationSize = 1)
	private Long id;
	@Schema(description = "کد فرایند", name = "processId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	@Column(name = "C_PROCESS_ID", length = 50, nullable = false)
	private String processId;
	@Schema(description = "کد فرایندابطال", name = "reversalProcessId", example = "5e2b7f8c9d4a6b1e3f7c8d9a0b1e2c3d")
	@Column(name = "C_REVERSAL_PROCESS_ID", length = 50)
	private String reversalProcessId;
	@Schema(description = "شماره قرارداد", name = "contractNo", example = "123456")
	@Column(name = "N_CONTRACT_NO")
	private Long contractNo;
	@Schema(description = "کد پرداخت", name = "paymentCode", example = "1354376")
	@Column(name = "C_PAYMENT_CODE")
	private String paymentCode;
	@Schema(description = "درصد فروش نقدی ", name = "cashPercentage", example = "25")
	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;
	@Schema(description = "درصد کمیسیون", name = "commissionPercentage", example = "10")
	@Column(name = "N_COMMISSION_PERCENTAGE")
	private Double commissionPercentage;
	@Schema(description = "مدت اعتبار", name = "deadlineDays", example = "30")
	@Column(name = "N_DEADLINE_DAYS")
	private Integer deadlineDays;
	@Schema(description = "درصد فروش اعتباری", name = "creditPercentage", example = "15")
	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;
	@Schema(description = "جمع کل مالیات ارزش افزوده", name = "totalVatAmount", example = "1000000")
	@Column(name = "N_TOTAL_VAT_AMOUNT")
	private BigDecimal totalVatAmount;
	@Schema(description = "جمع کل مبلغ نهایی", name = "totalFinalAmount", example = "1000000")
	@Column(name = "N_TOTAL_FINAL_AMOUNT")
	private BigDecimal totalFinalAmount;
	@Schema(description = "جمع کل مبلغ نقدی", name = "totalCashAmount", example = "1000000")
	@Column(name = "N_TOTAL_CASH_AMOUNT")
	private BigDecimal totalCashAmount;
	@Schema(description = "جمع کل مبلغ اعتباری", name = "totalCreditAmount", example = "1000000")
	@Column(name = "N_TOTAL_CREDIT_AMOUNT")
	private BigDecimal totalCreditAmount;

	@Schema(description = "تعداد کل", name = "totalQuantity", example = "1000000")
	@Column(name = "N_TOTAL_QUANTITY")
	private BigDecimal totalQuantity;
	@Schema(description = "کد مشتری", name = "customerId", example = "1")
	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;
	@Schema(description = "نام مشتری", name = "customerName", example = "مهدی حسینی")
	@Column(name = "C_CUSTOMER_NAME")
	private String customerName;
	@Schema(description = "شماره ملی مشتری", name = "customerPhone", example = "02145678910")
	@Column(name = "c_national_code", nullable = false, length = 50)
	private String nationalCode;
	@Schema(description = "شماره تلفن مشتری", name = "customerPhone", example = "02145678910")
	@Column(name = "c_phone")
	private String phone;
	@Schema(description = "کد اقتصادی", name = "economicCode", example = "123456789")
	@Column(name = "C_ECONOMIC_CODE", length = 50)
	private String economicCode;
	@Schema(description = "شماره ثبت", name = "registerNumber", example = "123456789")
	@Column(name = "c_register_number", length = 50)
	private String registerNumber;
	@Schema(description = "کد پستی", name = "postCode", example = "123456789")
	@Column(name = "C_POST_CODE", length = 10)
	private String postCode;
	@Schema(description = "آدرس", name = "address", example = "تهران")
	@Column(name = "c_address", length = 4000)
	private String address;
	@Schema(description = "وضعیت در فرایند", name = "workflowApproveStatus", example = "PENDING")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_WORKFLOW_APPROVE_STATUS", length = 100)
	private WorkflowApproveStatus workflowApproveStatus;
	@Enumerated(EnumType.STRING)
	@Column(name = "C_PROFORMA_ISSUE_TYPE")
	private ProformaIssueType proformaIssueType;

	@Schema(description = "شناسه کالا", name = "goodId")
	@Column(name = "N_GOOD_ID")
	private Long goodId;
	@Schema(description = "نام کالا", name = "goodName")
	@Column(name = "C_GOOD_NAME", length = 100)
	private String goodName;
	@Column(name = "IS_PROCESS_FINAL")
	private Boolean isProcessFinal = false;
	@Column(name = "IS_REVERSAL_PROCESS_FINAL")
	private Boolean isReversalProcessFinal = false;
	@OneToMany(mappedBy = "ProformaMasterModel", fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	private List<ProformaDetailModel> proformaDetailModelLists;

	@OneToMany(mappedBy = "ProformaMasterModel", fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	private List<LcModel> lcModelList;

	@Column(name = "C_CONTRACT_DATE", length = 20)
	private String contractDate;

//	@ManyToOne(fetch = FetchType.LAZY, targetEntity = TradeExtractModel.class)
//	@JoinColumn(name = "F_TRADE_ID", insertable = false, updatable = false)
//	private TradeExtractModel tradeExtractModel;

//
//	@OneToMany(mappedBy = "ProformaMasterModel", fetch = FetchType.LAZY)
//	@EqualsAndHashCode.Exclude
//	private List<ExtraBankBillModel> ExtraBankBillModels;


//
//	@OneToMany(mappedBy = "ProformaMasterModel", fetch = FetchType.LAZY)
//	@EqualsAndHashCode.Exclude
//	private List<ExtraBankBillModel> extraBankBillModel;
//
//	@OneToMany(mappedBy = "proformaMasterModel", fetch = FetchType.LAZY)
//	@EqualsAndHashCode.Exclude
//	private List<ExtraBankBillModel> proformaBankBillModels;

	@Column(name = "F_TRADE_ID", nullable = false)
	private Long tradeId;

	@Column(name = "N_BROKER_ID")
	private Long brokerId;
	@Column(name = "C_BROKER_NAME", length = 100)
	private String brokerName;
	@Column(name = "C_BROKER_NATIONAL_CODE", length = 100)
	private String brokerNationalCode;

	@Column(name = "C_OFFER_DESCRIPTION", length = 4000)
	private String offerDescription;

	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

	//    @Enumerated(EnumType.STRING)
	@Column(name = "C_SETTLEMENT_TYPE", length = 50)
	private String settlementType = SettlementType.UNKNOWN.name();

}
