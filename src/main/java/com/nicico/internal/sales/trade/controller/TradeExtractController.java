package com.nicico.internal.sales.trade.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.trade.dto.TradeExtractDto;
import com.nicico.internal.sales.trade.service.TradeExtractService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/trade")
@PreAuthorize("@secUtil.hasAuthority('R_INS_PROFORMA')")
//@Tag(name = "معاملات", description = "استخراج و جستجوی اطلاعات معاملات")
public class TradeExtractController {
	private final TradeExtractService service;


	@Operation(summary = "جستجوی معاملات")
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<TradeExtractDto.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}


	@Operation(summary = "جستجوی معاملات قابل شروع پیش فاکتور")
	@PostMapping("/search-proforma-startable")
	public ResponseEntity<SearchDTO.SearchRs<TradeExtractDto.Info>> searchProformaStartable(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.searchProformaStartable(searchRq));
	}

	@Operation(summary = "دریافت معامله بر اساس کد پرداخت")
	@GetMapping("/{paymentCode}")
	public ResponseEntity<TradeExtractDto.Info> getByPaymentCode(@PathVariable String paymentCode) {
		return new ResponseEntity<>(service.getByPaymentCode(paymentCode), HttpStatus.OK);
	}

	@Operation(summary = "لیست همه معاملات")
	@GetMapping
	public ResponseEntity<List<TradeExtractDto.Info>> getAll() {
		return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
	}

	@Operation(summary = "خروجی اکسل معاملات")
	@PostMapping("/excel")
	public ResponseEntity<byte[]> excel(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		try {
			byte[] wordContent = service.excel(searchRq);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trades.xlsx");
			headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			return new ResponseEntity<>(wordContent, headers, HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Operation(summary = "همگام سازی اطلاعات معاملات")
	@GetMapping("/sync-trade-data")
	public ResponseEntity<?> syncTradeData() {
		service.syncDataTrade();
		return ResponseEntity.ok().build();
	}
}
