package com.nicico.internal.sales.deposit.controller;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.deposit.service.CashDepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/cash-deposit")
@Slf4j
public class CashDepositController {

	private final CashDepositService service;

	@Operation(summary = "تاریخچه صدور فیش واریزی", description = "گزارش تاریخچه کامل صدور فیش واریزی شامل وضعیت ها، تاریخ ها و جزئیات را برمی گرداند.")
	@PostMapping("/search-issue-history")
	public ResponseEntity<?> searchIssueHistory(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(service.searchIssueHistory(searchRq));
	}

	@Operation(summary = "دریافت فیش واریزی بر اساس شناسه قرارداد اصلی", description = "تمام فیش واریزی مرتبط با یک قرارداد پیش فاکتور اصلی (Master) را بر اساس شناسه آن برمی گرداند.")
	@GetMapping("/get-by-master/{masterId}")
	public ResponseEntity<?> getByMasterId(
			@Parameter(description = "شناسه قرارداد پیش فاکتور اصلی", required = true, example = "10")
			@PathVariable Long masterId) {
		return ResponseEntity.ok(service.getByMasterId(masterId));
	}

	@Operation(summary = "ارسال ایمیل تایید تسویه", description = "فیش واریزی مشخص، ایمیل تایید تسویه حساب به کارگزار ارسال می شود. این عملیات پس از اتمام فرآیند تسویه و تایید نهایی انجام می گردد.")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_SEND_NOTIFICATION')")
	@PostMapping("/send-reckoning-mail/{depositId}")
	public ResponseEntity<Void> sendReckoningEmail(@PathVariable Long depositId) {
		service.sendReckoningEmail(depositId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "جستجوی فیش واریزیی آماده تسویه", description = "لیست فیش واریزییی که آماده فرآیند تسویه هستند را بر اساس فیلترهای دریافتی برمی گرداند.")
	@PostMapping("/search-ready-reckoning")
	public ResponseEntity<?> findReadyReckoning(
			@RequestBody(required = false) SearchDTO.SearchRq request) {
		return ResponseEntity.ok(service.findReadyReckoning(request));
	}

	@Operation(summary = "دریافت جزئیات تاریخچه گردش کار فیش واریزی", description = "تاریخچه کامل گردش کار (Workflow) یک فیش واریزی شامل تاییدیه ها، ردیه ها و توضیحات را برمی گرداند.")
	@GetMapping("/history/{depositId}")
	public ResponseEntity<ProcessInstanceHistory> getExtraBillHistoryDetail(@PathVariable Long depositId) {
		return ResponseEntity.ok(service.getHistoryDetail(depositId));
	}

	@Operation(summary = "تولید محتوای ایمیل کارگزار", description = "محتوای HTML ایمیل فارسی برای اطلاع رسانی به کارگزار درباره جزئیات فیش واریزی را تولید می کند.")
	@GetMapping("/get-broker-email-content/{depositId}")
	public ResponseEntity<String> generateExtraBillBrokerEmailContent(@PathVariable long depositId) {
		return ResponseEntity.ok(service.generateBrokerEmailContent(depositId));
	}

	@Operation(summary = "دریافت گزارش وظایف کاربران", description = "گزارش وظایف کاربران مرتبط با گردش کار فیش واریزی را برمی گرداند.")
	@GetMapping("/user-tasks-report/{depositId}")
	public ResponseEntity<?> getUserTasksReport(@PathVariable Long depositId) {
		return ResponseEntity.ok(service.getUserTasksReport(depositId));
	}
}
