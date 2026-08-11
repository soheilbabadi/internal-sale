package com.nicico.internal.sales.penalty.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.penalty.dto.DimDateDto;

import java.util.Date;
import java.util.List;

public interface DimDateService {
	SearchDTO.SearchRs<DimDateDto.Info> search(SearchDTO.SearchRq request);

	long countWorkingDays(Date startDate, Date finishDate);

	DimDateDto getByDate(Date date);

	DimDateDto getByPersianDate(String persianDate);

	DimDateDto updateHolidayStatus(Date date, boolean isHoliday);

	List<DimDateDto.Info> getAllBetween(Date startDate, Date endDate);

	List<DimDateDto.Info> getDatesByPersianYearAndMonth(Long persianYear, Long persianMonth);


	List<DimDateDto.Info> getHolidayByYear(int year);

	DimDateDto.Info addWorkingDays(Date date, int workingDays);

	DimDateDto.Info addSixWorkingDays(Date date);
}