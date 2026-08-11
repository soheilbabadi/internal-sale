package com.nicico.internal.sales.ins.customer.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ins.customer.dto.CustomerDTO;
import com.nicico.internal.sales.ins.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_CUSTOMER')")
@RequestMapping("/api/v1/ins/customer")
public class CustomerController {
	private final CustomerService service;

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<CustomerDTO.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@PostMapping
	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	public ResponseEntity<CustomerDTO.Info> save(@RequestBody CustomerDTO.Create request) {
		CustomerDTO.Info save = service.save(request);
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
	public ResponseEntity<CustomerDTO.Info> update(@PathVariable Long id, @RequestBody CustomerDTO.Create request) {
		CustomerDTO.Info update = service.update(id, request);
		return ResponseEntity.ok(update);
	}

	@GetMapping("/find-similar/{keyword}")
	public ResponseEntity<List<CustomerDTO.Info>> findSimilar(@RequestParam String keyword) {
		return ResponseEntity.ok(service.findSimilarNames(keyword));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_CUSTOMER')")
	@PutMapping("/import-trade")
	public ResponseEntity<HttpStatus> findSimilar() {
		service.importTradeData();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
