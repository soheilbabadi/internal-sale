package com.nicico.internal.sales.goods.special.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.goods.dto.GoodsDTO;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.goods.special.dto.PreciousMetalDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Tag(name = "فلزات گرانبها", description = "مدیریت کالاهای فلزات گرانبها")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_GOODS')")
@RequestMapping("/api/v1/ins/performa/precious-metal")
public class PreciousMetalController {

	private final GoodsService goodsService;

	@Operation(summary = "جستجوی فلز گرانبها")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(goodsService.searchPreciousMetal(searchRq));
	}

	@Operation(summary = "افزودن فلز گرانبها")
	@PostMapping("/add/{goodId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GOODS')")
	public ResponseEntity<PreciousMetalDto.Info> save(@PathVariable Long goodId) {
		PreciousMetalDto.Info save = goodsService.savePreciousMetal(goodId);
		return ResponseEntity.ok(save);
	}

	@Operation(summary = "حذف فلز گرانبها")
	@PreAuthorize("@secUtil.hasAuthority('D_INS_GOODS')")
	@DeleteMapping("/remove/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
		goodsService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Deprecated
	@PreAuthorize("@secUtil.hasAuthority('D_INS_GOODS')")
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteOld(@PathVariable Long id) {
		return delete(id);
	}

	@Operation(summary = "کالاهای قابل قبول به عنوان فلز گرانبها")
	@GetMapping("/acceptable-goods")
	public ResponseEntity<List<GoodsDTO.Info>> acceptableGoods() {
		return ResponseEntity.ok(goodsService.acceptableGoods());
	}
}
