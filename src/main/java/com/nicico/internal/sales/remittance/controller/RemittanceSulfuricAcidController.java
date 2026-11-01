package com.nicico.internal.sales.remittance.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.export.service.FmsDocumentService;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import com.nicico.internal.sales.remittance.dto.RemittanceUpdateRequest;
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
@RequestMapping("/api/v1/ins/remittance-sulfuric-acid")
@PreAuthorize("@secUtil.hasAuthority('R_INS_REMITTANCE_SULFURIC_ACID')")
public class RemittanceSulfuricAcidController {
	private final RemittanceService remittanceService;
	private final RemittanceDataProvider remittanceDataProvider;
	private final RemittanceTaxService taxService;
	private final FmsDocumentService fmsDocumentService;
	private final NotificationService notificationService;


	@Operation(summary = "ویرایش توضیحات حواله", description = "توضیحات مربوط به حواله را ویرایش کرده و پس از ذخیره، ایمیلی حاوی پیوست حواله ویرایش شده ارسال می کند")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE_SULFURIC_ACID')")
	@PostMapping("/update-description")
	public ResponseEntity<RemittanceMasterDto> updateDescription(@RequestBody RemittanceUpdateRequest
			                                                             remittanceMasterDto) {
		RemittanceMasterDto resultDto = remittanceService.updateDescription(remittanceMasterDto);
		notificationService.sendEmailWithEditedRemittanceAttachment(resultDto.getId());
		return ResponseEntity.ok(resultDto);
	}

	@Operation(summary = "ثبت حواله", description = "یک حواله جدید بر اساس داده های ورودی ایجاد کرده و فرایند گردش کار آن را آغاز می کند")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE_SULFURIC_ACID')")
	@PostMapping("/create")
	public ResponseEntity<RemittanceMasterDto.Info> create(@RequestBody RemittanceCreateDto createDto) {
			return ResponseEntity.ok(remittanceService.createFromSulfuricAcid(createDto));
	}


	@Operation(summary = "جستجوی حواله های قابل صدور", description = "لیست داده های قابل صدور حواله از منبع اسید سولفوریک را با قابلیت فیلتر و صفحه بندی برمی گرداند")
	@PostMapping("/search-issuable")
	public ResponseEntity<?> searchFromSulfuricAcid(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchFromSulfuricAcid(searchRq));
	}

	@Operation(summary = "جستجوی حواله ها", description = "جستجوی پیشرفته حواله های اسید سولفوریک با نماد کالای SUACB را با قابلیت فیلتر و صفحه بندی برمی گرداند")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchSulfuricAcid(searchRq));
	}

	@Operation(summary = "محاسبه مالیات", description = "مبلغ مالیات مربوط به حواله را بر اساس داده های ورودی محاسبه می کند")
	@PostMapping("/tax")
	public ResponseEntity<Long> calculateTax(@RequestBody RemittanceCreateDto createDto) {
		return ResponseEntity.ok(taxService.calculateTax(createDto).longValue());
	}

	@Operation(summary = "دریافت جزییات حواله", description = "اطلاعات کامل یک حواله را بر اساس شناسه آن برمی گرداند")
	@GetMapping("/get-by-master/{id}")
	public ResponseEntity<RemittanceMasterDto> getByMasterId(@PathVariable Long id) {
		return ResponseEntity.ok(remittanceService.getDetailById(id));
	}

	@Operation(summary = "محاسبه مهلت تحویل", description = "مهلت آخرین تاریخ تحویل را برای حواله محاسبه و برمی گرداند")
	@PostMapping("/delivery-deadline")
	public ResponseEntity<Date> calculateDeliveryDeadlineProforma(@RequestBody RemittanceCreateDto createDto) {
			return ResponseEntity.ok(remittanceDataProvider.lastDeliveryDeadlineTrade(createDto));
	}

	@Operation(summary = "دریافت فایل حواله", description = "فایل PDF حواله را ایجاد و محتوای آن را برمی گرداند")
	@GetMapping(value = "/export/{remittanceId}")
	public ResponseEntity<byte[]> exportRemittanceDoc(@PathVariable Long remittanceId) {
		return ResponseEntity.ok(fmsDocumentService.getOrCreateRemittancePdf(remittanceId).getContent());
	}

	@Operation(summary = "دریافت حواله های قرارداد", description = "لیست حواله های ثبت شده برای یک شماره قرارداد مشخص را برمی گرداند")
	@GetMapping("/get-by-contract-number/{contractNo}")
	public ResponseEntity<List<RemittanceMasterDto.Info>> getByContractNo(@PathVariable Long contractNo) {
		return ResponseEntity.ok(remittanceService.getByContractNo(contractNo));
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