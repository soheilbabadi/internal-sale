package com.nicico.internal.sales.pms.controller;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.pms.service.PMSRemittanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pms/remittance")
@AllArgsConstructor
@Slf4j
class PMSRemittanceController {
	private final PMSRemittanceService service;

    @GetMapping("/create-from-remittance-master-id/{remittanceMasterId}")
    @PreAuthorize("@secUtil.hasAuthority('C_PMS_REMITTANCE')")
    @Operation(summary = "ایجاد پیش فاکتور در PMS از روی آیتم کالای پیش فاکتور", description = "با دریافت شناسه ProformaGoodItem، اطلاعات کالا را به سیستم PMS ارسال و پیش فاکتور ایجاد می کند")
    public ResponseEntity<HttpStatus> createPreFactorFromProformaGoodItem(
            @Parameter(description = "شناسه آیتم کالای پیش فاکتور", required = true) @PathVariable Long remittanceMasterId,
            @Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName,
            @Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend
    ) throws IOException {
        log.info("Received request to create PMS remittance from remittanceMasterId ID: {} User: {}", remittanceMasterId, SecurityUtil.getUsername());
        service.create(remittanceMasterId, pmsUserName, resend);
        log.info("Saved   PMS remittance from remittanceMasterId ID: {} User: {}",
                remittanceMasterId,
                SecurityUtil.getUsername()
        );
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/update/{remittanceMasterId}")
    @PreAuthorize("@secUtil.hasAuthority('C_PMS_REMITTANCE')")
    @Operation(summary = "بروزرسانی حواله در PMS", description = "با دریافت شناسه حواله داخلی، اطلاعات حواله را به سیستم PMS ارسال و بروزرسانی می کند")
    public ResponseEntity<Void> updatePmsRemittance(
            @Parameter(description = "شناسه حواله داخلی", required = true) @PathVariable Long remittanceMasterId,
            @Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName
    ) throws IOException {
        log.info("Received request to update PMS remittance ID: {} User: {}", remittanceMasterId, SecurityUtil.getUsername());
        service.update(remittanceMasterId, pmsUserName);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/send-pms/{remittanceMasterId}")
    @PreAuthorize("@secUtil.hasAuthority('C_PMS_REMITTANCE')")

    public ResponseEntity<HttpStatus> sendPms(@PathVariable Long remittanceMasterId,
                                              @Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) throws IOException {
        service.create(remittanceMasterId, SecurityUtil.getUsername(), resend);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
