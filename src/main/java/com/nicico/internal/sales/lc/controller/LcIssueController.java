package com.nicico.internal.sales.lc.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.lc.dto.LcIssueProviderDto;
import com.nicico.internal.sales.lc.service.LcIssueService;
import com.nicico.internal.sales.trade.service.TradeExtractService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("@secUtil.hasAuthority('R_INS_LC')")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/lc/lc-issue")
public class LcIssueController {
	private final LcIssueService service;
	private final TradeExtractService tradeExtractService;

	@Operation(
			summary = "دریافت اطلاعات اعتبارات اسنادی قابل صدور",
			description = "با دریافت شناسه یکتای ارائه دهنده صدور اعتبار اسنادی، اطلاعات کامل آن شامل مشخصات بانک، نوع LC، شرایط و ضوابط و سایر جزئیات مرتبط را بازمی گرداند."
	)
	@GetMapping("/{id}")
	public ResponseEntity<LcIssueProviderDto.Info> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}

	@Operation(
			summary = "دریافت اطلاعات اعتبار اسنادی بر اساس شناسه قرارداد در پیش فاکتور ",
			description = "با دریافت شناسه مادر (Master ID)، لیست تمام  اعتبار اسنادی مرتبط با آن را بازمی گرداند. این شامل اعتبارات اسنادی می شود که برای یک درخواست یا قرارداد مشخص ثبت شده اند."
	)
	@GetMapping("/get-by-master/{masterId}")
	public ResponseEntity<List<LcIssueProviderDto.Info>> getByMasterId(@PathVariable Long masterId) {
		return ResponseEntity.ok(service.getByMasterId(masterId));
	}

//    @Operation(
//            summary = "دریافت لیست تمام ارائه دهندگان صدور LC",
//            description = "لیست کامل تمام ارائه دهندگان صدور اعتبار اسنادی را به صورت صفحه بندی شده بازمی گرداند. این متد امکان مرتب سازی بر اساس فیلدهای مختلف مانند شناسه، نام بانک و تاریخ ایجاد را فراهم می کند."
//    )
//    @GetMapping("/all")
//    public ResponseEntity<Page<LcIssueProviderDto.Info>> getAll(
//            @RequestParam(defaultValue = "0") Integer pageIndex,
//            @RequestParam(defaultValue = "10") Integer pageSize,
//            @RequestParam(defaultValue = "id") String sortColumn,
//            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
//        log.info("Getting all proforma LC issue providers - page: {}, size: {}, sort: {}", pageIndex, pageSize, sortColumn);
//        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortDirection, sortColumn));
//        return ResponseEntity.ok(service.getAll(pageable));
//    }

	@Operation(
			summary = "جستجوی اعتبارات اسنادی قابل صدور",
			description = " امکان جستجوی پیشرفته  صدور اعتبار اسنادی را با استفاده از معیارهای مختلف فراهم می کند"
	)
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<LcIssueProviderDto.Info>> search(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(service.search(searchRq));
	}
}