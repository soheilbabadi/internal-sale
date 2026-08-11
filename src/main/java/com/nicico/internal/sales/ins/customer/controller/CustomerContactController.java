package com.nicico.internal.sales.ins.customer.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ins.customer.dto.CustomerContactDto;
import com.nicico.internal.sales.ins.customer.service.CustomerContactService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_CUSTOMER')")
@RequestMapping("/api/v1/ins/customer-contact")
public class CustomerContactController {
	private final CustomerContactService service;

	@PostMapping
	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	public ResponseEntity<CustomerContactDto.Create> save(@RequestBody CustomerContactDto.Create request) {
		var save = service.save(request);
		return ResponseEntity.ok(save);
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	@PutMapping("/{id}")
	public ResponseEntity<CustomerContactDto.Create> update(@PathVariable Long id, @RequestBody CustomerContactDto.Create request) {
		var update = service.update(id, request);
		return ResponseEntity.ok(update);
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	@PostMapping("/search/{customerId}")
	public ResponseEntity<SearchDTO.SearchRs<CustomerContactDto.Create>> filterByCustomer(@PathVariable Long customerId, @RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.filterByCustomer(customerId, searchRq));
	}
}
