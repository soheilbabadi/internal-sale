package com.nicico.internal.sales.gaam.controller;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.gaam.dto.*;
import com.nicico.internal.sales.gaam.service.GaamIssueService;
import com.nicico.internal.sales.gaam.service.GaamService;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@PreAuthorize("@secUtil.hasAuthority('R_INS_GAAM')")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/gaam")
public class GaamController {

	private final GaamService service;
	private final GaamIssueService extraBillIssueService;
	private final ProcessStatusDeterminerService processStatusDeterminerService;

	@Operation(summary = "جستجوی اوراق گامهای قابل صدور", description = "لیست اوراق گامها که آماده صدور هستند را بر اساس فیلترهای دریافتی برمی گرداند.")
	@PostMapping("/search-issuable")
	public ResponseEntity<SearchDTO.SearchRs<GaamIssueProviderDto.Info>> searchIssuable(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(extraBillIssueService.search(searchRq));
	}

	@Operation(summary = "جستجوی اوراق گامها", description = "اوراق گامها صادر شده را بر اساس فیلترهای ورودی جستجو کرده و نتیجه را برمی گرداند.")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<GaamDto.Info>> search(
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
	@GetMapping("/get-broker-email-content/{gaamId}")
	public ResponseEntity<String> generateLcBrokerEmailContent(@PathVariable Long gaamId) {
		return ResponseEntity.ok(service.generateGaamBrokerEmailContent(gaamId));
	}

	@Operation(summary = "تاریخچه صدور اوراق گامها", description = "گزارش تاریخچه کامل صدور اوراق گامها شامل وضعیت ها، تاریخ ها و جزئیات را برمی گرداند.")
	@PostMapping("/search-issue-history")
	public ResponseEntity<SearchDTO.SearchRs<GaamReportDto.Info>> searchIssueHistory(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(service.searchReport(searchRq));
	}

	@Operation(summary = "دریافت اوراق گامها بر اساس شناسه قرارداد اصلی", description = "تمام اوراق گامها مرتبط با یک قرارداد پیش فاکتور اصلی (Master) را بر اساس شناسه آن برمی گرداند.")
	@GetMapping("/get-by-master/{masterId}")
	public ResponseEntity<List<GaamDto.Info>> getByMasterId(
			@Parameter(description = "شناسه قرارداد پیش فاکتور اصلی", required = true, example = "10")
			@PathVariable Long masterId) {
		return ResponseEntity.ok(service.getByMasterId(masterId));
	}

	@Operation(summary = "ثبت اوراق گام  جدید", description = "یک اوراق گام  جدید بر اساس اطلاعات دریافتی ایجاد و ذخیره می کند. نیاز به مجوز C_INS_GAAM دارد.")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GAAM')")
	@PostMapping("/save")
	public ResponseEntity<GaamDto.Info> save(@RequestBody GaamRequest proformaBankBillRequest) {
		return ResponseEntity.ok(service.save(proformaBankBillRequest));
	}

	@Operation(
			summary = "ثبت اوراق گام های جدید", description = "اوراق گام های دریافتی را ثبت و ذخیره می کند. نیاز به مجوز C_INS_GAAM دارد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GAAM')")
	@PostMapping("/save-all")
	public ResponseEntity<List<GaamDto.Info>> saveAll(@RequestBody List<GaamRequest> requests) {
		return ResponseEntity.ok(service.saveAll(requests));
	}

	@Operation(
			summary = "بروزرسانی فایل های پیوست اوراق گام",
			description = "فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می کند. نیاز به مجوز C_UPD_EXTRA_BILL دارد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GAAM')")
	@PutMapping("/update-files")
	public ResponseEntity<GaamDto.Info> updateBillFiles(@RequestBody GaamFileUpdateDto updateDto) {

		return ResponseEntity.ok(service.updateGaamFiles(updateDto));
	}

	@Operation(
			summary = "ارسال ایمیل تایید تسویه",
			description = "اوراق گام مشخص، ایمیل تایید تسویه حساب به کارگزار ارسال می شود. این عملیات پس از اتمام فرآیند تسویه و تایید نهایی انجام می گردد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_SEND_NOTIFICATION')")
	@PostMapping("/send-reckoning-extra-bill/{gaamId}")
	public ResponseEntity<Void> sendReckoningEmail(@PathVariable Long gaamId) {
		service.sendReckoningEmail(gaamId);
		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "بروزرسانی اطلاعات اوراق گام",
			description = "اطلاعات بانکی و الکترونیکی اوراق گام را بروزرسانی می کند. نیاز به مجوز C_UPD_EXTRA_BILL دارد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GAAM')")
	@PutMapping("/update")
	public ResponseEntity<GaamDto.Info> updateExtraBill(
			@RequestBody UpdateGaamRequest updateGaamRequest) {
		return ResponseEntity.ok(service.update(updateGaamRequest));
	}

	@Operation(
			summary = "دریافت تاریخچه تغییرات اوراق گام",
			description = "لیست کامل تغییرات و نسخه های مختلف یک اوراق گام را بر اساس شناسه آن برمی گرداند. شامل اطلاعات ایجاد، ویرایش و وضعیت های مختلف اوراق گام در طول زمان."
	)

	@GetMapping("/audit-history/{gaamId}")
	public ResponseEntity<?> getAuditHistory(@PathVariable Long gaamId) {
		return ResponseEntity.ok(service.getAuditHistory(gaamId));
	}

	@Operation(
			summary = "جستجوی اوراق گام های آماده تسویه",
			description = "لیست اوراق گام هایی که آماده فرآیند تسویه هستند را بر اساس فیلترهای دریافتی برمی گرداند."
	)
	@PostMapping("/search/ready-reckoning")
	public ResponseEntity<?> findReadyReckoning(
			@RequestBody(required = false) SearchDTO.SearchRq request) {
		return ResponseEntity.ok(service.findReadyReckoning(request));
	}

	@Operation(
			summary = "دریافت جزئیات تاریخچه گردش کار اوراق گام",
			description = "تاریخچه کامل گردش کار (Workflow) یک اوراق گام شامل تاییدیه ها، ردیه ها و توضیحات را برمی گرداند."
	)
	@GetMapping("/history/{gaamId}")
	public ResponseEntity<ProcessInstanceHistory> getExtraBillHistoryDetail(@PathVariable Long gaamId) {
		return ResponseEntity.ok(service.getHistoryDetail(gaamId));
	}

	@Operation(
			summary = "تولید محتوای ایمیل کارگزار",
			description = "محتوای HTML ایمیل فارسی برای اطلاع رسانی به کارگزار درباره جزئیات اوراق گام را تولید می کند."
	)
	@GetMapping("/get-broker-email-content/{gaamId}")
	public ResponseEntity<String> generateExtraBillBrokerEmailContent(@PathVariable long gaamId) {
		return ResponseEntity.ok(service.generateGaamBrokerEmailContent(gaamId));
	}

	@Operation(
			summary = "دریافت گزارش وظایف کاربران",
			description = "گزارش وظایف کاربران مرتبط با گردش کار اوراق گام را برمی گرداند."
	)
	@GetMapping("/user-tasks-report/{gaamId}")
	public ResponseEntity<?> getUserTasksReport(@PathVariable Long gaamId) {
		return ResponseEntity.ok(service.getUserTasksReport(gaamId));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_GAAM')")
	@PutMapping("/cancel")
	public ResponseEntity<?> cancelLc(@RequestBody GaamCancelRequest request) {
		service.cancel(request);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/update-all-acknowledgment")
	public ResponseEntity<Void> updateAllLcAcknowledgment() {
//		processStatusDeterminerService.updateAllGaamAcknowledgments();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}