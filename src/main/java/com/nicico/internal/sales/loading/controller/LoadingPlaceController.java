package com.nicico.internal.sales.loading.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.loading.dto.LoadingPlaceDto;
import com.nicico.internal.sales.loading.services.LoadingPlaceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/loading/loading-place")
@PreAuthorize("@secUtil.hasAuthority('R_INS_LOADING_PLACE')")
public class LoadingPlaceController {
	private final LoadingPlaceServiceImpl service;

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

	@PreAuthorize("@secUtil.hasAuthority('R_INS_LOADING_PLACE')")
	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody LoadingPlaceDto.Create request) {
		return ResponseEntity.ok(service.save(request));
	}
}
