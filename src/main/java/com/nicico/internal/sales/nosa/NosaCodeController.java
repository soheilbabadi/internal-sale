package com.nicico.internal.sales.nosa;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "NosaCode", description = "خدمات تولید کد نوسا")
@PreAuthorize("@secUtil.hasAuthority('R_INS_LC')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/nosa-code")
public class NosaCodeController {

	private final NosaCodeService nosaCodeService;

	@Operation(
			summary = "تولید کد نوسا برای LC",
			description = "این متد کد نوسا را برای LC با فرمت '107/18' + کد بانک + دو رقم آخر سال جلالی + شماره سه رقمی تولید می کند."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@GetMapping("/lc/{issuerBankId}")
	public ResponseEntity<String> generateLcNosaCode(@PathVariable Long issuerBankId) {
		String nosaCode = nosaCodeService.generateLcNosaCode(issuerBankId);
		return ResponseEntity.ok(nosaCode);
	}

	@Operation(
			summary = "تولید کد نوسا برای برات الکترونیک",
			description = "این متد کد نوسا را برای برات الکترونیک با فرمت '109/18' + کد بانک + دو رقم آخر سال جلالی + شماره سه رقمی تولید می کند."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA_BANK_BILL')")
	@GetMapping("/extra-bill/{issuerBankId}")
	public ResponseEntity<String> generateExtraBillNosaCode(@PathVariable Long issuerBankId) {
		String nosaCode = nosaCodeService.generateExtraBillNosaCode(issuerBankId);
		return ResponseEntity.ok(nosaCode);
	}
}
