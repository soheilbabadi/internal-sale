package com.nicico.internal.sales.penalty.controller;


import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.penalty.dto.DimDateDto;
import com.nicico.internal.sales.penalty.service.DimDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/dim-date")
public class DimDateController {
	private final DimDateService dimDateService;

	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<DimDateDto.Info>> search(@RequestBody SearchDTO.SearchRq request) {
		return ResponseEntity.ok(dimDateService.search(request));
	}

	@GetMapping("/between")
	public ResponseEntity<List<DimDateDto.Info>> getBetween(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		return ResponseEntity.ok(dimDateService.getAllBetween(startDate, endDate));
	}

	@GetMapping("/count-working-days")
	public ResponseEntity<Long> countWorkingDays(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		return ResponseEntity.ok(dimDateService.countWorkingDays(startDate, endDate));
	}

	@GetMapping("/day-detail")
	public ResponseEntity<DimDateDto> getDayDetail(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		return ResponseEntity.ok(dimDateService.getByDate(date));
	}

	@GetMapping("/get-by-persian-date/{persianDate}")
	public ResponseEntity<DimDateDto> getDayDetailByPersian(@PathVariable String persianDate) {
		return ResponseEntity.ok(dimDateService.getByPersianDate(persianDate));
	}

	@PutMapping("/set-holiday")
	public ResponseEntity<DimDateDto> setHoliday(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		return ResponseEntity.ok(dimDateService.updateHolidayStatus(date, true));
	}

	@PutMapping("/set-working-day")
	public ResponseEntity<DimDateDto> setWorkingDay(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		return ResponseEntity.ok(dimDateService.updateHolidayStatus(date, false));
	}

	@GetMapping("/get-holidays-by-year/{year}")
	public ResponseEntity<List<DimDateDto.Info>> getHolidaysByYear(@PathVariable Integer year) {
		return ResponseEntity.ok(dimDateService.getHolidayByYear(year));
	}

	// Add 6 working days - returns DimDateDto.Info
	@GetMapping("/add-six-working-days")
	public ResponseEntity<DimDateDto.Info> addSixWorkingDays(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate) {
		return ResponseEntity.ok(dimDateService.addSixWorkingDays(startDate));
	}

	@GetMapping("/add-working-days")
	public ResponseEntity<DimDateDto.Info> addWorkingDays(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam int workingDays) {
		return ResponseEntity.ok(dimDateService.addWorkingDays(startDate, workingDays));

	}

	@GetMapping("/is-working-day")
	public ResponseEntity<Map<String, Boolean>> isWorkingDay(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		DimDateDto dayDetail = dimDateService.getByDate(date);

		Map<String, Boolean> response = new HashMap<>();
		response.put("isWorkingDay", dayDetail != null && !dayDetail.getIsHoliday());
		response.put("isHoliday", dayDetail != null && dayDetail.getIsHoliday());
		response.put("isWeekend", dayDetail != null && dayDetail.getIsWeekend() != null && dayDetail.getIsWeekend());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/working-days-between")
	public ResponseEntity<Long> getWorkingDaysBetween(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

		return ResponseEntity.ok(dimDateService.countWorkingDays(startDate, endDate));
	}

	@GetMapping("/by-persian-year-month")
	public ResponseEntity<List<DimDateDto.Info>> getDatesByPersianYearAndMonth(@RequestParam Long persianYear, @RequestParam Long persianMonth) {

		List<DimDateDto.Info> dates = dimDateService.getDatesByPersianYearAndMonth(persianYear, persianMonth);
		if (dates != null && !dates.isEmpty()) {
			return ResponseEntity.ok(dates);
		} else {
			return ResponseEntity.noContent().build();
		}
	}
}