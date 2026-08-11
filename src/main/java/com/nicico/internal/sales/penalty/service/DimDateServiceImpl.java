package com.nicico.internal.sales.penalty.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.penalty.dto.DimDateDto;
import com.nicico.internal.sales.penalty.dto.DimDateMapper;
import com.nicico.internal.sales.penalty.model.DimDateModel;
import com.nicico.internal.sales.penalty.repository.DimDateRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DimDateServiceImpl implements DimDateService {
	private final DimDateRepository dimDateRepository;
	private final DimDateMapper mapper;

	@Override
	public SearchDTO.SearchRs<DimDateDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(dimDateRepository, request, mapper::toDTO);
	}

	@Override
	public long countWorkingDays(Date startDate, Date finishDate) {
		// Convert Date to LocalDate for the repository method
		LocalDate start = DateUtility.toLocalDate(startDate);
		LocalDate finish = DateUtility.toLocalDate(finishDate);
		return dimDateRepository.countWorkingDaysBetween(start, finish);
	}

	@Override
	public DimDateDto getByDate(Date date) {
		return dimDateRepository.findAll().stream()
				.filter(dimDate -> isSameDay(dimDate.getShortDate(), date))
				.findFirst()
				.map(mapper::toDTO)
				.orElse(null);
	}

	@Override
	public DimDateDto getByPersianDate(String persianDate) {
		persianDate = persianDate.replace("-", "/");
		var result = dimDateRepository.findByPersianShortDate(persianDate).orElse(null);
		return mapper.toDTO(result);
	}

	public DimDateModel findByDate(Date date) {
		return dimDateRepository.findAll().stream()
				.filter(dimDate -> isSameDay(dimDate.getShortDate(), date))
				.findFirst()
				.orElse(null);
	}

	@Override
	public DimDateDto updateHolidayStatus(Date date, boolean isHoliday) {
		try {
			var model = findByDate(date);
			if (model != null) {
				model.setIsHoliday(isHoliday);
				dimDateRepository.save(model);
				return mapper.toDTO(model);
			}
			return null;
		} catch (Exception ex) {
			log.error("Error updating holiday status: {}", ex.getMessage());
			return null;
		}
	}

	@Override
	public List<DimDateDto.Info> getAllBetween(Date startDate, Date endDate) {
		var list = dimDateRepository.findAllByShortDateBetween(startDate, endDate);
		return list.stream().distinct().map(mapper::toDTO).toList();
	}

	public List<DimDateModel> getAllDimDates() {
		return dimDateRepository.findAll();
	}

	@Override
	public List<DimDateDto.Info> getHolidayByYear(int year) {
		var list = dimDateRepository.findHolidaysPerYear(year);
		return list.stream().distinct().map(mapper::toDTO).toList();
	}

	@Override
	public DimDateDto.Info addWorkingDays(Date date, int workingDays) {
		if (date == null || workingDays <= 0) {
			return mapper.toDTO(findByDate(date));
		}

		try {
			int addedDays = 0;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int maxIterations = 1000;
			int iterations = 0;

			while (addedDays < workingDays && iterations < maxIterations) {
				// Move to next day
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				iterations++;

				DimDateModel dimDate = findByDate(calendar.getTime());
				// Working day = isHoliday is false
				if (dimDate != null && !dimDate.getIsHoliday()) {
					addedDays++;
				} else if (dimDate == null) {
					addedDays++;
				}
			}

			return mapper.toDTO(findByDate(calendar.getTime()));
		} catch (Exception ex) {
			log.error("Error adding working days: {}", ex.getMessage());
			return null;
		}
	}

	@Override
	public DimDateDto.Info addSixWorkingDays(Date date) {
		return addWorkingDays(date, 6);
	}


	private boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public List<DimDateDto.Info> getDatesByPersianYearAndMonth(Long persianYear, Long persianMonth) {
		try {
			List<DimDateModel> dates = dimDateRepository.findByPersianYearAndPersianMonthOrderByPersianDayAsc(persianYear, persianMonth);
			return dates.stream().map(mapper::toDTO).toList();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return List.of();
		}
	}
}