package com.nicico.internal.sales.ime.broker.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ime.broker.dto.IMEBrokerDTO;
import com.nicico.internal.sales.ime.broker.service.IMEBrokerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "کارگزار بورس", description = "مدیریت کارگزاران بورس کالا")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/broker")
public class IMEBrokerController {
	private final IMEBrokerService service;

	@Operation(summary = "جستجوی کارگزار بورس")
	@PreAuthorize("@secUtil.hasAuthority('R_IME_BROKER')")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<IMEBrokerDTO.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}
}
