package com.nicico.internal.sales.penalty.repository;

import com.nicico.internal.sales.penalty.model.DimDateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DimDateRepository extends JpaRepository<DimDateModel, Long>, JpaSpecificationExecutor<DimDateModel> {
	@Query(value = "SELECT ABS(COUNT(*)) FROM T_INS_DATES d WHERE d.IS_HOLIDAY = 0 AND d.SHORT_DATE BETWEEN :startDate AND :endDate", nativeQuery = true)
	long countWorkingDaysBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


	Optional<DimDateModel> findByPersianShortDate(String persianDate);

	List<DimDateModel> findAllByShortDateBetween(Date shortDate, Date shortDate2);

	@Query(value = "SELECT * FROM T_INS_DATES d WHERE d.IS_HOLIDAY = 1 AND d.PERSIAN_YEAR = :year ORDER BY d.SHORT_DATE", nativeQuery = true)
	List<DimDateModel> findHolidaysPerYear(@Param("year") int year);

	Optional<DimDateModel> findByPersianYearAndPersianMonthAndPersianDay(
			Long persianYear,
			Long persianMonth,
			Long persianDay
	);

	List<DimDateModel> findByPersianYearAndPersianMonthOrderByPersianDayAsc(Long persianYear, Long persianMonth);


}