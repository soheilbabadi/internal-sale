package com.nicico.internal.sales.pricing.controller;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pricing.dto.LastUsdRateInfo;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyDto;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyRequest;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyTypeDto;
import com.nicico.internal.sales.pricing.service.PricingCurrencyService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/ins/pricing/currency")
@PreAuthorize("@secUtil.hasAuthority('R_INS_PRICING')")
@RequiredArgsConstructor
//@Tag(name = "Pricing Currency", description = "کلاس پایه برای ثبت نرخ ارز و محاسبه میانگین قیمت قابل استفاده در قیمتگذاری")
public class PricingCurrencyController {

	private final PricingCurrencyService service;

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PRICING')")
	@PostMapping("/save")
	public ResponseEntity<PricingCurrencyDto.Info> save(@RequestBody PricingCurrencyRequest request) {
		PricingCurrencyDto.Info response = service.save(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/save-currency-title")
	public ResponseEntity<PricingCurrencyTypeDto.Info> saveCurrencyTitle(@RequestBody PricingCurrencyTypeDto request) {
		PricingCurrencyTypeDto.Info response = service.saveType(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<PricingCurrencyDto.Info>> search(@RequestBody SearchDTO.SearchRq request) {
		SearchDTO.SearchRs<PricingCurrencyDto.Info> response = service.search(request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{persianShortDate}")
	public ResponseEntity<Void> deleteById(
			@Parameter(description = "Persian date (format: yyyy/MM/dd)", example = "1403/01/29")
			@PathVariable String persianShortDate) {
		service.deleteById(persianShortDate);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/get-usd-last-avg")
	public ResponseEntity<BigDecimal> getLastUsdAverage() {
		return ResponseEntity.ok(service.getAverageUsdRateForLastDay());
	}

	@GetMapping("/get-last-detail")
	public ResponseEntity<LastUsdRateInfo> getLastUsdDetail() {
		return ResponseEntity.ok(service.getLastUsdRateInfo());

	}

	@GetMapping("/get-last-price-type")
	public ResponseEntity<?> getLastPricingType() {
		return ResponseEntity.ok(service.getLastPricingType());

	}
}