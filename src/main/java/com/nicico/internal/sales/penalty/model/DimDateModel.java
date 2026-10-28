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
	@Column(name = "id")
	private Long id;

	@Column(name = "SHORT_DATE")
	private Date shortDate;
	@Column(name = "LONG_DATE")
	private String longDate;

	@Column(name = "DAY_OF_WEEK")
	private String dayOfWeek;

	@Column(name = "MONTH_NAME")
	private String monthName;

	@Column(name = "DAY_IN_YEAR")
	private Long dayInYear;

	@Column(name = "WEEK_IN_YEAR")
	private Long weekInYear;

	@Column(name = "GREGORIAN_MONTH")
	private Long gregorianMonth;

	@Column(name = "GREGORIAN_DAYS_IN_MONTH")
	private Long gregorianDaysInMonth;

	@Column(name = "GREGORIAN_DAY")
	private Long gregorianDay;

	@Column(name = "QUARTER")
	private Long quarter;

	@Column(name = "GREGORIAN_YEAR")
	private Long gregorianYear;
	@Size(max = 255)

	@Column(name = "PERSIAN_SHORT_DATE")
	private String persianShortDate;

	@Column(name = "PERSIAN_LONG_DATE")
	private String persianLongDate;

	@Column(name = "PERSIAN_DAY")
	private Long persianDay;
	@Column(name = "PERSIAN_MONTH")
	private Long persianMonth;

	@Column(name = "PERSIAN_YEAR")
	private Long persianYear;

	@Column(name = "PERSIAN_WEEKDAY_NAME", length = 50)
	private String persianWeekdayName;
	@Column(name = "PERSIAN_MONTH_NAME", length = 50)
	private String persianMonthName;

	@Column(name = "IS_LEAP_YEAR")
	private Long isLeapYear;
	@Column(name = "HIJRI_SHORT_DATE", length = 50)
	private String hijriShortDate;
	@Column(name = "HIJRI_LONG_DATE", length = 50)
	private String hijriLongDate;

	@Column(name = "HIJRI_DAY")
	private Long hijriDay;

	@Column(name = "HIJRI_MONTH")
	private Long hijriMonth;

	@Column(name = "HIJRI_YEAR")
	private Long hijriYear;
	@Column(name = "HIJRI_MONTH_NAME", length = 50)
	private String hijriMonthName;
	@Column(name = "IS_WEEKEND")
	private Boolean isWeekend;

	@Column(name = "IS_HOLIDAY")
	private Boolean isHoliday = false;

}