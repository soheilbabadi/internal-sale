package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.enums.QCAnalysisStage;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class QCAnalysisMasterDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

	@Schema(description = "مرحله آنالیز (قبل عرضه، بعد عرضه، اصلاحی)")
	private QCAnalysisStage analysisStage;

	@Schema(description = "شناسه کالا")
	private Long commodityId;

	@Schema(description = "کد یا نماد بورسی کالا")
	private String commodityCode;

	@Schema(description = "نام فارسی کالا")
	private String commodityName;

	@Schema(description = "شماره اندیکاتور نامه")
	private String qcIndicatorNo;

	@Schema(description = "شناسه نامه والد")
	private Long parentAnalysisId;

	@Schema(description = "شماره اندیکاتور نامه والد")
	private String refIndicatorNo;

	@Schema(description = "تاریخ ثبت نامه")
	private Date registerDate;

	@Schema(description = "تاریخ ثبت نامه (شمسی)")
	private String persianRegisterDate;

	@Schema(description = "تاریخ عرضه")
	private Date offerDate;

	@Schema(description = "تاریخ عرضه (شمسی)")
	private String persianOfferDate;

	@Schema(description = "شناسه محل بارگیری")
	private Long loadingPlaceId;

	@Schema(description = "عنوان محل بارگیری")
	private String loadingPlace;

	@Schema(description = "وضعیت نامه")
	private QCAnalysisStatus status;

	@Schema(description = "توضیحات نامه")
	private String description;

	@Schema(description = "آیتم‌ها و ردیف‌های عیار و بشکه")
	private List<QCAnalysisItemDto> items = new ArrayList<>();

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ApiModel("QCAnalysisMasterDto.Info")
	public static class Info extends QCAnalysisMasterDto {
		@Serial
		private static final long serialVersionUID = 1L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ApiModel("QCAnalysisMasterDto.SaveRequest")
	public static class SaveRequest implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		private Long id;

		@NotNull(message = "مرحله آنالیز الزامی است")
		private QCAnalysisStage analysisStage;

		@NotNull(message = "شناسه کالا الزامی است")
		private Long commodityId;

		private String commodityCode;
		private String commodityName;

		@NotBlank(message = "شماره اندیکاتور نامه کنترل کیفی الزامی است")
		private String qcIndicatorNo;

		private Long parentAnalysisId;
		private String refIndicatorNo;

		@NotNull(message = "تاریخ ثبت نامه الزامی است")
		private Date registerDate;

		@NotBlank(message = "تاریخ شمسی ثبت نامه الزامی است")
		private String persianRegisterDate;

		@NotNull(message = "تاریخ عرضه در بورس الزامی است")
		private Date offerDate;

		@NotBlank(message = "تاریخ شمسی عرضه الزامی است")
		private String persianOfferDate;

		private Long loadingPlaceId;
		private String loadingPlace;

		private QCAnalysisStatus status = QCAnalysisStatus.CONFIRMED;

		private String description;

		@Valid
		private List<QCAnalysisItemDto> items = new ArrayList<>();
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ApiModel("QCAnalysisMasterDto.ParentOption")
	public static class ParentOption implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		private Long id;
		private String qcIndicatorNo;
		private String persianRegisterDate;
		private String persianOfferDate;
		private Long commodityId;
		private String commodityName;
		private QCAnalysisStage analysisStage;
	}
}
