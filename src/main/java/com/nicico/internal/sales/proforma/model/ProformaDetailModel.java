package com.nicico.internal.sales.proforma.model;

import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.SaleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
@Builder
@Table(name = "T_INS_PERFORMA_DETAIL", indexes = {
		@Index(name = "idx_performa_detail_fk", columnList = "F_PERFORMA_MASTER_ID, ID")
})
@ToString(callSuper = true, exclude = {"performaGoodItemModels"})
public class ProformaDetailModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -8232856503968765193L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_PERFORMA_DETAIL")
	@SequenceGenerator(name = "SEQ_INS_PERFORMA_DETAIL", sequenceName = "SEQ_INS_PERFORMA_DETAIL", allocationSize = 1)
	private long id;
	@Column(name = "N_JALAALI_YEAR")
	private int jalaaliYear;
	@Column(name = "N_FINAL_PRICE")
	private BigDecimal finalPrice;
	@Schema(description = "شماره پیش فاکتور")
	@Column(name = "C_PERFORMA_NO")
	private String performaNo;
	@Schema(description = "تاریخ پیش فاکتور")
	@Column(name = "D_PERFORMA_DATE")
	private Date performaDate;
	@Schema(description = "مبنای فروش")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_SALE_TYPE", length = 50)
	private SaleType saleType;
	@Schema(description = "نحوه تسویه")
//    @Enumerated(EnumType.STRING)
	@Column(name = "C_SETTLEMENT_TYPE", length = 50)
	private String settlementType;
	@Column(name = "C_PMS_ID", length = 50)
	private String pmsId;
	@Column(name = "N_VAT_AMOUNT")
	private BigDecimal vatAmount;
	@Column(name = "N_TOTAL_AMOUNT")
	private BigDecimal totalAmount;
	@Schema(description = "مهلت بارگیری پس از مهلت مجاز بورس کالا-روز")
	@Column(name = "N_STORAGE_DEADLINE", nullable = false)
	private Integer storageDeadline;
	@Schema(description = "هزینه انبارداری روزشمار-درصد")
	@Column(name = "N_STORAGE_COST", nullable = false, precision = 10, scale = 2)
	private BigDecimal storageCost;
	@Schema(description = "سررسید انقضای اعتبار-ماه")
	@Column(name = "N_CREDIT_EXPIRE_PERIOD", nullable = false)
	private Integer creditExpirePeriod;
	@Schema(description = "مهلت بارگیری-روز")
	@Column(name = "N_SHIPPING_DEADLINE", nullable = false)
	private Integer shippingDeadline;
	@Schema(description = "مهلت پرداخت وجه اعتبار اسنادی-روز")
	@Column(name = "N_PAYMENT_DEFERRAL", nullable = false)
	private Integer paymentDeferral;
	@Schema(description = "مدت اعتبار پیش فاکتور-روز")
	@Column(name = "N_DEADLINE_DAYS", nullable = false)
	private Integer deadlineDays;
	@Schema(description = "وضعیت در فرایند", name = "proformaIssueType", example = "PENDING")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_PROFORMA_ISSUE_TYPE")
	private ProformaIssueType proformaIssueType;
	@Schema(description = "وضعیت ابطال پیش فاکتور", name = "proformaReversalStatus", example = "CANCEL")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_PROFORMA_REVERSAL_STATUS")
	private ProformaReversalStatus proformaReversalStatus;
	@Schema(description = "تاریخ سفارش")
	@Column(name = "D_ORDER_DATE")
	private Date orderDate;
	@Schema(description = "تاریخ قرارداد")
	@Column(name = "C_CONTRACT_DATE", length = 20)
	private String contractDate;

	@Schema(description = "تعداد اوراق گام")
	@Column(name = "N_GAM_CERTIFICATE_COUNT", nullable = false)
	@Builder.Default
	private Integer gamCertificateCount = 0;


	@Schema(description = "مبلغ اضافه شده به مبلغ کل")
	@Column(name = "N_EXTRA_BILL_OF_AMOUNT", nullable = false, precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal extraBillOfExchangeAmount = BigDecimal.ZERO;

	@Schema(description = "درصد اضافه شده به مبلغ کل")
	@Column(name = "N_EXTRA_BILL_OF_PERCENT", nullable = false, precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal extraBillOfPercent = BigDecimal.ZERO;

	@Schema(description = "لیست کالاها")
	@OneToMany(mappedBy = "proformaDetailModel", fetch = FetchType.EAGER)
	@EqualsAndHashCode.Exclude
	private List<ProformaGoodItemModel> proformaGoodItemModels;

	@Schema(description = "شناسه قرارداد")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "F_PERFORMA_MASTER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
	private ProformaMasterModel ProformaMasterModel;
	@Column(name = "F_PERFORMA_MASTER_ID")
	private Long proformaMasterId;


}