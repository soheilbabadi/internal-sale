package com.nicico.internal.sales.broker.controller;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.broker.dto.BrokerDto;
import com.nicico.internal.sales.broker.service.BrokerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

//@Tag(name = "کارگزار", description = "مدیریت کارگزاران")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/sales/brokers")
@PreAuthorize("@secUtil.hasAuthority('R_INS_BROKER')")
public class BrokerController {
	private final BrokerService brokerService;

	@Operation(summary = "لیست تمام کارگزاران")
	@GetMapping
	public ResponseEntity<?> getAllBrokers() {
		return ResponseEntity.ok(brokerService.getAll());
	}

	@Operation(summary = "دریافت کارگزار بر اساس شناسه")
	@GetMapping("/{id}")
	public ResponseEntity<?> getBrokerById(@PathVariable Long id) {
		BrokerDto.Info broker = brokerService.getById(id);
		return ResponseEntity.ok(broker);
	}

	@Operation(summary = "اطلاعات تماس کارگزار")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_BROKER')")
	@GetMapping("/contact-missing/{id}")
	public ResponseEntity<Map<String, String>> getContactMissing(@PathVariable Long id) {
		return ResponseEntity.ok(brokerService.contactMissing(id));
	}

	@Operation(summary = "ثبت کارگزار جدید")
	@PostMapping("/save")
	public ResponseEntity<BrokerDto.Info> save(@Valid @RequestBody BrokerDto.Create brokerDto) {
		return ResponseEntity.ok(brokerService.save(brokerDto));
	}

	@Operation(summary = "جستجوی کارگزار")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody SearchDTO.SearchRq request) {
		SearchDTO.SearchRs<BrokerDto.Info> result = brokerService.search(request);
		return ResponseEntity.ok(result);
	}
}
