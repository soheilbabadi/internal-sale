package com.nicico.internal.sales.penalty.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DimDateDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -7514146817354502099L;

	private Long id;
	private Date shortDate;
	private String longDate;
	private String dayOfWeek;
	private String monthName;
	private Long dayInYear;
	private Long weekInYear;
	private Long gregorianMonth;
	private Long gregorianDaysInMonth;
	private Long gregorianDay;
	private Long quarter;
	private Long gregorianYear;
	private String persianShortDate;
	private String persianLongDate;
	private Long persianDay;
	private Long persianMonth;
	private Long persianYear;
	private String persianWeekdayName;
	private String persianMonthName;
	private Long isLeapYear;
	private String hijriShortDate;
	private String hijriLongDate;
	private Long hijriDay;
	private Long hijriMonth;
	private Long hijriYear;
	private String hijriMonthName;
	private Boolean isWeekend;
	private Boolean isHoliday = false;
	private String persianSeason;
	private Long persianHalfYear;
	private Long persianSeasonId;
	private Boolean isWorkDay = false;
	private Long persianWeekdayId;
	private Long monthId;
	private Long daysInMonth;
	private Long dayOfMonth;
	private Long daysInYear;
	private String shortPersianDate;
	private String longPersianDate;
	private String persianWeekday;

	public static class Info extends DimDateDto {
		@Serial
		private static final long serialVersionUID = -2281761574154274220L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("DimDateDto.Create")
	public static class Create extends DimDateDto {
		@Serial
		private static final long serialVersionUID = 7221994728926672403L;
	}
}