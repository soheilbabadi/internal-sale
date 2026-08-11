package com.nicico.internal.sales.proforma.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.service.ExportDocService;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.notification.service.SmsNotificationService;
import com.nicico.internal.sales.proforma.dto.*;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.service.PreciousMetalService;
import com.nicico.internal.sales.proforma.service.ProformaService;
import com.nicico.internal.sales.proforma.service.cash.CashSaleService;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/performa")
@PreAuthorize("@secUtil.hasAuthority('R_INS_PROFORMA')")
public class ProformaController {

	private final ProformaService proformaService;
	private final PreciousMetalService proformaContractPreciousMetalService;
	private final NotificationService notificationService;
	private final ExportDocService exportDocService;
	private final SmsNotificationService smsNotificationService;
	private final ProcessStatusDeterminerService processStatusDeterminerService;
	private final CashSaleService cashSaleService;

	// ==================== TASK HISTORY ====================

	@Operation(summary = "تاریخچه تسک", description = "گزارش خلاصه از تاریخچه فرآیند")
	@GetMapping("/get-task-history/{masterId}")
	public ResponseEntity<?> getUserTasksReport(@PathVariable Long masterId) {
		return ResponseEntity.ok(processStatusDeterminerService.getProformaSummaryReport(masterId));
	}

	@Operation(summary = "تاریخچه کامل تسک", description = "گزارش کامل و جزییات تاریخچه فرآیند")
	@GetMapping("/get-task-history-detail/{masterId}")
	public ResponseEntity<?> getLcHistoryDetail(@PathVariable Long masterId) {
		return ResponseEntity.ok(processStatusDeterminerService.getProformaHistoryDetail(masterId));
	}

	// ==================== SEARCH ====================

