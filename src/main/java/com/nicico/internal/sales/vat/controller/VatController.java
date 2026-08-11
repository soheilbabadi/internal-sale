package com.nicico.internal.sales.vat.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.vat.dto.VatDTO;
import com.nicico.internal.sales.vat.service.VatService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/vat")
@PreAuthorize("@secUtil.hasAuthority('R_INS_VAT')")
public class VatController {
	private final VatService service;

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<VatDTO.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_VAT')")
	@PostMapping
	public ResponseEntity<VatDTO.Info> save(@RequestBody VatDTO.Create request) {
		VatDTO.Info save = service.save(request);
		return ResponseEntity.ok(save);
	}

	@PreAuthorize("@secUtil.hasAuthority('D_INS_VAT')")
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
