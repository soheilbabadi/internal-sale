package com.nicico.internal.sales.history.controller;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto;
import com.nicico.internal.sales.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "تاریخچه معاملات", description = "مدیریت تاریخچه معاملات و تراکنش ها")
@Slf4j
@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_PROFORMA')")
@RequestMapping("/api/v1/ins/trade/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;

	@Operation(summary = "جستجوی تاریخچه", description = "جستجوی تاریخچه معاملات با قابلیت صفحه بندی و فیلتر")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<HistoryExtractMasterDto.Info>> search(@RequestBody SearchDTO.SearchRq request) {
		return ResponseEntity.ok(historyService.search(request));
	}

	@Operation(summary = "دریافت تاریخچه بر اساس شناسه", description = "شناسه همان شناسه اطلاعیه عرضه است")
	@GetMapping("/find-by-id/{historyId}")
	public ResponseEntity<HistoryExtractMasterDto.Info> findById(@PathVariable Long historyId) {
		return ResponseEntity.ok(historyService.findById(historyId));
	}

	@Operation(summary = "جزئیات تاریخچه", description = "دریافت جزئیات کامل تاریخچه بر اساس شناسه")
	@GetMapping("/get-detail-by-id/{id}")
	public ResponseEntity<?> getHistoryDetails(@PathVariable Long id) {
		return ResponseEntity.ok(historyService.getHistoryDetails(id));
	}

}