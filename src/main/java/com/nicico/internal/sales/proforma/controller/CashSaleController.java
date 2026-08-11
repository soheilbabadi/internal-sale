package com.nicico.internal.sales.proforma.controller;

import com.nicico.internal.sales.proforma.dto.CashSaleCreateRequest;
import com.nicico.internal.sales.proforma.service.cash.CashSaleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("@secUtil.hasAuthority('R_INS_PROFORMA')")
//@Tag(name = "فروش نقدی", description = "API مربوط به فاکتورهای فروش نقدی")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/cash-sale")
public class CashSaleController {
	private final CashSaleService cashSaleService;

	@Operation(
			summary = "ثبت فاکتور فروش نقدی",
			description = "این متد یک فاکتور فروش نقدی جدید را در سیستم ثبت می کند. ابتدا بررسی می شود که آیا کالای مورد نظر جزو فلزات گرانبها می باشد یا خیر، سپس بر اساس نتیجه، فرآیند ثبت در سرویس مناسب (فروش نقدی معمولی یا فروش نقدی فلزات گرانبها) انجام می شود. ورودی شامل اطلاعات خریدار، کالاها، مبالغ و شرایط فروش می باشد."
	)
	@Deprecated(forRemoval = true)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@PostMapping
	public ResponseEntity<?> createCashSale(@RequestBody CashSaleCreateRequest dto) {

		return ResponseEntity.ok(cashSaleService.create(dto));
	}
}