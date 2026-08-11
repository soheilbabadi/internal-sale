package com.nicico.internal.sales.bill.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bill.service.BillingDataProviderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "صورتحساب", description = "مدیریت صورتحساب ها - شامل جستجو، مشاهده و مدیریت اطلاعات صورت حساب های مالی")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/billing")
@PreAuthorize("@secUtil.hasAuthority('R_INS_BILL')")
@Slf4j
public class BillingController {

	private final BillingDataProviderService billingDataProviderService;

	@Operation(
			summary = "جستجوی صورتحساب",
			description = "سرویس جستجوی پیشرفته صورت حساب های مالی با قابلیت فیلتر"
	)
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq,
	                                @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(billingDataProviderService.searchBillingStartDataProvider(searchRq));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_BILL')")
	@PostMapping
	public ResponseEntity<?> create() {
		return ResponseEntity.ok(billingDataProviderService.searchBillingStartDataProvider(null));
	}


	@PreAuthorize("@secUtil.hasAuthority('D_INS_BILL')")
	@PostMapping("/reversal")
	public ResponseEntity<?> reversal() {
		return ResponseEntity.ok(billingDataProviderService.searchBillingStartDataProvider(null));
	}
}