package com.nicico.internal.sales.loading.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.loading.dto.CreateGoodLoading;
import com.nicico.internal.sales.loading.dto.CreateGoodLoadingBatch;
import com.nicico.internal.sales.loading.services.LoadingExtractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/loading/loading-place-extract")
public class LoadingExtractController {
	private final LoadingExtractService service;

	@GetMapping()
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(service.getAll());
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable Long id) {
		return ResponseEntity.ok(service.get(id));
	}

	@PostMapping("/save")
	public ResponseEntity<HttpStatus> save(@RequestBody CreateGoodLoading request) {
		service.save(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/save-batch")
	public ResponseEntity<HttpStatus> saveBatch(@RequestBody CreateGoodLoadingBatch request) {
		service.saveBatch(request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteById(@PathVariable Long id) {
		service.deleteById(id);
		return ResponseEntity.ok().build();
	}


}
