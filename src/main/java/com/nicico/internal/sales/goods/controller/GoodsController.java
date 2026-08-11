package com.nicico.internal.sales.goods.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.goods.dto.GoodsDTO;
import com.nicico.internal.sales.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Tag(name = "کالا", description = "مدیریت کالاها")
@RequiredArgsConstructor
@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_GOODS')")
@RequestMapping("/api/v1/ins/goods")
public class GoodsController {
	private final GoodsService service;

	@Operation(summary = "جستجوی کالا")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<GoodsDTO.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@Operation(summary = "ثبت کالای جدید")
	@PostMapping("")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GOODS')")
	public ResponseEntity<GoodsDTO.Info> save(@RequestBody GoodsDTO.Create request) {
		GoodsDTO.Info save = service.save(request);
		return new ResponseEntity<>(save, HttpStatus.CREATED);
	}

	@Operation(summary = "لیست تمام کالاها")
	@GetMapping()
	public ResponseEntity<List<GoodsDTO.Info>> list() {
		return ResponseEntity.ok(service.list());
	}

	@Operation(summary = "دریافت نام پردازش شده کالا")
	@GetMapping("/get-clean-name/{goodId}")
	public ResponseEntity<String> getCleanName(@PathVariable long goodId) {
		return ResponseEntity.ok(service.getCleanName(goodId));
	}
}
