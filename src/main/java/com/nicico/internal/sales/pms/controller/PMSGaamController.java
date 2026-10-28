package com.nicico.internal.sales.pms.controller;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.pms.service.PMSGaamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pms/gaam")
public class PMSGaamController {
	private final PMSGaamService service;

	@GetMapping("/create-from-proforma-master-id/{proformaMasterId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@Operation(summary = "ایجاد اوراق گام در PMS از روی پروفرما", description = "با دریافت شناسه ProformaMasterId، اطلاعات اوراق گام را به سیستم PMS ارسال می کند")
	public ResponseEntity<HttpStatus> createPMSGaam(
			@Parameter(description = "شناسه پروفرما مستر", required = true) @PathVariable Long proformaMasterId,
			@Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName,
			@Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) {
		log.info("Received request to create PMS GAAM from proformaMasterId ID: {} User: {}", proformaMasterId, SecurityUtil.getUsername());
		service.createPMSGaam(proformaMasterId, pmsUserName, resend);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/update/{pmsId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@Operation(summary = "بروزرسانی اوراق گام در PMS", description = "با دریافت شناسه اوراق گام در PMS، اطلاعات را به PMS ارسال و بروزرسانی می کند")
	public ResponseEntity<HttpStatus> updatePmsGaam(
			@Parameter(description = "شناسه اوراق گام در PMS", required = true) @PathVariable String pmsId,
			@Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName) {
		log.info("Received request to update PMS GAAM ID: {} User: {}", pmsId, SecurityUtil.getUsername());
		service.updatePmsGaam(pmsId, pmsUserName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/send-pms/{gaamId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@Operation(summary = "ارسال مستقیم اوراق گام به PMS با شناسه گام", description = "با دریافت شناسه اوراق گام، آن را به PMS ارسال می کند")
	public ResponseEntity<HttpStatus> sendGaamToPms(
			@PathVariable Long gaamId,
			@Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) throws IOException {
		service.createPMSGaam(gaamId, resend);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
