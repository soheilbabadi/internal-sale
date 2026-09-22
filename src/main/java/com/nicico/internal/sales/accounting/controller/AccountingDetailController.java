package com.nicico.internal.sales.accounting.controller;

import com.fgostar.accounting.sdk.dto.DetailDto;
import com.fgostar.accounting.sdk.dto.EOperator;
import com.nicico.internal.sales.accounting.dto.*;
import com.nicico.internal.sales.accounting.service.AccountingDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/accounting/detail")
@Tag(name = "حسابداری تفصیلی", description = "مدیریت و استعلام کدهای تفصیلی در سامانه حسابداری")
public class AccountingDetailController {

	private final AccountingDetailService accountingDetailService;

	@Operation(summary = "استعلام و دریافت حساب تفصیلی بر اساس کدملی شخص یا شناسه ملی شرکت")
	@GetMapping("/by-national-code/{nationalCodeOrId}")
	public ResponseEntity<DetailDto> findDetailOfNationalCode(@PathVariable String nationalCodeOrId) {
		log.info("REST request to find accounting detail for national identifier: {}", nationalCodeOrId);
		return accountingDetailService.findDetailOfNationalCode(nationalCodeOrId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "استعلام تفصیلی شخص حقیقی بر اساس کد ملی (پیشوند 02/)")
	@GetMapping("/person/{nationalCode}")
	public ResponseEntity<DetailDto> findDetailForPerson(@PathVariable String nationalCode) {
		log.info("REST request to find person accounting detail for national code: {}", nationalCode);
		return accountingDetailService.findDetailForPerson(nationalCode)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "استعلام تفصیلی شخص حقوقی / شرکت بر اساس شناسه ملی (پیشوند 01/)")
	@GetMapping("/company/{nationalId}")
	public ResponseEntity<DetailDto> findDetailForCompany(@PathVariable String nationalId) {
		log.info("REST request to find company accounting detail for national ID: {}", nationalId);
		return accountingDetailService.findDetailForCompany(nationalId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "جستجوی حساب‌های تفصیلی بر اساس کد یا پیشوند")
	@GetMapping("/search")
	public ResponseEntity<List<DetailDto>> searchDetailsByCode(@RequestParam("code") String detailCode) {
		log.info("REST request to search accounting details by code: {}", detailCode);
		List<DetailDto> details = accountingDetailService.searchDetailsByCode(detailCode, EOperator.equals);
		return ResponseEntity.ok(details);
	}

	@Operation(summary = "تطبیق و یافتن والد مناسب برای کد تفصیلی هدف")
	@GetMapping("/resolve-parent")
	public ResponseEntity<DetailDto> resolveParent(@RequestParam("targetCode") String targetCode) {
		log.info("REST request to resolve parent for targetCode: {}", targetCode);
		return accountingDetailService.resolveParentDetail(targetCode)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "دریافت لیست حساب‌های تفصیلی فرزند بر اساس شناسه والد")
	@GetMapping("/children-by-parent/{parentDetailId}")
	public ResponseEntity<List<DetailDto>> getChildrenByParent(
			@PathVariable Long parentDetailId,
			@RequestParam(value = "startRow", required = false, defaultValue = "0") Integer startRow,
			@RequestParam(value = "endRow", required = false, defaultValue = "500") Integer endRow,
			@RequestParam(value = "sortBy", required = false, defaultValue = "-detailName") String sortBy) {
		log.info("REST request to fetch children for parentDetailId: {}, startRow: {}, endRow: {}, sortBy: {}",
				parentDetailId, startRow, endRow, sortBy);
		List<DetailDto> children = accountingDetailService.getChildrenByParentDetailId(parentDetailId, startRow, endRow, sortBy);
		return ResponseEntity.ok(children);
	}

	@Operation(summary = "محاسبه شماره دنباله بعدی تفصیلی بر اساس بزرگترین فرزند موجود والد")
	@GetMapping("/next-sequence")
	public ResponseEntity<String> getNextSequenceCode(@RequestParam("parentCode") String parentCode) {
		log.info("REST request to generate next sequence code for parent: {}", parentCode);
		String nextCode = accountingDetailService.generateNextSequenceCode(parentCode);
		return ResponseEntity.ok(nextCode);
	}

	@Operation(summary = "ایجاد حساب تفصیلی برای شخص حقیقی")
	@PostMapping("/person")
	public ResponseEntity<DetailDto> createDetailForPerson(@RequestBody CreatePersonDetailDto request) {
		log.info("REST request to create person accounting detail for national code: {}", request != null ? request.getNationalCode() : null);
		DetailDto created = accountingDetailService.createDetailForPerson(request);
		return ResponseEntity.ok(created);
	}

	@Operation(summary = "ایجاد حساب تفصیلی برای شخص حقوقی / شرکت")
	@PostMapping("/company")
	public ResponseEntity<DetailDto> createDetailForCompany(@RequestBody CreateCompanyDetailDto request) {
		log.info("REST request to create company accounting detail for national ID: {}", request != null ? request.getNationalId() : null);
		DetailDto created = accountingDetailService.createDetailForCompany(request);
		return ResponseEntity.ok(created);
	}

	@Operation(summary = "محاسبه شماره دنباله بعدی تفصیلی برای ابزار مالی (اعتبار اسنادی، اوراق گام، برات الکترونیکی)")

	@GetMapping("/financial-instrument/next-sequence")
	public ResponseEntity<Long> getFinancialInstrumentNextSequenceCode(
			@RequestParam("instrumentType") FinancialInstrumentType instrumentType,
			@RequestParam("twoDigitCode") String twoDigitCode,
			@RequestParam(value = "yearSuffix", required = false) String yearSuffix) {
		log.info("REST request to generate next sequence code for financial instrument: {}, twoDigit: {}, year: {}",
				instrumentType, twoDigitCode, yearSuffix);
		Long nextCode = accountingDetailService.generateNextFinancialInstrumentSequenceCode(instrumentType, twoDigitCode, yearSuffix);
		return ResponseEntity.ok(nextCode);
	}

//	@Operation(summary = "محاسبه شماره دنباله بعدی و ایجاد حساب تفصیلی ابزار مالی (با ارسال submissionId و تلاش مجدد)")
//	@PostMapping("/financial-instrument/next-sequence")
//	public ResponseEntity<FinancialInstrumentCreateResponseDto> generateAndCreateFinancialInstrumentDetail(
//			@RequestParam("instrumentType") FinancialInstrumentType instrumentType,
//			@RequestParam("twoDigitCode") String twoDigitCode,
//			@RequestParam(value = "yearSuffix", required = false) String yearSuffix,
//			@RequestParam(value = "detailName", required = false) String detailName,
//			@RequestParam(value = "submissionId", required = false) String submissionId) {
//		log.info("REST request to generate next sequence and create financial instrument detail: {}, twoDigit: {}, year: {}, submissionId: {}",
//				instrumentType, twoDigitCode, yearSuffix, submissionId);
//		FinancialInstrumentCreateResponseDto response =
//				accountingDetailService.generateAndCreateFinancialInstrumentDetail(
//						instrumentType, twoDigitCode, yearSuffix, detailName, submissionId);
//		return ResponseEntity.ok(response);
//	}


	@Operation(summary = "محاسبه شماره دنباله بعدی و ایجاد حساب تفصیلی ابزار مالی (با ارسال submissionId و تلاش مجدد)")
	@PostMapping("/financial-instrument/next-sequence")
	public ResponseEntity<FinancialInstrumentCreateResponseDto> generateAndCreateFinancialInstrumentDetail(
			@Valid @RequestBody CreateFinancialInstrumentDetailDto request) {
		log.info("REST request to generate next sequence and create financial instrument detail: {}", request);
		FinancialInstrumentCreateResponseDto response =
				accountingDetailService.generateAndCreateFinancialInstrumentDetail(
						request.getInstrumentType(),
						request.getTwoDigitCode(),
						request.getYearSuffix(),
						request.getDetailName(),
						request.getSubmissionId());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "ایجاد حساب تفصیلی برای ابزار مالی (اعتبار اسنادی، اوراق گام، برات الکترونیکی)")
	@PostMapping("/financial-instrument")
	public ResponseEntity<?> createDetailForFinancialInstrument(
			@RequestBody CreateFinancialInstrumentDetailDto request) {
		log.info("REST request to create financial instrument accounting detail: {}", request);
		accountingDetailService.createDetailForFinancialInstrument(request);
		return ResponseEntity.ok().build();
	}

}
