package com.nicico.internal.sales.pricing.model;

import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;

@Audited
@Schema(description = "ردیف‌ها و جزئیات عیار و اوزان آنالیز آزمایشگاه (بشکه/لات)")
@Entity
@Table(name = "T_INS_QC_ANALYSIS_ITEM")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"analysisMaster"})
@ToString(exclude = {"analysisMaster"})
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class QCAnalysisItemModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_QC_ANALYSIS_ITEM")
	@SequenceGenerator(name = "SEQ_QC_ANALYSIS_ITEM", sequenceName = "SEQ_QC_ANALYSIS_ITEM", allocationSize = 1)
	private Long id;

	@Schema(description = "شناسه نامه مستر")
	@Column(name = "F_ANALYSIS_MASTER_ID", nullable = false)
	private Long analysisMasterId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "F_ANALYSIS_MASTER_ID", insertable = false, updatable = false)
	private QCAnalysisMasterModel analysisMaster;

	@Schema(description = "شماره ردیف")
	@Column(name = "N_ROW_NO", nullable = false)
	private Integer rowNo;

	@Schema(description = "شماره اولین بشکه / شماره تک بشکه")
	@Column(name = "N_FROM_BARREL_NO")
	private Long fromBarrelNo;

	@Schema(description = "شماره آخرین بشکه (در صورت بازه بودن؛ برای تک بشکه null)")
	@Column(name = "N_TO_BARREL_NO")
	private Long toBarrelNo;


	@Schema(description = "شناسه لات")
	@Column(name = "C_LOT_NO", length = 100)
	private String lotNo;

	@Schema(description = "تعداد کل بشکه‌ها")
	@Column(name = "N_TOTAL_BARRELS_COUNT")
	private Integer totalBarrelsCount;

	@Schema(description = "محل بارگیری این پارت (در صورت تفاوت در سطح ردیف)")
	@Column(name = "F_LOADING_PLACE_ID")
	private Long loadingPlaceId;

	@Schema(description = "عنوان محل بارگیری ردیف")
	@Column(name = "C_LOADING_PLACE", length = 200)
	private String loadingPlace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "F_LOADING_PLACE_ID", insertable = false, updatable = false)
	private LoadingPlaceModel loadingPlaceModel;

	// ==========================================
	// مقادیر وزنی و پایه قیمت
	// ==========================================

	@Schema(description = "وزن ناخالص مرطوب (کیلوگرم)")
	@Column(name = "N_GROSS_WET_WEIGHT", precision = 19, scale = 3)
	private BigDecimal grossWetWeight;

	@Schema(description = "وزن خالص خشک (کیلوگرم)")
	@Column(name = "N_NET_DRY_WEIGHT", precision = 19, scale = 3)
	private BigDecimal netDryWeight;

	@Schema(description = "وزن عرضه لات در بورس کالا (تن)")
	@Column(name = "N_OFFER_LOT_WEIGHT_MT", precision = 19, scale = 3)
	private BigDecimal offerLotWeightMt;

	@Schema(description = "قیمت واحد بشکه در هر کیلوگرم (ریال)")
	@Column(name = "N_PRICE_PER_KG_BARREL", precision = 19, scale = 2)
	private BigDecimal pricePerKgBarrel;

	// ==========================================
	// عیارهای مرحله جاری (Current Stage Assays)
	// ==========================================

	@Schema(description = "درصد رطوبت (H2O %)")
	@Column(name = "N_MOISTURE_PCT", precision = 10, scale = 4)
	private BigDecimal moisturePct;

	@Schema(description = "درصد نقره (Ag %)")
	@Column(name = "N_AG_PCT", precision = 10, scale = 4)
	private BigDecimal agPct;

	@Schema(description = "نقره محتوی (کیلوگرم)")
	@Column(name = "N_AG_KG", precision = 19, scale = 3)
	private BigDecimal agKg;

	@Schema(description = "عیار نقره (ppm)")
	@Column(name = "N_AG_PPM", precision = 10, scale = 4)
	private BigDecimal agPpm;

	@Schema(description = "درصد طلا (Au %)")
	@Column(name = "N_AU_PCT", precision = 10, scale = 4)
	private BigDecimal auPct;

	@Schema(description = "طلای محتوی (کیلوگرم)")
	@Column(name = "N_AU_KG", precision = 19, scale = 3)
	private BigDecimal auKg;

	@Schema(description = "عیار طلا (ppm)")
	@Column(name = "N_AU_PPM", precision = 10, scale = 4)
	private BigDecimal auPpm;

	@Schema(description = "درصد سلنیوم (Se %)")
	@Column(name = "N_SE_PCT", precision = 10, scale = 4)
	private BigDecimal sePct;

	@Schema(description = "سلنیوم محتوی (کیلوگرم)")
	@Column(name = "N_SE_KG", precision = 19, scale = 3)
	private BigDecimal seKg;

	@Schema(description = "درصد مس (Cu %)")
	@Column(name = "N_CU_PCT", precision = 10, scale = 4)
	private BigDecimal cuPct;

	@Schema(description = "مس محتوی (کیلوگرم)")
	@Column(name = "N_CU_KG", precision = 19, scale = 3)
	private BigDecimal cuKg;

	@Schema(description = "پلاتین (Pt ppm)")
	@Column(name = "N_PT_PPM", precision = 10, scale = 4)
	private BigDecimal ptPpm;

	@Schema(description = "پالادیوم (Pd ppm)")
	@Column(name = "N_PD_PPM", precision = 10, scale = 4)
	private BigDecimal pdPpm;

	@Schema(description = "درصد مولیبدن (Mo %)")
	@Column(name = "N_MO_PCT", precision = 10, scale = 4)
	private BigDecimal moPct;

	@Schema(description = "درصد آهن (Fe %)")
	@Column(name = "N_FE_PCT", precision = 10, scale = 4)
	private BigDecimal fePct;

	@Schema(description = "درصد گوگرد (S %)")
	@Column(name = "N_S_PCT", precision = 10, scale = 4)
	private BigDecimal sPct;

	@Schema(description = "درصد سرب (Pb %)")
	@Column(name = "N_PB_PCT", precision = 10, scale = 4)
	private BigDecimal pbPct;

	@Schema(description = "درصد روغن / چربی (Oil %)")
	@Column(name = "N_OIL_PCT", precision = 10, scale = 4)
	private BigDecimal oilPct;

	// ==========================================
	// فیلدهای محاسباتی و هزینه‌ای کنسانتره مس و سولفور
	// ==========================================

	@Schema(description = "عیار مس محاسبه‌شده (%)")
	@Column(name = "N_CU_CALC_PCT", precision = 10, scale = 4)
	private BigDecimal cuCalcPct;

	@Schema(description = "عیار طلای محاسبه‌شده (ppm)")
	@Column(name = "N_AU_CALC_PPM", precision = 10, scale = 4)
	private BigDecimal auCalcPpm;

	@Schema(description = "عیار نقره محاسبه‌شده (ppm)")
	@Column(name = "N_AG_CALC_PPM", precision = 10, scale = 4)
	private BigDecimal agCalcPpm;

	@Schema(description = "هزینه ذوب (Treatment Charge)")
	@Column(name = "N_SMELTING_COST", precision = 19, scale = 2)
	private BigDecimal smeltingCost;

	@Schema(description = "هزینه تصفیه (Refining Charge)")
	@Column(name = "N_REFINING_COST", precision = 19, scale = 2)
	private BigDecimal refiningCost;

	@Schema(description = "قیمت کاتد")
	@Column(name = "N_CATHODE_PRICE", precision = 19, scale = 2)
	private BigDecimal cathodePrice;

	@Schema(description = "قیمت اکسید مولیبدن مبنا")
	@Column(name = "N_OXIDE_PRICE", precision = 19, scale = 2)
	private BigDecimal oxidePrice;

	@Schema(description = "کسورات وزنی/کیفی")
	@Column(name = "N_FRACTION_DEDUCTION", precision = 10, scale = 4)
	private BigDecimal fractionDeduction;

	// ==========================================
	// ستون‌های میانگین (Averaged Assays - Post Offer / Correction)
	// ==========================================

	@Schema(description = "درصد رطوبت میانگین (%)")
	@Column(name = "N_AVG_MOISTURE_PCT", precision = 10, scale = 4)
	private BigDecimal avgMoisturePct;

	@Schema(description = "درصد نقره میانگین (%)")
	@Column(name = "N_AVG_AG_PCT", precision = 10, scale = 4)
	private BigDecimal avgAgPct;

	@Schema(description = "نقره محتوی میانگین (کیلوگرم)")
	@Column(name = "N_AVG_AG_KG", precision = 19, scale = 3)
	private BigDecimal avgAgKg;

	@Schema(description = "عیار نقره میانگین (ppm)")
	@Column(name = "N_AVG_AG_PPM", precision = 10, scale = 4)
	private BigDecimal avgAgPpm;

	@Schema(description = "درصد طلای میانگین (%)")
	@Column(name = "N_AVG_AU_PCT", precision = 10, scale = 4)
	private BigDecimal avgAuPct;

	@Schema(description = "طلای محتوی میانگین (کیلوگرم)")
	@Column(name = "N_AVG_AU_KG", precision = 19, scale = 3)
	private BigDecimal avgAuKg;

	@Schema(description = "عیار طلای میانگین (ppm)")
	@Column(name = "N_AVG_AU_PPM", precision = 10, scale = 4)
	private BigDecimal avgAuPpm;

	@Schema(description = "درصد سلنیوم میانگین (%)")
	@Column(name = "N_AVG_SE_PCT", precision = 10, scale = 4)
	private BigDecimal avgSePct;

	@Schema(description = "سلنیوم محتوی میانگین (کیلوگرم)")
	@Column(name = "N_AVG_SE_KG", precision = 19, scale = 3)
	private BigDecimal avgSeKg;

	@Schema(description = "درصد مس میانگین (%)")
	@Column(name = "N_AVG_CU_PCT", precision = 10, scale = 4)
	private BigDecimal avgCuPct;

	@Schema(description = "مس محتوی میانگین (کیلوگرم)")
	@Column(name = "N_AVG_CU_KG", precision = 19, scale = 3)
	private BigDecimal avgCuKg;

	@Schema(description = "پلاتین میانگین (ppm)")
	@Column(name = "N_AVG_PT_PPM", precision = 10, scale = 4)
	private BigDecimal avgPtPpm;

	@Schema(description = "پالادیوم میانگین (ppm)")
	@Column(name = "N_AVG_PD_PPM", precision = 10, scale = 4)
	private BigDecimal avgPdPpm;

	@Schema(description = "درصد مولیبدن میانگین (%)")
	@Column(name = "N_AVG_MO_PCT", precision = 10, scale = 4)
	private BigDecimal avgMoPct;

	@Schema(description = "درصد آهن میانگین (%)")
	@Column(name = "N_AVG_FE_PCT", precision = 10, scale = 4)
	private BigDecimal avgFePct;

	@Schema(description = "درصد گوگرد میانگین (%)")
	@Column(name = "N_AVG_S_PCT", precision = 10, scale = 4)
	private BigDecimal avgSPct;

	@Schema(description = "درصد سرب میانگین (%)")
	@Column(name = "N_AVG_PB_PCT", precision = 10, scale = 4)
	private BigDecimal avgPbPct;

	@Schema(description = "درصد روغن میانگین (%)")
	@Column(name = "N_AVG_OIL_PCT", precision = 10, scale = 4)
	private BigDecimal avgOilPct;

	@Schema(description = "عیار مس محاسبه‌شده میانگین (%)")
	@Column(name = "N_AVG_CU_CALC_PCT", precision = 10, scale = 4)
	private BigDecimal avgCuCalcPct;

	@Schema(description = "عیار طلای محاسبه‌شده میانگین (ppm)")
	@Column(name = "N_AVG_AU_CALC_PPM", precision = 10, scale = 4)
	private BigDecimal avgAuCalcPpm;

	@Schema(description = "عیار نقره محاسبه‌شده میانگین (ppm)")
	@Column(name = "N_AVG_AG_CALC_PPM", precision = 10, scale = 4)
	private BigDecimal avgAgCalcPpm;

	@Schema(description = "توضیحات و یادداشت ردیف")
	@Column(name = "C_REMARKS", length = 1000)
	private String remarks;
}
