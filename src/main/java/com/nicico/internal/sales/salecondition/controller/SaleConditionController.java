package com.nicico.internal.sales.salecondition.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.salecondition.dto.SaleConditionDto;
import com.nicico.internal.sales.salecondition.service.SaleConditionService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/performa/sale-condition")
public class SaleConditionController {
	private final SaleConditionService saleConditionService;

	@GetMapping("/{id}")
	public ResponseEntity<SaleConditionDto.Info> getCurrentRule(@PathVariable Long id) {
		return ResponseEntity.ok(saleConditionService.getCurrentRule(id));
	}

	@PostMapping
	public ResponseEntity<SaleConditionDto.Info> saveRule(@RequestBody SaleConditionDto.Create ruleDto) {
		return ResponseEntity.ok(saleConditionService.save(ruleDto));
	}

	@GetMapping
	public ResponseEntity<List<SaleConditionDto.Info>> getCurrentRule() {
		return ResponseEntity.ok(saleConditionService.getCurrentRule());
	}

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<SaleConditionDto.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(saleConditionService.search(searchRq));
	}

	@GetMapping("/history/{goodId}")
	public ResponseEntity<List<SaleConditionDto.Info>> getGoodsHistory(@PathVariable Long goodId) {
		return ResponseEntity.ok(saleConditionService.getGoodsHistory(goodId));
	}
}
