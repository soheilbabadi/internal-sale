package com.nicico.internal.sales.pms.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pms.dto.PMSCustomerDTO;
import com.nicico.internal.sales.pms.service.PMSCustomerService;
import com.nicico.internal.sales.pms.service.PmsCustomerCreateRabbitService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pms/customer")
@PreAuthorize("@secUtil.hasAuthority('R_INS_CUSTOMER')")
public class PMSCustomerController {
	private final PMSCustomerService service;
	private final PmsCustomerCreateRabbitService rabbitService;

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<PMSCustomerDTO.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	@GetMapping("/{insCustomerId}")
	public ResponseEntity<HttpStatus> create(@PathVariable Long insCustomerId) {
		rabbitService.createCustomer(insCustomerId);
		return ResponseEntity.ok().build();
	}
}