	@Operation(summary = "جستجوی پیشرفته", description = "جستجوی پیش فاکتورها با معیارهای داینامیک")
	@PostMapping("/search")
	public ResponseEntity<?> search(
			@RequestBody(required = false) SearchDTO.SearchRq searchRq,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {

		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray())) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		return ResponseEntity.ok(proformaService.search(searchRq));
	}

	@Operation(summary = "جستجو با کد ملی", description = "لیست پیش فاکتورهای مشتری بر اساس شماره ملی")
	@GetMapping("/search-by-national-code")
	public ResponseEntity<SearchDTO.SearchRs<ProformaMasterDTO.Info>> searchByNationalCode(
			@RequestParam(required = false) List<String> nationalCodes) {
		return ResponseEntity.ok(proformaService.getByNationalCode(nationalCodes));
	}

	@Operation(summary = "پیش فاکتورهای خطادار", description = "لیست پیش فاکتورهایی که در شروع فرایند با خطا مواجه شده اند")
	@GetMapping("/get-failed-proforma")
	public ResponseEntity<List<ProformaMasterDTO.Info>> getFailed(
			@RequestParam(defaultValue = "0") Integer pageIndex,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortColumn) {

		Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortColumn));
		return ResponseEntity.ok(proformaService.getFailedProforma(pageable, Sort.by(sortColumn)));
	}

	@Operation(summary = "پیش فاکتورهای قابل ابطال", description = "پیش فاکتورهایی که تسویه نقدی شده و باید باطل شوند")
	@GetMapping("/get-cancelable-proforma")
	public ResponseEntity<List<ProformaMasterDTO.Info>> getCancelableProforma() {
		return ResponseEntity.ok(proformaService.getCancelable());
	}

	// ==================== GET BY ID ====================

	@Operation(summary = "جستجو بر اساس قرارداد", description = "خواندن پیش فاکتور و کالاها بر اساس شناسه قرارداد اصلی")
	@GetMapping("/get-by-master/{id}")
	public ResponseEntity<ProformaResponseDto> getByMasterId(@PathVariable Long id) {
		return ResponseEntity.ok(proformaService.getDetailById(id));
	}

	@Operation(summary = "جستجوی پیش فاکتور معتبر", description = "خواندن قرارداد و پیش فاکتورهای معتبر بر اساس شناسه")
	@GetMapping("/get-valid-proforma-by-master/{id}")
	public ResponseEntity<ProformaResponseDto> getValidProformaByMasterId(@PathVariable Long id) {
		return ResponseEntity.ok(proformaService.getActiveProformaById(id));
	}

	@Operation(summary = "جستجو بر اساس شناسه فرآیند", description = "خواندن پیش فاکتور بر اساس شناسه جریان کاری")
	@GetMapping("/get-by-instance/{instanceId}")
	public ResponseEntity<ProformaResponseDto> getByInstanceId(@PathVariable String instanceId) {
		return ResponseEntity.ok(proformaService.getDetailByInstanceId(instanceId));
	}

	// ==================== CREATE ====================

	@Operation(summary = "ایجاد پیش فاکتور", description = "ایجاد پیش فاکتور جدید در سیستم")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@PostMapping
	public ResponseEntity<?> create(@RequestBody PerfomaCreateRequest dto) {
		log.info("Creating proforma with tradeId: {}", dto.getTradeId());
		return ResponseEntity.ok(proformaService.create(dto));
	}


	@Operation(summary = "حذف پیش فاکتور", description = "حذف پیش فاکتورها بر اساس شناسه قرارداد اصلی")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
		proformaService.delete(id);
		return ResponseEntity.ok().build();
	}


	@Operation(
			summary = "ثبت فاکتور فروش نقدی",
			description = "ثبت فاکتور فروش نقدی جدید در سیستم"
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@PostMapping("/cash-sale")  // ← مسیر جدید برای رفع Ambiguous mapping
	public ResponseEntity<?> createCashSale(@RequestBody CashSaleCreateRequest dto) {
		log.info("Creating cash sale proforma with tradeId: {}", dto.getTradeId());
		return ResponseEntity.ok(cashSaleService.create(dto));
	}

	@Operation(summary = "ایجاد پیش فاکتور فلزات گرانبها", description = "ایجاد پیش فاکتور مخصوص کالاهای فلزات گرانبها")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@PostMapping("/precious-metal")
	public ResponseEntity<?> createPreciousMetal(@RequestBody PreciousMetalProfomaCreateRequest dto) {
		log.info("Creating precious metal proforma with tradeId: {}", dto.getTradeId());
		return ResponseEntity.ok(proformaContractPreciousMetalService.create(dto));
	}

	@Operation(summary = "ابطال پیش فاکتور", description = "شروع فرآیند ابطال پیش فاکتور")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
	@PostMapping("/create-reversal")
	public ResponseEntity<?> createReversal(@RequestBody PerformerCreateRevealRequest dto) {
		log.info("Creating reversal proforma with masterId: {}", dto.getMasterId());
		return ResponseEntity.ok(proformaService.createReversal(dto));
	}

	// ==================== UTILITY ====================

	@Operation(summary = "تشخیص فلز گرانبها", description = "بررسی می کند که آیا کالای مربوطه جزو فلزات گرانبها است یا خیر")
	@GetMapping("/is-precious-metal/{paymentCode}")
	public ResponseEntity<Boolean> isPreciousMetal(@PathVariable String paymentCode) {
		return ResponseEntity.ok(proformaService.isPreciousMetal(paymentCode));
	}

	@Operation(summary = "لیست انواع تسویه", description = "دریافت لیست انواع تسویه حساب موجود")
	@GetMapping("/get-settlement-type")
	public ResponseEntity<?> getSettlementType() {
		Map<String, Integer> settlementTypes = Arrays.stream(SettlementType.values())
				.collect(Collectors.toMap(Enum::name, SettlementType::getCode));
		return ResponseEntity.ok(settlementTypes);
	}

	@Operation(summary = "لیست انواع پیش فاکتور", description = "دریافت لیست انواع پیش فاکتور")
	@GetMapping("/get-proforma-type")
	public ResponseEntity<Map<String, String>> getProformaType() {
		Map<String, String> settlementTypes = Arrays.stream(ProformaIssueType.values())
				.collect(Collectors.toMap(Enum::name, ProformaIssueType::getValue));
		return ResponseEntity.ok(settlementTypes);
	}

	@Operation(summary = "بررسی قابلیت ابطال", description = "بررسی امکان شروع فرآیند ابطال پیش فاکتور")
	@GetMapping("/can-start-reversal/{masterId}")
	public ResponseEntity<Boolean> isReversal(@PathVariable Long masterId) {
		return ResponseEntity.ok(proformaService.canStartReversal(masterId));
	}

	@Operation(summary = "دریافت شماره لات", description = "دریافت شماره لات بر اساس شناسه معامله")
	@GetMapping("/get-lot-number/{tradeId}")
	public ResponseEntity<String> getLotNumberByTradeId(@PathVariable Long tradeId) {
		return ResponseEntity.ok(proformaService.getLotNumberByTradeId(tradeId));
	}

	// ==================== CANCELED & EDITED ====================

	@Operation(summary = "شماره های ابطال شده", description = "لیست شماره پیش فاکتورهای ابطال شده")
	@GetMapping("/get-canceled/{proformaId}")
	public ResponseEntity<List<String>> getCanceledProformaNo(@PathVariable Long proformaId) {
		return ResponseEntity.ok(proformaService.getCanceledProformaNo(proformaId));
	}

	@Operation(summary = "ابطال شده بر اساس قرارداد", description = "لیست پیش فاکتورهای ابطال شده بر اساس شماره قرارداد")
	@GetMapping("/get-canceled-by-contract/{contractNo}")
	public ResponseEntity<List<ProformaDetailDto.Info>> getCanceledByContractNo(@PathVariable Long contractNo) {
		return ResponseEntity.ok(proformaService.getCanceledByContractNo(contractNo));
	}

	@Operation(summary = "شماره های ویرایش شده", description = "لیست شماره پیش فاکتورهای ویرایش شده")
	@GetMapping("/get-edited/{proformaId}")
	public ResponseEntity<List<String>> getEditedProformaNo(@PathVariable Long proformaId) {
		return ResponseEntity.ok(proformaService.getEditedProformaNo(proformaId));
	}

	// ==================== EXPORT ====================

	@Operation(summary = "خروجی ورد", description = "خروجی پیش فاکتور به صورت فایل ورد")
	@GetMapping("/export-doc/{proformaId}")
	public ResponseEntity<byte[]> exportDoc(@PathVariable Long proformaId) {
		return ResponseEntity.ok(exportDocService.exportProformaDoc(proformaId));
	}

	@Operation(summary = "دانلود ورد", description = "خروجی پیش فاکتور به صورت فایل دانلودی ورد")
	@GetMapping(value = "/export-doc-file/{proformaId}", produces = "application/octet-stream")
	public ResponseEntity<byte[]> exportDocFile(@PathVariable Long proformaId) {
		byte[] response = exportDocService.exportProformaDoc(proformaId);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proforma_" + proformaId + ".docx\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(response.length)
				.body(response);
	}

	@Operation(summary = "دانلود پی دی اف", description = "خروجی پیش فاکتور به صورت فایل پی دی اف قابل دانلود")
	@GetMapping(value = "/export-pdf-file/{proformaId}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportPdfFile(@PathVariable Long proformaId) {
		try {
			byte[] docxBytes = exportDocService.exportProformaDoc(proformaId);
			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docxBytes));
			byte[] pdf = exportDocService.convertDocListToPdf(List.of(document));

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proforma_" + proformaId + ".pdf\"")
					.contentType(MediaType.APPLICATION_PDF)
					.contentLength(pdf.length)
					.body(pdf);
		} catch (Exception ex) {
			log.error("Error exporting PDF for proformaId: {}", proformaId, ex);
			throw new InternalSaleCustomException.FileContentException(ex.getMessage());
		}
	}

	@Operation(summary = "خروجی پی دی اف", description = "تبدیل پیش فاکتور به فرمت پی دی اف")
	@GetMapping(value = "/export-pdf/{proformaId}")
	public ResponseEntity<byte[]> exportPdf(@PathVariable Long proformaId) {
		try {
			byte[] docxBytes = exportDocService.exportProformaDoc(proformaId);
			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docxBytes));
			return ResponseEntity.ok(exportDocService.convertDocListToPdf(List.of(document)));
		} catch (IOException ex) {
			log.error("Error converting to PDF for proformaId: {}", proformaId, ex);
			throw new InternalSaleCustomException.FileContentException(ex.getMessage());
		}
	}

	// ==================== NOTIFICATIONS ====================

	@Operation(summary = "ارسال ایمیل", description = "ارسال ایمیل پیش فاکتور با فایل ضمیمه")
	@GetMapping("/send-proforma-email/{proformaMasterId}")
	public ResponseEntity<?> sendProformaFile(@PathVariable Long proformaMasterId) {
		notificationService.sendEmailWithProformaAttachment(proformaMasterId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "ارسال پیامک", description = "ارسال پیامک اطلاع رسانی پیش فاکتور")
	@GetMapping("/send-proforma-sms/{proformaMasterId}")
	public ResponseEntity<?> sendProformaSms(@PathVariable Long proformaMasterId) throws IOException {
		smsNotificationService.preFactorEmailedSMSNotification(proformaMasterId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}