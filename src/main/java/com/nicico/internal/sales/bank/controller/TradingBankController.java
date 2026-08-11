package com.nicico.internal.sales.bank.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.TradingBankDto;
import com.nicico.internal.sales.bank.service.TradingBankService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/performa/trading-bank")
@PreAuthorize("@secUtil.hasAuthority('R_INS_BANK')")
public class TradingBankController {
	private final TradingBankService service;

	@Operation(summary = "ثبت بانک معامله کننده", description = "این سرویس برای ثبت و ایجاد یک بانک معامله کننده جدید در سیستم استفاده می شود. ")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_BANK')")
	@PostMapping
	public ResponseEntity<TradingBankDto> save(@RequestBody TradingBankDto.Create dto) {
		return ResponseEntity.ok(service.save(dto));
	}

	@Operation(summary = "جستجوی بانک معامله کننده", description = "این سرویس برای جستجوی پیشرفته بانک های معامله کننده با قابلیت فیلترهای داینامیک طراحی شده است. ")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq,
	                                @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@Operation(summary = "لیست ساده بانک معامله کننده - بدون امکان جستجو", description = "این سرویس برای دریافت لیست کامل تمام بانک های معامله کننده بدون اعمال هیچ فیلتر یا جستجویی استفاده می شود. ")
	@GetMapping()
	public ResponseEntity<List<TradingBankDto.Info>> list() {
		return ResponseEntity.ok(service.getAll());
	}
}