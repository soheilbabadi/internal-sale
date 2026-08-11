package com.nicico.internal.sales.bank.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.IssuingBankDto;
import com.nicico.internal.sales.bank.service.IssuingBankService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("@secUtil.hasAuthority('R_INS_BANK')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/performa/bank-issuer")
//@Tag(
//        name = "بانک صادرکننده",
//        description = "مدیریت اطلاعات بانک های صادرکننده مورد استفاده در سیستم"
//)
public class IssuingBankController {

	private final IssuingBankService service;

	@Operation(summary = "ثبت/بروز رسانی بانک صادرکننده", description = "ایجاد و ذخیره اطلاعات یک بانک صادرکننده جدید یا بروز رسانی اطلاعات بانک موجود در سیستم")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_BANK')")
	@PostMapping
	public ResponseEntity<IssuingBankDto> save(@RequestBody IssuingBankDto.Create dto) {
		return ResponseEntity.ok(service.save(dto));
	}

	@Operation(summary = "جستجوی بانک صادرکننده", description = "جستجو و فیلتر بانک های صادرکننده بر اساس معیارهای ارسالی")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<IssuingBankDto.Info>> search(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {

		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));

		return ResponseEntity.ok(service.search(searchRq));
	}

	@Operation(summary = "لیست ساده بانک صادرکننده-بدون امکان جستجو", description = "دریافت فهرست کامل بانک های صادرکننده بدون اعمال فیلتر یا جستجو")
	@GetMapping
	public ResponseEntity<List<IssuingBankDto.Info>> list() {
		return ResponseEntity.ok(service.getAll());
	}

	@Operation(summary = "خواندن بانک صادرکننده بر اساس شناسه", description = "دریافت اطلاعات یک بانک صادرکننده بر اساس شناسه یکتا")
	@GetMapping("/{id}")
	public ResponseEntity<IssuingBankDto.Info> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}
}