package com.nicico.internal.sales.extrabill.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.*;
import com.nicico.internal.sales.extrabill.service.ExtraBillIssueService;
import com.nicico.internal.sales.extrabill.service.ExtraBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@PreAuthorize("@secUtil.hasAuthority('R_INS_EXTRA_BILL')")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/extra-bill")
public class ExtraBillController {

	private final ExtraBillService service;
	private final ExtraBillIssueService extraBillIssueService;

	@Operation(summary = "جستجوی براتهای قابل صدور", description = "لیست براتها که آماده صدور هستند را بر اساس فیلترهای دریافتی برمی‌گرداند.")
	@PostMapping("/search-issuable")
	public ResponseEntity<SearchDTO.SearchRs<ExtraBillIssueProviderDto.Info>> searchIssuable(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(extraBillIssueService.search(searchRq));
	}

	@Operation(summary = "جستجوی براتها", description = "براتها صادر شده را بر اساس فیلترهای ورودی جستجو کرده و نتیجه را برمی‌گرداند.")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<ProformaBankBillDto.Info>> search(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(service.search(searchRq));
	}

	@Operation(summary = "تاریخچه صدور براتها", description = "گزارش تاریخچه کامل صدور براتها شامل وضعیت‌ها، تاریخ‌ها و جزئیات را برمی‌گرداند.")
	@PostMapping("/search-issue-history")
	public ResponseEntity<SearchDTO.SearchRs<ProformaBankBillReportDto.Info>> searchIssueHistory(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(service.searchReport(searchRq));
	}

	@Operation(summary = "دریافت براتها بر اساس شناسه قرارداد اصلی", description = "تمام براتها مرتبط با یک قرارداد پیش فاکتور اصلی (Master) را بر اساس شناسه آن برمی‌گرداند.")
	@GetMapping("/get-by-master/{masterId}")
	public ResponseEntity<List<ProformaBankBillDto.Info>> getByMasterId(
			@Parameter(description = "شناسه قرارداد پیش فاکتور اصلی", required = true, example = "10")
			@PathVariable Long masterId) {
		return ResponseEntity.ok(service.getByMasterId(masterId));
	}

	@Operation(summary = "ثبت برات  جدید", description = "یک برات  جدید بر اساس اطلاعات دریافتی ایجاد و ذخیره می‌کند. نیاز به مجوز C_INS_EXTRA_BILL دارد.")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
	@PostMapping("/save")
	public ResponseEntity<ProformaBankBillDto.Info> save(@RequestBody ProformaBankBillRequest proformaBankBillRequest) {
		return ResponseEntity.ok(service.save(proformaBankBillRequest));
	}

	/**
	 * بروزرسانی فایل‌های پیوست برات
	 * این متد فقط فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند
	 * نیاز به مجوز C_UPD_EXTRA_BILL دارد
	 */
	@Operation(
			summary = "بروزرسانی فایل‌های پیوست برات",
			description = "فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند. نیاز به مجوز C_UPD_EXTRA_BILL دارد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
	@PutMapping("/update-files")
	@ApiResponse(responseCode = "200", description = "بروزرسانی با موفقیت انجام شد",
			content = @Content(schema = @Schema(implementation = ProformaBankBillDto.Info.class)))
	public ResponseEntity<ProformaBankBillDto.Info> updateBillFiles(
			@RequestBody ProformaBankBillFileUpdateDto updateDto) {

		return ResponseEntity.ok(service.updateBillFiles(updateDto));
	}

	@Operation(
			summary = "ارسال ایمیل تایید تسویه",
			description = "برات مشخص، ایمیل تایید تسویه حساب به کارگزار ارسال می شود. این عملیات پس از اتمام فرآیند تسویه و تایید نهایی انجام می گردد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_SEND_NOTIFICATION')")
	@PostMapping("/send-reckoning-extra-bill/{extraBillId}")
	public ResponseEntity<Void> sendReckoningEmail(@PathVariable Long extraBillId) {
		service.sendReckoningEmail(extraBillId);
		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "بروزرسانی اطلاعات برات",
			description = "اطلاعات بانکی و الکترونیکی برات را بروزرسانی می‌کند. نیاز به مجوز C_UPD_EXTRA_BILL دارد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
	@PutMapping("/update")
	@ApiResponse(responseCode = "200", description = "بروزرسانی با موفقیت انجام شد",
			content = @Content(schema = @Schema(implementation = ProformaBankBillDto.Info.class)))
	public ResponseEntity<ProformaBankBillDto.Info> updateExtraBill(
			@RequestBody UpdateExtraBillRequest updateExtraBillRequest) {
		return ResponseEntity.ok(service.updateExtraBill(updateExtraBillRequest));
	}

	@Operation(
			summary = "دریافت تاریخچه تغییرات برات",
			description = "لیست کامل تغییرات و نسخه‌های مختلف یک برات را بر اساس شناسه آن برمی‌گرداند. شامل اطلاعات ایجاد، ویرایش و وضعیت‌های مختلف برات در طول زمان."
	)

	@GetMapping("/audit-history/{extraBillId}")
	@ApiResponse(responseCode = "200", description = "تاریخچه با موفقیت دریافت شد",
			content = @Content(schema = @Schema(implementation = ProformaBankBillAuditDto.class)))
	public ResponseEntity<List<ProformaBankBillAuditDto>> getAuditHistory(
			@Parameter(description = "شناسه برات", required = true, example = "1")
			@PathVariable Long extraBillId) {
		return ResponseEntity.ok(service.getAuditHistory(extraBillId));
	}
}