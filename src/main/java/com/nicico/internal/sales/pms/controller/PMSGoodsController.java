package com.nicico.internal.sales.pms.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pms.dto.PMSGoodsDTO;
import com.nicico.internal.sales.pms.service.PMSGoodsService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pms/goods")
public class PMSGoodsController {
	private final PMSGoodsService service;

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<PMSGoodsDTO.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}
}
