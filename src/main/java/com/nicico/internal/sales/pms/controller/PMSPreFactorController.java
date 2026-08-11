package com.nicico.internal.sales.pms.controller;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.pms.service.PMSProformaService;
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
@RequestMapping("/api/v1/pms/pre-factor")
////@Tag(name = "PMS PreFactor", description = "مدیریت ارتباط با سیستم PMS برای ایجاد پیش فاکتور")
public class PMSPreFactorController {
	private final PMSProformaService pmsPreFactorService;

	@GetMapping("/create-from-proforma-master-id/{proformaMasterId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@Operation(summary = "ایجاد پیش فاکتور در PMS از روی آیتم کالای پیش فاکتور", description = "با دریافت شناسه ProformaGoodItem، اطلاعات کالا را به سیستم PMS ارسال و پیش فاکتور ایجاد می کند")
	public ResponseEntity<HttpStatus> createPreFactorFromProformaGoodItem(@Parameter(description = "شناسه آیتم کالای پیش فاکتور", required = true) @PathVariable Long proformaMasterId, @Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName,
	                                                                      @Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend
	) throws IOException {
		log.info("Received request to create PMS pre-factor from proformaMasterId ID: {} User: {}", proformaMasterId, SecurityUtil.getUsername());
		pmsPreFactorService.createPreFactorFromProformaMasterId(proformaMasterId, pmsUserName, resend);
		return ResponseEntity.accepted().build();
	}


	@GetMapping("/send-pms/{proformaMasterId}")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	public ResponseEntity<HttpStatus> sendPms(@PathVariable Long proformaMasterId,
	                                          @Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) throws IOException {

		pmsPreFactorService.createPreFactorFromProformaMasterId(proformaMasterId, SecurityUtil.getUsername(), resend);
		return ResponseEntity.accepted().build();
	}
}
