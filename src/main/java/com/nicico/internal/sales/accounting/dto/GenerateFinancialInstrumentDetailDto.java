package com.nicico.internal.sales.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Input of "generate next sequence and create financial instrument detail" (with submissionId for safe retry).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateFinancialInstrumentDetailDto {

	@NotNull
	@Schema(description = "نوع ابزار مالی (اعتبار اسنادی، اوراق گام، برات الکترونیکی)")
	private FinancialInstrumentType instrumentType;

	@NotBlank
	@Schema(description = "کد دو رقمی")
	private String twoDigitCode;

	@Schema(description = "پسوند سال (اختیاری)")
	private String yearSuffix;

	@Schema(description = "نام حساب تفصیلی (اختیاری)")
	private String detailName;

	@Schema(description = "شناسه ارسال برای تلاش مجدد ایمن و جلوگیری از ایجاد تکراری (اختیاری)")
	private String submissionId;
}