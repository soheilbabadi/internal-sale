package com.nicico.internal.sales.bank.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.BaseBankDto;
import com.nicico.internal.sales.bank.service.BaseBankService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/performa/base-bank")
@PreAuthorize("@secUtil.hasAuthority('R_INS_BANK')")
public class BaseBankController {
	private final BaseBankService service;

	@Operation(summary = "ثبت بانک پایه", description = "ایجاد و ذخیره اطلاعات یک بانک پایه جدید در سیستم. ")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_BANK')")
	@PostMapping
	public ResponseEntity<BaseBankDto> save(@RequestBody BaseBankDto.Create dto) {
		return ResponseEntity.ok(service.save(dto));
	}

	@Operation(summary = "جستجوی بانک پایه", description = "جستجوی بانک های پایه بر اساس معیارهای مختلف. ")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@Operation(summary = "خواندن بانک پایه بر اساس شناسه", description = "دریافت اطلاعات کامل یک بانک پایه با استفاده از شناسه یکتا")
	@GetMapping("/{id}")
	public ResponseEntity<BaseBankDto.Info> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}
}