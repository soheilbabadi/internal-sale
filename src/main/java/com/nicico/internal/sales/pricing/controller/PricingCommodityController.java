package com.nicico.internal.sales.pricing.controller;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pricing.dto.FieldMetadataDto;
import com.nicico.internal.sales.pricing.dto.PricingCommodityDto;
import com.nicico.internal.sales.pricing.dto.PricingCommodityWithAvgDto;
import com.nicico.internal.sales.pricing.service.PricingCommodityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_PRICING')")
@RequestMapping("/api/v1/ins/pricing/commodity")
@RequiredArgsConstructor

public class PricingCommodityController {

	private final PricingCommodityService service;

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PRICING')")
	@PostMapping("/save")
	@Operation(summary = "ثبت اطلاعات قیمت کالا", description = "ایجاد رکورد جدید قیمت کالا")
	public ResponseEntity<PricingCommodityDto.Info> save(@RequestBody PricingCommodityDto.Create request) {
		PricingCommodityDto.Info response = service.save(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/search")
	@Operation(summary = "جستجوی پیشرفته", description = "جستجوی قیمت کالاها با فیلترهای مختلف")
	public ResponseEntity<SearchDTO.SearchRs<PricingCommodityWithAvgDto.Info>> search(@RequestBody SearchDTO.SearchRq request) {
		SearchDTO.SearchRs<PricingCommodityWithAvgDto.Info> response = service.search(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "دریافت قیمت کالا با شناسه", description = "بازیابی رکورد قیمت کالا بر اساس شناسه")
	public ResponseEntity<PricingCommodityDto.Info> findById(@PathVariable Long id) {
		PricingCommodityDto.Info response = service.findById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/by-date")
	@Operation(summary = "دریافت قیمت کالا بر اساس تاریخ میلادی", description = "بازیابی رکورد قیمت کالا بر اساس تاریخ میلادی")
	public ResponseEntity<PricingCommodityDto.Info> findByShortDate(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		PricingCommodityDto.Info response = service.findByShortDate(date);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/by-persian-date")
	@Operation(summary = "دریافت قیمت کالا بر اساس تاریخ شمسی ی", description = "بازیابی رکورد قیمت کالا بر اساس تاریخ شمسی ی")
	public ResponseEntity<PricingCommodityDto.Info> findByPersianShortDate(@RequestParam String persianShortDate) {
		PricingCommodityDto.Info response = service.findByPersianShortDate(persianShortDate);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/by-date-range")
	@Operation(summary = "دریافت قیمت کالا در بازه زمانی", description = "بازیابی رکوردهای قیمت کالا در بازه زمانی مشخص")
	public ResponseEntity<List<PricingCommodityDto.Info>> findByDateRange(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		List<PricingCommodityDto.Info> response = service.findByDateRange(startDate, endDate);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/latest")
	@Operation(summary = "دریافت آخرین قیمت کالا", description = "بازیابی آخرین رکورد قیمت کالا")
	public ResponseEntity<PricingCommodityDto.Info> findLatest() {
		PricingCommodityDto.Info response = service.findLatest();
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "حذف قیمت کالا با شناسه", description = "حذف رکورد قیمت کالا بر اساس شناسه")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/by-date")
	@Operation(summary = "حذف قیمت کالا بر اساس تاریخ میلادی", description = "حذف رکورد قیمت کالا بر اساس تاریخ میلادی")
	public ResponseEntity<Void> deleteByShortDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date shortDate) {
		service.deleteByShortDate(shortDate);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exists/by-date")
	@Operation(summary = "بررسی وجود قیمت کالا بر اساس تاریخ میلادی", description = "بررسی وجود رکورد قیمت کالا بر اساس تاریخ میلادی")
	public ResponseEntity<Boolean> existsByShortDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date shortDate) {
		boolean exists = service.existsByShortDate(shortDate);
		return ResponseEntity.ok(exists);
	}

	@GetMapping("/exists/by-persian-date")
	@Operation(summary = "بررسی وجود قیمت کالا بر اساس تاریخ شمسی ی", description = "بررسی وجود رکورد قیمت کالا بر اساس تاریخ شمسی ی")
	public ResponseEntity<Boolean> existsByPersianShortDate(@RequestParam String persianShortDate) {
		boolean exists = service.existsByPersianShortDate(persianShortDate);
		return ResponseEntity.ok(exists);
	}


	@GetMapping("/average/molybdenum-high-low")
	@Operation(summary = "دریافت میانگین قیمت مولیبدن", description = "محاسبه میانگین قیمت مولیبدن (کمترین و بیشترین) در تاریخ مشخص")
	public ResponseEntity<BigDecimal> getAverageMolybdenumPrice(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		BigDecimal average = service.getAverageMolybdenumPrice(date);
		return ResponseEntity.ok(average);
	}

	@GetMapping("/average/molybdenum-am-pm")
	@Operation(summary = "دریافت میانگین قیمت مولیبدن (صبح و عصر)", description = "محاسبه میانگین قیمت مولیبدن (صبح و عصر) در تاریخ مشخص")
	public ResponseEntity<BigDecimal> getAverageMolybdenumAmPmPrice(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		BigDecimal average = service.getAverageMolybdenumAmPmPrice(date);
		return ResponseEntity.ok(average);
	}

	@GetMapping("/average/platinum")
	@Operation(summary = "دریافت میانگین قیمت پلاتین", description = "محاسبه میانگین قیمت پلاتین (صبح و عصر) در تاریخ مشخص")
	public ResponseEntity<BigDecimal> getAveragePlatinumPrice(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		BigDecimal average = service.getAveragePlatinumPrice(date);
		return ResponseEntity.ok(average);
	}

	@GetMapping("/average/palladium")
	@Operation(summary = "دریافت میانگین قیمت پالادیوم", description = "محاسبه میانگین قیمت پالادیوم (صبح و عصر) در تاریخ مشخص")
	public ResponseEntity<BigDecimal> getAveragePalladiumPrice(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		BigDecimal average = service.getAveragePalladiumPrice(date);
		return ResponseEntity.ok(average);
	}

	@GetMapping("/average/selenium")
	@Operation(summary = "دریافت میانگین قیمت سلنیوم", description = "محاسبه میانگین قیمت سلنیوم (تقاضا و عرضه) در تاریخ مشخص")
	public ResponseEntity<BigDecimal> getAverageSeleniumPrice(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		BigDecimal average = service.getAverageSeleniumPrice(date);
		return ResponseEntity.ok(average);
	}

	@GetMapping("/commoditiy-pricing-metadata")
	@Operation(
			summary = "دریافت متادیتای فیلدها",
			description = "لیست فیلدهای PricingCommodityDto به همراه توضیحات Schema"
	)
	public ResponseEntity<List<FieldMetadataDto>> getMetadata() {

		List<FieldMetadataDto> result = Arrays.stream(PricingCommodityDto.class.getDeclaredFields())
				.filter(field -> !java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				.map(service::toMetadata)
				.toList();

		return ResponseEntity.ok(result);
	}


}