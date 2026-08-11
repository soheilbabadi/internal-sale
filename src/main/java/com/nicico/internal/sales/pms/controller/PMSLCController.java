package com.nicico.internal.sales.pms.controller;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.pms.service.PMSLcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pms/lc")
////@Tag(name = "PMS LC", description = "مدیریت ارتباط با سیستم PMS برای ایجاد LC")
public class PMSLCController {
	private final PMSLcService service;

	@GetMapping("/create-from-proforma-master-id/{proformaMasterId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@Operation(summary = "ایجاد LC در PMS از روی آیتم LC", description = "با دریافت شناسه ProformaGoodItem، اطلاعات کالا را به سیستم PMS ارسال و پیش فاکتور ایجاد می کند")
	public ResponseEntity<HttpStatus> createPMSLc(@Parameter(description = "شناسه آیتم کالای پیش فاکتور", required = true) @PathVariable Long proformaMasterId, @Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName,
	                                              @Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend
	) {
		log.info("Received request to create PMS pre-factor from proformaMasterId ID: {} User: {}", proformaMasterId, SecurityUtil.getUsername());
		service.createPMSLc(proformaMasterId, pmsUserName, resend);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/update/{pmsId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@Operation(summary = "بروزرسانی LC در PMS", description = "با دریافت شناسه LC در PMS، اطلاعات LC را به PMS ارسال و بروزرسانی می کند")
	public ResponseEntity<HttpStatus> updatePmsLc(@Parameter(description = "شناسه LC در PMS", required = true) @PathVariable String pmsId,
	                                              @Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName
	) {
		log.info("Received request to update PMS LC ID: {} User: {}", pmsId, SecurityUtil.getUsername());
		service.updatePmsLc(pmsId, pmsUserName);
		return new ResponseEntity<>(HttpStatus.OK);
	}


	@GetMapping("/send-pms/{lcId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	public ResponseEntity<HttpStatus> sendLcToPms(@PathVariable Long lcId,
	                                              @Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) throws IOException {
		service.createPMSLc(lcId, resend);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}


	@GetMapping("/fix-null-pms")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	public ResponseEntity<List<LcDto.Info>> findRemittanceLcWithoutPmsId() {
		return ResponseEntity.ok(service.findRemittanceLcWithoutPmsId());
	}


}
