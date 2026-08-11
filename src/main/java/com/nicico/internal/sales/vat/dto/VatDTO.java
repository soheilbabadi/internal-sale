package com.nicico.internal.sales.vat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ضرایب مالیات و ارزش افزوده برای سال مالی شمسی")
public class VatDTO {

	@NotNull(message = "سال شمسی اجباری است")
	@Min(value = 1400, message = "سال شمسی باید 1400 یا بیشتر باشد")
	@Max(value = 1500, message = "سال شمسی باید 1500 یا کمتر باشد")
	@Schema(description = "سال مالی بر اساس تقویم شمسی", example = "1403", required = true)
	private Integer jalaliYear;

	@NotNull(message = "مالیات آلایندگی اجباری است")
	@DecimalMin(value = "0", message = "مالیات آلایندگی باید بین ۰ و ۱۰۰ باشد")
	@DecimalMax(value = "100", message = "مالیات آلایندگی باید بین ۰ و ۱۰۰ باشد")
	@Schema(description = "درصد مالیات آلایندگی (بین ۰ تا ۱۰۰)", example = "5.00")
	private BigDecimal emissionTax;

	@NotNull(message = "ضریب مالیات اجباری است")
	@DecimalMin(value = "0", message = "ضریب مالیات باید بین ۰ و ۱۰۰ باشد")
	@DecimalMax(value = "100", message = "ضریب مالیات باید بین ۰ و ۱۰۰ باشد")
	@Schema(description = "درصد ضریب مالیات (بین ۰ تا ۱۰۰)", example = "9.00")
	private BigDecimal taxCoefficient;

	@NotNull(message = "ضریب مالیات بر ارزش افزوده اجباری است")
	@DecimalMin(value = "0", inclusive = false, message = "ضریب مالیات بر ارزش افزوده باید بزرگتر از ۰ و حداکثر ۱۰۰ باشد")
	@DecimalMax(value = "100", message = "ضریب مالیات بر ارزش افزوده باید بزرگتر از ۰ و حداکثر ۱۰۰ باشد")
	@Schema(description = "درصد ضریب مالیات بر ارزش افزوده (بزرگتر از ۰ تا ۱۰۰)", example = "9.00")
	private BigDecimal vatCoefficient;


	@EqualsAndHashCode(callSuper = true)
	@Data
	@NoArgsConstructor
	@Schema(description = "درخواست ثبت رکورد جدید مالیات بر ارزش افزوده")
	public static class Create extends VatDTO {
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@NoArgsConstructor
	@Schema(description = "اطلاعات کامل رکورد مالیات بر ارزش افزوده شامل فیلدهای حسابرسی")
	public static class Info extends VatDTO {

		private Long id;

		private Date createdDate;

		private Date lastModifiedDate;

		private String createdBy;

		private String lastModifiedBy;

		private String comment;
		private String description;
	}
}