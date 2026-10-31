package com.nicico.internal.sales.pricing.model;

import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStage;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Audited
@Schema(description = "مستر نامه‌های آنالیز کنترل کیفی و آزمایشگاه")
@Entity
@Table(name = "T_INS_QC_ANALYSIS_MASTER")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"items", "parentAnalysis", "childAnalyses"})
@ToString(exclude = {"items", "parentAnalysis", "childAnalyses"})
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class QCAnalysisMasterModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_QC_ANALYSIS_MASTER")
	@SequenceGenerator(name = "SEQ_QC_ANALYSIS_MASTER", sequenceName = "SEQ_QC_ANALYSIS_MASTER", allocationSize = 1)
	private Long id;

	@Schema(description = "مرحله آنالیز (قبل عرضه، بعد عرضه، اصلاحی)")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_ANALYSIS_STAGE", length = 30, nullable = false)
	private QCAnalysisStage analysisStage;

	@Schema(description = "شناسه کالا (جوین به جدول کالاها)")
	@Column(name = "N_COMMODITY_ID", nullable = false)
	private Long commodityId;

	@Schema(description = "کد یا نماد بورسی کالا")
	@Column(name = "C_COMMODITY_CODE", length = 50)
	private String commodityCode;

	@Schema(description = "نام فارسی کالا")
	@Column(name = "C_COMMODITY_NAME", length = 200)
	private String commodityName;

	@Schema(description = "شماره اندیکاتور نامه کنترل کیفی")
	@Column(name = "C_QC_INDICATOR_NO", length = 100, nullable = false)
	private String qcIndicatorNo;

	@Schema(description = "شناسه نامه والد (جوین به خود)")
	@Column(name = "F_PARENT_ANALYSIS_ID")
	private Long parentAnalysisId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "F_PARENT_ANALYSIS_ID", insertable = false, updatable = false)
	private QCAnalysisMasterModel parentAnalysis;

	@OneToMany(mappedBy = "parentAnalysis", fetch = FetchType.LAZY)
	private List<QCAnalysisMasterModel> childAnalyses = new ArrayList<>();

	@Schema(description = "شماره اندیکاتور نامه مرجع / والد")
	@Column(name = "C_REF_INDICATOR_NO", length = 100)
	private String refIndicatorNo;

	@Schema(description = "تاریخ ثبت نامه (میلادی)")
	@Temporal(TemporalType.DATE)
	@Column(name = "D_REGISTER_DATE", nullable = false)
	private Date registerDate;

	@Schema(description = "تاریخ ثبت نامه (شمسی)")
	@Column(name = "C_PERSIAN_REGISTER_DATE", length = 20, nullable = false)
	private String persianRegisterDate;

	@Schema(description = "تاریخ عرضه در بورس (میلادی)")
	@Temporal(TemporalType.DATE)
	@Column(name = "D_OFFER_DATE", nullable = false)
	private Date offerDate;

	@Schema(description = "تاریخ عرضه در بورس (شمسی)")
	@Column(name = "C_PERSIAN_OFFER_DATE", length = 20, nullable = false)
	private String persianOfferDate;

	@Schema(description = "شناسه محل بارگیری (جوین به جدول محل‌های بارگیری)")
	@Column(name = "F_LOADING_PLACE_ID")
	private Long loadingPlaceId;

	@Schema(description = "عنوان محل بارگیری")
	@Column(name = "C_LOADING_PLACE", length = 200)
	private String loadingPlace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "F_LOADING_PLACE_ID", insertable = false, updatable = false)
	private LoadingPlaceModel loadingPlaceModel;

	@Schema(description = "وضعیت نامه آنالیز")
	@Enumerated(EnumType.STRING)
	@Column(name = "C_STATUS", length = 30, nullable = false)
	private QCAnalysisStatus status = QCAnalysisStatus.CONFIRMED;

	@OneToMany(mappedBy = "analysisMaster", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<QCAnalysisItemModel> items = new ArrayList<>();
}
