package com.nicico.internal.sales.pms.controller;

import com.nicico.internal.sales.pms.service.PmsBankCreateRabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pms/bank")
@PreAuthorize("@secUtil.hasAuthority('R_INS_BANK')")
public class PMSBankController {
	private final PmsBankCreateRabbitService service;


	@PreAuthorize("@secUtil.hasAuthority('C_INS_BANK')")
	@GetMapping("/{insIssueBankId}")
	public ResponseEntity<HttpStatus> create(@PathVariable Long insIssueBankId) {
		service.createBank(insIssueBankId);
		return ResponseEntity.ok().build();
	}
}
