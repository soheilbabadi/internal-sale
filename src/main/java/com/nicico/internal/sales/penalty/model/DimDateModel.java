package com.nicico.internal.sales.penalty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "T_INS_DATES")
public class DimDateModel implements Serializable {
	@Serial
	private static final long serialVersionUID = -5179948276361472410L;
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "SHORT_DATE", nullable = false)
	private Date shortDate;


	@Column(name = "LONG_DATE", nullable = false)
	private String longDate;

	@Column(name = "DAY_OF_WEEK", nullable = false)
	private String dayOfWeek;

	@Column(name = "MONTH_NAME", nullable = false)
	private String monthName;

	@Column(name = "DAY_IN_YEAR", nullable = false)
	private Long dayInYear;

	@Column(name = "WEEK_IN_YEAR", nullable = false)
	private Long weekInYear;

	@Column(name = "GREGORIAN_MONTH", nullable = false)
	private Long gregorianMonth;

	@Column(name = "GREGORIAN_DAYS_IN_MONTH", nullable = false)
	private Long gregorianDaysInMonth;

	@Column(name = "GREGORIAN_DAY", nullable = false)
	private Long gregorianDay;

	@Column(name = "QUARTER", nullable = false)
	private Long quarter;

	@Column(name = "GREGORIAN_YEAR", nullable = false)
	private Long gregorianYear;
	@Size(max = 255)

	@Column(name = "PERSIAN_SHORT_DATE", nullable = false)
	private String persianShortDate;

	@Column(name = "PERSIAN_LONG_DATE", nullable = false)
	private String persianLongDate;

	@Column(name = "PERSIAN_DAY", nullable = false)
	private Long persianDay;
	@Column(name = "PERSIAN_MONTH")
	private Long persianMonth;

	@Column(name = "PERSIAN_YEAR", nullable = false)
	private Long persianYear;

	@Column(name = "PERSIAN_WEEKDAY_NAME", nullable = false, length = 50)
	private String persianWeekdayName;


	@Column(name = "PERSIAN_MONTH_NAME", nullable = false, length = 50)
	private String persianMonthName;

	@Column(name = "IS_LEAP_YEAR", nullable = false)
	private Long isLeapYear;


	@Column(name = "HIJRI_SHORT_DATE", nullable = false, length = 50)
	private String hijriShortDate;


	@Column(name = "HIJRI_LONG_DATE", nullable = false, length = 50)
	private String hijriLongDate;

	@Column(name = "HIJRI_DAY", nullable = false)
	private Long hijriDay;

	@Column(name = "HIJRI_MONTH", nullable = false)
	private Long hijriMonth;

	@Column(name = "HIJRI_YEAR", nullable = false)
	private Long hijriYear;


	@Column(name = "HIJRI_MONTH_NAME", nullable = false, length = 50)
	private String hijriMonthName;
	@Column(name = "IS_WEEKEND")
	private Boolean isWeekend;

	@Column(name = "IS_HOLIDAY", nullable = false)
	private Boolean isHoliday = false;

}