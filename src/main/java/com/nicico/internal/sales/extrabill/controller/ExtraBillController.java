package com.nicico.internal.sales.extrabill.controller;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.*;
import com.nicico.internal.sales.extrabill.service.ExtraBillIssueService;
import com.nicico.internal.sales.extrabill.service.ExtraBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@Operation(
			summary = "تولید محتوای ایمیل کارگزار",
			description = "با دریافت شناسه LC، محتوای ایمیل مخصوص کارگزار شامل اطلاعات کامل اعتبار اسنادی، تاریخ ها، مبالغ و شرایط را تولید و به صورت رشته متنی بازمی گرداند."
	)
	@GetMapping("/get-broker-email-content/{lcId}")
	public ResponseEntity<String> generateLcBrokerEmailContent(@PathVariable Long lcId) {
		return ResponseEntity.ok(service.generateLcBrokerEmailContent(lcId));
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


	@Operation(
			summary = "بروزرسانی فایل‌های پیوست برات",
			description = "فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند. نیاز به مجوز C_UPD_EXTRA_BILL دارد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
	@PutMapping("/update-files")
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
	public ResponseEntity<ProformaBankBillDto.Info> updateExtraBill(
			@RequestBody UpdateExtraBillRequest updateExtraBillRequest) {
		return ResponseEntity.ok(service.updateExtraBill(updateExtraBillRequest));
	}

	@Operation(
			summary = "دریافت تاریخچه تغییرات برات",
			description = "لیست کامل تغییرات و نسخه‌های مختلف یک برات را بر اساس شناسه آن برمی‌گرداند. شامل اطلاعات ایجاد، ویرایش و وضعیت‌های مختلف برات در طول زمان."
	)

	@GetMapping("/audit-history/{extraBillId}")

	public ResponseEntity<List<ProformaBankBillAuditDto>> getAuditHistory(@PathVariable Long extraBillId) {
		return ResponseEntity.ok(service.getAuditHistory(extraBillId));
	}

	@Operation(
			summary = "جستجوی برات‌های آماده تسویه",
			description = "لیست برات‌هایی که آماده فرآیند تسویه هستند را بر اساس فیلترهای دریافتی برمی‌گرداند."
	)
	@PostMapping("/search/ready-reckoning")
	@PreAuthorize("@secUtil.hasAuthority('R_INS_EXTRA_BILL')")
	public ResponseEntity<SearchDTO.SearchRs<ProformaBankBillDto.Info>> findReadyReckoning(
			@RequestBody(required = false) SearchDTO.SearchRq request) {
		return ResponseEntity.ok(service.findReadyReckoning(request));
	}

	@Operation(
			summary = "دریافت جزئیات تاریخچه گردش کار برات",
			description = "تاریخچه کامل گردش کار (Workflow) یک برات شامل تاییدیه‌ها، ردیه‌ها و توضیحات را برمی‌گرداند."
	)
	@GetMapping("/history/{extraBillId}")
	@PreAuthorize("@secUtil.hasAuthority('R_INS_EXTRA_BILL')")
	public ResponseEntity<ProcessInstanceHistory> getExtraBillHistoryDetail(@PathVariable Long extraBillId) {
		return ResponseEntity.ok(service.getHistoryDetail(extraBillId));
	}

	@Operation(
			summary = "تولید محتوای ایمیل کارگزار",
			description = "محتوای HTML ایمیل فارسی برای اطلاع‌رسانی به کارگزار درباره جزئیات برات را تولید می‌کند."
	)
	@GetMapping("/broker-email/{extraBillId}")
	@PreAuthorize("@secUtil.hasAuthority('R_INS_EXTRA_BILL')")
	public ResponseEntity<String> generateExtraBillBrokerEmailContent(@PathVariable long extraBillId) {
		return ResponseEntity.ok(service.generateExtraBillBrokerEmailContent(extraBillId));
	}

	@Operation(
			summary = "دریافت گزارش وظایف کاربران",
			description = "گزارش وظایف کاربران مرتبط با گردش کار برات را برمی‌گرداند."
	)
	@GetMapping("/user-tasks-report/{extraBillId}")
	@PreAuthorize("@secUtil.hasAuthority('R_INS_EXTRA_BILL')")

	public ResponseEntity<?> getUserTasksReport(@PathVariable Long extraBillId) {
		return ResponseEntity.ok(service.getUserTasksReport(extraBillId));
	}
}