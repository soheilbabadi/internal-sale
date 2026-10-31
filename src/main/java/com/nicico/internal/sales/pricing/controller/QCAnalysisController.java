package com.nicico.internal.sales.pricing.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pricing.dto.QCAnalysisItemDto;
import com.nicico.internal.sales.pricing.dto.QCAnalysisMasterDto;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStage;
import com.nicico.internal.sales.pricing.service.QCAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/ins/qc-analysis")
@PreAuthorize("@secUtil.hasAuthority('R_INS_PRICING')")
@RequiredArgsConstructor
@Tag(name = "QC Analysis", description = "مدیریت و ثبت نامه‌های آنالیز کنترل کیفیت و آزمایشگاه (قبل عرضه، بعد عرضه و اصلاحی)")
public class QCAnalysisController {

	private final QCAnalysisService service;

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PRICING')")
	@PostMapping("/save")
	@Operation(summary = "ثبت یا ویرایش نامه آنالیز آزمایشگاه", description = "ایجاد یا به‌روزرسانی مستر نامه و ردیف‌های عیار به صورت تراکنشی")
	public ResponseEntity<QCAnalysisMasterDto.Info> save(@Valid @RequestBody QCAnalysisMasterDto.SaveRequest request) {
		QCAnalysisMasterDto.Info response = service.save(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/search")
	@Operation(summary = "جستجوی پیشرفته گرید آنالیز", description = "جستجوی نامه‌ها بر اساس کالا، تاریخ، شماره اندیکاتور و مرحله آنالیز")
	public ResponseEntity<SearchDTO.SearchRs<QCAnalysisMasterDto.Info>> search(
			@RequestBody(required = false) SearchDTO.SearchRq request,
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			request = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}
		SearchDTO.SearchRs<QCAnalysisMasterDto.Info> response = service.search(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "دریافت جزئیات نامه با شناسه", description = "بازیابی اطلاعات مستر نامه به همراه ردیف‌های عیار و میانگین‌ها")
	public ResponseEntity<QCAnalysisMasterDto.Info> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping("/by-indicator")
	@Operation(summary = "دریافت نامه بر اساس شماره اندیکاتور", description = "جستجوی نامه آزمایشگاه بر اساس شماره اندیکاتور ثبت شده")
	public ResponseEntity<QCAnalysisMasterDto.Info> findByIndicatorNo(@RequestParam String indicatorNo) {
		return ResponseEntity.ok(service.findByIndicatorNo(indicatorNo));
	}

	@GetMapping("/parent-options")
	@Operation(summary = "گزینه‌های کمبوباکس نامه مرجع (والد)", description = "واکشی نامه‌های معتبر جهت انتخاب در فرم بعد عرضه یا اصلاحی بر اساس نوع کالا")
	public ResponseEntity<List<QCAnalysisMasterDto.ParentOption>> getParentOptions(
			@Parameter(description = "شناسه کالا", required = true) @RequestParam Long commodityId,
			@Parameter(description = "مرحله جاری (POST_OFFER یا CORRECTION)", required = true) @RequestParam QCAnalysisStage stage) {
		return ResponseEntity.ok(service.getParentOptions(commodityId, stage));
	}

	@GetMapping("/{id}/parent-items")
	@Operation(summary = "آیتم‌های نامه والد برای مقداردهی اولیه", description = "دریافت ردیف‌های عیار و بشکه‌های نامه مرجع برای پر کردن خودکار فرم بعد عرضه یا اصلاحی")
	public ResponseEntity<List<QCAnalysisItemDto>> getParentItems(@PathVariable Long id) {
		return ResponseEntity.ok(service.getParentItems(id));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PRICING')")
	@DeleteMapping("/{id}")
	@Operation(summary = "حذف نامه آنالیز", description = "حذف نامه در صورت عدم استفاده در مراحل بعدی")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	// ==========================================
	// تفکیک عملیات CRUD برای ردیف‌ها و آیتم‌ها (Detail Items)
	// ==========================================

	@GetMapping("/items/{itemId}")
	@Operation(summary = "دریافت یک آیتم عیار با شناسه", description = "مشاهده جزئیات یک ردیف عیار یا بشکه")
	public ResponseEntity<QCAnalysisItemDto> findItemById(@PathVariable Long itemId) {
		return ResponseEntity.ok(service.findItemById(itemId));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PRICING')")
	@PostMapping("/{masterId}/items")
	@Operation(summary = "افزودن ردیف عیار جدید به یک مستر", description = "ایجاد یک آیتم جدید برای نامه مستر مشخص")
	public ResponseEntity<QCAnalysisItemDto> createItem(@PathVariable Long masterId, @Valid @RequestBody QCAnalysisItemDto itemDto) {
		QCAnalysisItemDto created = service.saveItem(masterId, itemDto);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@PreAuthorize("@secUtil.hasAuthority('U_INS_PRICING')")
	@PutMapping("/items/{itemId}")
	@Operation(summary = "ویرایش ردیف عیار", description = "به‌روزرسانی مقادیر و عیارهای یک ردیف مشخص")
	public ResponseEntity<QCAnalysisItemDto> updateItem(@PathVariable Long itemId, @Valid @RequestBody QCAnalysisItemDto itemDto) {
		QCAnalysisItemDto updated = service.updateItem(itemId, itemDto);
		return ResponseEntity.ok(updated);
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PRICING')")
	@DeleteMapping("/items/{itemId}")
	@Operation(summary = "حذف ردیف عیار", description = "حذف یک ردیف مشخص از نامه مستر")
	public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
		service.deleteItem(itemId);
		return ResponseEntity.noContent().build();
	}
}
