package com.nicico.internal.sales.bank.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.IssuingBankWithPmsIdDto;
import com.nicico.internal.sales.bank.service.IssuingBankWithPmsIdService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("@secUtil.hasAuthority('R_INS_BANK')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/performa/bank-issuer-with-pms-id")
public class IssuingBankWithPmsIdController {
	private final IssuingBankWithPmsIdService service;

	@Operation(summary = "جستجوی بانک صادرکننده به همراه شناسه PMS", description = "این سرویس برای جستجوی بانک های صادرکننده همراه با شناسه PMS استفاده می شود. ")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<IssuingBankWithPmsIdDto.Info>> search(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}
}