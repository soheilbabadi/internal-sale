package com.nicico.internal.sales.extrabill.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.ExtraBillIssueProviderDto;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillReportDto;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillRequest;
import com.nicico.internal.sales.extrabill.service.ExtraBillIssueService;
import com.nicico.internal.sales.extrabill.service.ExtraBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
	public ResponseEntity<SearchDTO.SearchRs<ExtraBillIssueProviderDto.Info> > searchIssuable(
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
}