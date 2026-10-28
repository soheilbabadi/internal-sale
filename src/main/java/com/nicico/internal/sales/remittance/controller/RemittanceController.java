package com.nicico.internal.sales.remittance.controller;

import com.fgostar.fms.sdk.model.FmsFile;
import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.export.service.FmsDocumentService;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.remittance.dto.LotNumberRequest;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import com.nicico.internal.sales.remittance.dto.RemittanceUpdateRequest;
import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.remittance.service.RemittanceDataProvider;
import com.nicico.internal.sales.remittance.service.RemittanceService;
import com.nicico.internal.sales.remittance.service.RemittanceTaxService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/remittance")
@PreAuthorize("@secUtil.hasAuthority('R_INS_REMITTANCE')")
public class RemittanceController {
	private final RemittanceService remittanceService;
	private final RemittanceDataProvider remittanceDataProvider;
	private final RemittanceTaxService taxService;
	private final FmsDocumentService fmsDocumentService;
	private final NotificationService notificationService;


	@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE')")
	@PostMapping("/update-description")
	public ResponseEntity<RemittanceMasterDto> updateDescription(@RequestBody RemittanceUpdateRequest
			                                                             remittanceMasterDto) {
		RemittanceMasterDto resultDto = remittanceService.updateDescription(remittanceMasterDto);
		notificationService.sendEmailWithEditedRemittanceAttachment(resultDto.getId());
		return ResponseEntity.ok(resultDto);
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE')")
	@PostMapping("/create")
	public ResponseEntity<RemittanceMasterDto.Info> create(@RequestBody RemittanceCreateDto createDto) {
		if (createDto.getSourceType() == RemittanceSourceType.TRADE) {
			return ResponseEntity.ok(remittanceService.createFromTrade(createDto));
		} else if (createDto.getSourceType() == RemittanceSourceType.SULFURIC_ACID) {
			return ResponseEntity.ok(remittanceService.createFromSulfuricAcid(createDto));
		}
		return ResponseEntity.ok(remittanceService.createFromProforma(createDto));
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.search(searchRq));
	}

	@PostMapping("/search-trade")
	public ResponseEntity<?> searchTrade(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchFromTrade(searchRq));
	}

	@PostMapping("/search-sulfuric-acid")
	public ResponseEntity<?> searchFromSulfuricAcid(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchFromSulfuricAcid(searchRq));
	}


	@PostMapping("/search-proforma")
	public ResponseEntity<?> searchProforma(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchFromProforma(searchRq));
	}

	@PostMapping("/tax")
	public ResponseEntity<Long> calculateTax(@RequestBody RemittanceCreateDto createDto) {
		return ResponseEntity.ok(taxService.calculateTax(createDto).longValue());
	}

	@GetMapping("/get-by-master/{id}")
	public ResponseEntity<RemittanceMasterDto> getByMasterId(@PathVariable Long id) {
		return ResponseEntity.ok(remittanceService.getDetailById(id));
	}

	@PostMapping("/delivery-deadline")
	public ResponseEntity<Date> calculateDeliveryDeadlineProforma(@RequestBody RemittanceCreateDto createDto) {
		if (createDto.getSourceType() == RemittanceSourceType.TRADE || createDto.getSourceType() == RemittanceSourceType.SULFURIC_ACID) {
			return ResponseEntity.ok(remittanceDataProvider.lastDeliveryDeadlineTrade(createDto));
		} else {
			return ResponseEntity.ok(remittanceDataProvider.lastDeliveryDeadlineProforma(createDto));
		}

	}

	@GetMapping(value = "/export/{remittanceId}")
	public ResponseEntity<byte[]> exportRemittanceDoc(@PathVariable Long remittanceId) {
		return ResponseEntity.ok(fmsDocumentService.getOrCreateRemittancePdf(remittanceId).getContent());
	}

	@Operation(summary = "خروجی FMS حواله", description = "فایل FMS حواله را پس از تبدیل فایل Word مربوطه برمی گرداند.")
	@GetMapping("/export-fms/{remittanceId}")
	public ResponseEntity<FmsFile> exportProformaFms(@PathVariable long remittanceId) {
		return ResponseEntity.ok(fmsDocumentService.getOrCreateRemittancePdf(remittanceId));
	}

	@GetMapping("/get-by-contract-number/{contractNo}")
	public ResponseEntity<List<RemittanceMasterDto.Info>> getByContractNo(@PathVariable Long contractNo) {
		return ResponseEntity.ok(remittanceService.getByContractNo(contractNo));
	}

	@Operation(summary = "خواندن شماره لات", description = "شماره لات را از آگهی عرضه استخراج می کند")
	@PostMapping("/get-lot-number")
	public ResponseEntity<String> getLotNumber(@RequestBody LotNumberRequest request) {
		return ResponseEntity.ok(remittanceDataProvider.getLotNumber(request.getTradeId(), request.getSourceType()));
	}

	@Operation(summary = "دریافت جزییات تاریخچه تسک ها", description = "لیست کامل تاریخچه تسک های مربوط به حواله را شامل تمام فعالیت های انجام شده در  فرایند همراه با زمان و وضعیت هر تسک بازمی گرداند."
	)
	@GetMapping("/get-task-history-detail/{lcId}")
	public ResponseEntity<?> getLcHistoryDetail(@PathVariable Long lcId) {
		return ResponseEntity.ok(remittanceService.getHistoryDetail(lcId));
	}

	@Operation(summary = "تاریخچه تسک", description = "لیست کامل تاریخچه تغییرات حواله را شامل تمام ویرایش ها، به روزرسانی ها و تغییرات وضعیت به همراه زمان و کاربر انجام دهنده بازمی گرداند."
	)
	@GetMapping("/get-task-history/{lcId}")
	public ResponseEntity<?> getAuditHistory(@PathVariable Long lcId) {
		return ResponseEntity.ok(remittanceService.getUserTasksReport(lcId));
	}

}