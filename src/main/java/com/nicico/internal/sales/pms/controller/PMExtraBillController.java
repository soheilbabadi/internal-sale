//package com.nicico.internal.sales.pms.controller;
//
//import com.nicico.copper.core.SecurityUtil;
//import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto;
//import com.nicico.internal.sales.pms.service.PMExtraBillService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/pms/extra-bill")
/// ///@Tag(name = "PMS Extra Bill", description = "مدیریت ارتباط با سیستم PMS برای ایجاد برات")
//public class PMExtraBillController {
//	private final PMExtraBillService service;
//
//	@GetMapping("/create-from-proforma-master-id/{proformaMasterId}")
//	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
//	@Operation(summary = "ایجاد برات در PMS از روی پروفرما", description = "با دریافت شناسه ProformaMasterId، اطلاعات برات را به سیستم PMS ارسال می کند")
//	public ResponseEntity<HttpStatus> createPMSExtraBill(
//			@Parameter(description = "شناسه پروفرما مستر", required = true) @PathVariable Long proformaMasterId,
//			@Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName,
//			@Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) {
//		log.info("Received request to create PMS Extra Bill from proformaMasterId ID: {} User: {}", proformaMasterId, SecurityUtil.getUsername());
//		service.createPMSExtraBill(proformaMasterId, pmsUserName, resend);
//		return new ResponseEntity<>(HttpStatus.CREATED);
//	}
//
//	@GetMapping("/update/{pmsId}")
//	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
//	@Operation(summary = "بروزرسانی برات در PMS", description = "با دریافت شناسه برات در PMS، اطلاعات برات را به PMS ارسال و بروزرسانی می کند")
//	public ResponseEntity<HttpStatus> updatePmsExtraBill(
//			@Parameter(description = "شناسه برات در PMS", required = true) @PathVariable String pmsId,
//			@Parameter(description = "نام کاربر ثبت کننده در PMS") @RequestParam(required = false, name = "username") String pmsUserName) {
//		log.info("Received request to update PMS Extra Bill ID: {} User: {}", pmsId, SecurityUtil.getUsername());
//		service.updatePmsExtraBill(pmsId, pmsUserName);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//
//	@GetMapping("/send-pms/{extraBillId}")
//	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
//	public ResponseEntity<HttpStatus> sendExtraBillToPms(
//			@PathVariable Long extraBillId,
//			@Parameter(description = "ارسال مجدد حتی اگر قبلاً به PMS ارسال شده باشد") @RequestParam(required = false, name = "resend", defaultValue = "false") boolean resend) throws IOException {
//		service.createPMSExtraBill(extraBillId, resend);
//		return new ResponseEntity<>(HttpStatus.CREATED);
//	}
//
//
//	@GetMapping("/fix-null-pms")
//	@PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
//	public ResponseEntity<List<ProformaBankBillDto.Info>> findRemittanceExtraBillWithoutPmsId() {
//		return ResponseEntity.ok(service.findRemittanceExtraBillWithoutPmsId());
//	}
//
//
//}
