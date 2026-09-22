package com.nicico.internal.sales.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating an accounting detail account for a financial payment instrument")
public class CreateFinancialInstrumentDetailDto implements Serializable {

	@NotNull
	@Schema(description = "Financial instrument type (LETTER_OF_CREDIT, GAM, ELECTRONIC_PROMISSORY_NOTE)", required = true)
	private FinancialInstrumentType instrumentType;

	@Schema(description = "Two-digit user/branch sub-code (e.g. 01, 02)", example = "01", required = true)
	@NotBlank
	private String twoDigitCode;

	@Schema(description = "Optional two-digit Shamsi year (e.g. 03). Defaults to current Shamsi year if blank.", example = "03")
	private String yearSuffix;

	@Schema(description = "Full detail account name", example = "اعتبار اسنادی بانک ملی", required = true)
	private String detailName;

	@Schema(description = "Optional note/description", example = "اعتبار اسنادی شعبه مرکزی")
	private String note;

	@Schema(description = "Optional submission ID for Accounting tracking", example = "123456")
	private String submissionId;
}


