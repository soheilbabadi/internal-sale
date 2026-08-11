package com.nicico.internal.sales.util.date;

import lombok.Getter;

import java.io.Serial;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public class Jalali extends Calendar {

	public static final int AD = 1;
	static final int BCE = 0;
	static final int CE = 1;

	private static final int ONE_SECOND = 1_000;
	private static final int ONE_MINUTE = 60 * ONE_SECOND;
	private static final int ONE_HOUR = 60 * ONE_MINUTE;
	private static final long ONE_DAY = 24 * ONE_HOUR;

	static final int[] MIN_VALUES = {
			BCE,             // ERA
			100,             // YEAR
			1,               // MONTH
			1,               // WEEK_OF_YEAR
			0,               // WEEK_OF_MONTH
			1,               // DAY_OF_MONTH
			1,               // DAY_OF_YEAR
			7,               // DAY_OF_WEEK
			1,               // DAY_OF_WEEK_IN_MONTH
			AM,              // AM_PM
			0,               // HOUR
			0,               // HOUR_OF_DAY
			0,               // MINUTE
			0,               // SECOND
			0,               // MILLISECOND
			-13 * ONE_HOUR,  // ZONE_OFFSET
			0                // DST_OFFSET
	};

	static final int[] MAX_VALUES = {
			CE,              // ERA
			3000,            // YEAR
			11,              // MONTH
			53,              // WEEK_OF_YEAR
			6,               // WEEK_OF_MONTH
			31,              // DAY_OF_MONTH
			366,             // DAY_OF_YEAR
			6,               // DAY_OF_WEEK
			6,               // DAY_OF_WEEK_IN_MONTH
			PM,              // AM_PM
			11,              // HOUR
			23,              // HOUR_OF_DAY
			59,              // MINUTE
			59,              // SECOND
			999,             // MILLISECOND
			14 * ONE_HOUR,   // ZONE_OFFSET
			ONE_HOUR         // DST_OFFSET
	};

	private static final int[] GREGORIAN_DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private static final int[] JALALI_DAYS_IN_MONTH = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

	private static final String[] MONTH_NAMES = {
			"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
			"مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
	};
	private static final String[] WEEK_DAY_NAMES = {
			"شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه"
	};

	@Serial
	private static final long serialVersionUID = -570906057422128282L;

	private TimeZone timeZone;
	private boolean isSetTime = false;
	private GregorianCalendar gregorianCalendar;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public Jalali() {
		this(TimeZone.getDefault(), Locale.getDefault());
	}

	public Jalali(TimeZone zone) {
		this(zone, Locale.getDefault());
	}

	public Jalali(Locale aLocale) {
		this(TimeZone.getDefault(), aLocale);
	}

	public Jalali(TimeZone zone, Locale aLocale) {
		super(zone, aLocale);
		this.timeZone = zone;
		Calendar calendar = Calendar.getInstance(zone, aLocale);
		YearMonthDay ymd = toJalali(new YearMonthDay(
				calendar.get(YEAR), calendar.get(MONTH), calendar.get(DATE)));
		set(ymd.year, ymd.month, ymd.day);
		complete();
	}

	public Jalali(int year, int month, int dayOfMonth) {
		this(year, month, dayOfMonth, 0, 0, 0, 0);
	}

	public Jalali(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		this(year, month, dayOfMonth, hourOfDay, minute, 0, 0);
	}

	public Jalali(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		this(year, month, dayOfMonth, hourOfDay, minute, second, 0);
	}

	public Jalali(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millis) {
		super();
		set(YEAR, year);
		set(MONTH, month);
		set(DAY_OF_MONTH, dayOfMonth);
		setHourFields(hourOfDay);
		set(MINUTE, minute);
		set(SECOND, second);
		set(MILLISECOND, millis);

		YearMonthDay gregorianDate = toGregorian(new YearMonthDay(fields[1], fields[2], fields[5]));
		gregorianCalendar = new GregorianCalendar(
				gregorianDate.year, gregorianDate.month, gregorianDate.day,
				hourOfDay, minute, second);
		time = gregorianCalendar.getTimeInMillis();
		isSetTime = true;
	}

	// -------------------------------------------------------------------------
	// Core conversion — static, non-mutating
	// -------------------------------------------------------------------------

	/**
	 * Converts a Gregorian YearMonthDay (month 0-based) to a Jalali YearMonthDay.
	 * The input object is NOT mutated.
	 */
	public static YearMonthDay toJalali(YearMonthDay gregorian) {
		validateMonth(gregorian.month);

		int gy = gregorian.year - 1600;
		int gm = gregorian.month;
		int gd = gregorian.day - 1;

		int gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400;
		for (int i = 0; i < gm; i++) {
			gDayNo += GREGORIAN_DAYS_IN_MONTH[i];
		}
		if (gm > 1 && isGregorianLeapYear(gy)) {
			gDayNo++;
		}
		gDayNo += gd;

		int jDayNo = gDayNo - 79;
		int jNP = jDayNo / 12053;
		jDayNo = jDayNo % 12053;

		int jYear = 979 + 33 * jNP + 4 * (jDayNo / 1461);
		jDayNo = jDayNo % 1461;

		if (jDayNo >= 366) {
			jYear += (jDayNo - 1) / 365;
			jDayNo = (jDayNo - 1) % 365;
		}

		int jMonth = 0;
		for (; jMonth < 11 && jDayNo >= JALALI_DAYS_IN_MONTH[jMonth]; jMonth++) {
			jDayNo -= JALALI_DAYS_IN_MONTH[jMonth];
		}
		int jDay = jDayNo + 1;

		return new YearMonthDay(jYear, jMonth, jDay);
	}

	/**
	 * Converts a Jalali YearMonthDay (month 0-based) to a Gregorian YearMonthDay.
	 * The input object is NOT mutated.
	 */
	public static YearMonthDay toGregorian(YearMonthDay jalali) {
		validateMonth(jalali.month);

		int jy = jalali.year - 979;
		int jd = jalali.day - 1;

		int jDayNo = 365 * jy + (jy / 33) * 8 + ((jy % 33) + 3) / 4;
		for (int i = 0; i < jalali.month; i++) {
			jDayNo += JALALI_DAYS_IN_MONTH[i];
		}
		jDayNo += jd;

		int gDayNo = jDayNo + 79;
		int gYear = 1600 + 400 * (gDayNo / 146097);
		gDayNo = gDayNo % 146097;

		int leap = 1;
		if (gDayNo >= 36525) {
			gDayNo--;
			gYear += 100 * (gDayNo / 36524);
			gDayNo = gDayNo % 36524;
			if (gDayNo >= 365) gDayNo++;
			else leap = 0;
		}

		gYear += 4 * (gDayNo / 1461);
		gDayNo = gDayNo % 1461;

		if (gDayNo >= 366) {
			leap = 0;
			gDayNo--;
			gYear += gDayNo / 365;
			gDayNo = gDayNo % 365;
		}

		int gMonth = 0;
		for (; gDayNo >= GREGORIAN_DAYS_IN_MONTH[gMonth] + (gMonth == 1 && leap == 1 ? 1 : 0); gMonth++) {
			gDayNo -= GREGORIAN_DAYS_IN_MONTH[gMonth] + (gMonth == 1 && leap == 1 ? 1 : 0);
		}
		int gDay = gDayNo + 1;

		return new YearMonthDay(gYear, gMonth, gDay);
	}

	public static YearMonthDay toJalali(LocalDate gregorian) {
		return toJalali(new YearMonthDay(
				gregorian.getYear(), gregorian.getMonthValue() - 1, gregorian.getDayOfMonth()));
	}

	public static LocalDate toGregorianDate(YearMonthDay jalali) {
		YearMonthDay g = toGregorian(jalali);
		return LocalDate.of(g.year, g.month + 1, g.day);
	}

	// -------------------------------------------------------------------------
	// Factory / parse helpers
	// -------------------------------------------------------------------------

	/**
	 * Parses a Jalali date string in {@code yyyy/MM/dd} format.
	 */
	public static YearMonthDay parseJalali(String jalaliDate) {
		String[] parts = jalaliDate.split("/");
		if (parts.length != 3) {
			throw new IllegalArgumentException(
					"Invalid Jalali date format. Expected yyyy/MM/dd, got: " + jalaliDate);
		}
		return new YearMonthDay(
				Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1]) - 1,   // convert to 0-based
				Integer.parseInt(parts[2]));
	}

	/**
	 * Converts a {@link Date} to its Jalali YearMonthDay representation (UTC).
	 */
	public static YearMonthDay fromDate(Date date) {
		Objects.requireNonNull(date, "date must not be null");
		LocalDate localDate = date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
		return toJalali(localDate);
	}

	/**
	 * Converts a {@link LocalDate} to its Jalali YearMonthDay representation.
	 */
	public static YearMonthDay fromLocalDate(LocalDate date) {
		Objects.requireNonNull(date, "date must not be null");
		return toJalali(date);
	}

	// -------------------------------------------------------------------------
	// Calendar arithmetic (instance methods — delegate to LocalDate)
	// -------------------------------------------------------------------------

	public static int lengthOfMonth(YearMonthDay jalaliDate) {
		if (jalaliDate.month < 6) return 31;
		if (jalaliDate.month < 11) return 30;
		return isLeapYear(jalaliDate.year) ? 30 : 29;
	}

	public static int lengthOfYear(YearMonthDay jalaliDate) {
		return isLeapYear(jalaliDate.year) ? 366 : 365;
	}

	public static boolean isLeapYear(int year) {
		int r = year % 33;
		return r == 1 || r == 5 || r == 9 || r == 13 || r == 17 || r == 22 || r == 26 || r == 30;
	}

	private static boolean isGregorianLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}

	public static String toLongString(YearMonthDay jalaliDate) {
		return String.format("%s %d %s %d",
				getDayOfWeekName(jalaliDate), jalaliDate.day,
				getMonthName(jalaliDate), jalaliDate.year);
	}

	public static String getDayOfWeekName(YearMonthDay jalaliDate) {
		return WEEK_DAY_NAMES[getDayOfWeek(jalaliDate) - 1];
	}

	public static String getMonthName(YearMonthDay jalaliDate) {
		return MONTH_NAMES[jalaliDate.month];
	}

	// -------------------------------------------------------------------------
	// Calendar metadata helpers
	// -------------------------------------------------------------------------

	/**
	 * Returns the day-of-week (1=Saturday … 7=Friday) for a Jalali date.
	 */
	public static int getDayOfWeek(YearMonthDay jalaliDate) {
		return toGregorianDate(jalaliDate)
				.getDayOfWeek()
				.getValue();   // 1=Monday … 7=Sunday (ISO), kept for backward-compat
	}

	public static int weekOfYear(int dayOfYear, int year) {
		YearMonthDay firstDay = new YearMonthDay(year, 0, 1);
		int firstDayOfWeek = getDayOfWeek(firstDay);
		dayOfYear += (firstDayOfWeek - 2) % 7;
		return (dayOfYear / 7) + 1;
	}

	private static void validateMonth(int month) {
		if (month < 0 || month > 11) {
			throw new IllegalArgumentException(
					"Month must be between 0 and 11, got: " + month);
		}
	}

	public YearMonthDay addDays(YearMonthDay jalaliDate, int daysToAdd) {
		return toJalali(toGregorianDate(jalaliDate).plusDays(daysToAdd));
	}

	// -------------------------------------------------------------------------
	// Display helpers
	// -------------------------------------------------------------------------

	public YearMonthDay addMonths(YearMonthDay jalaliDate, int monthsToAdd) {
		return toJalali(toGregorianDate(jalaliDate).plusMonths(monthsToAdd));
	}

	public YearMonthDay addWeeks(YearMonthDay jalaliDate, int weeksToAdd) {
		return toJalali(toGregorianDate(jalaliDate).plusWeeks(weeksToAdd));
	}

	public YearMonthDay addYears(YearMonthDay jalaliDate, int yearsToAdd) {
		return toJalali(toGregorianDate(jalaliDate).plusYears(yearsToAdd));
	}

	public boolean isAfter(YearMonthDay d1, YearMonthDay d2) {
		return toGregorianDate(d1).isAfter(toGregorianDate(d2));
	}

	public boolean isBefore(YearMonthDay d1, YearMonthDay d2) {
		return toGregorianDate(d1).isBefore(toGregorianDate(d2));
	}

	// -------------------------------------------------------------------------
	// Calendar overrides
	// -------------------------------------------------------------------------

	public boolean isEqual(YearMonthDay d1, YearMonthDay d2) {
		return toGregorianDate(d1).isEqual(toGregorianDate(d2));
	}

	@Override
	protected void computeTime() {
		if (!isTimeSet && !isSetTime) {
			Calendar cal = GregorianCalendar.getInstance(timeZone);
			setDefaultFieldsIfNotSet(cal);
			setHourFields(internalGet(HOUR_OF_DAY));
			YearMonthDay greg = toGregorian(
					new YearMonthDay(internalGet(YEAR), internalGet(MONTH), internalGet(DAY_OF_MONTH)));
			gregorianCalendar = new GregorianCalendar(
					greg.year, greg.month, greg.day,
					internalGet(HOUR_OF_DAY), internalGet(MINUTE), internalGet(SECOND));
			time = gregorianCalendar.getTimeInMillis();

		} else if (!isTimeSet) {
			setHourFields(internalGet(HOUR_OF_DAY));
			gregorianCalendar = new GregorianCalendar();
			set(ZONE_OFFSET, timeZone.getRawOffset());
			set(DST_OFFSET, timeZone.getDSTSavings());
			YearMonthDay greg = toGregorian(
					new YearMonthDay(internalGet(YEAR), internalGet(MONTH), internalGet(DAY_OF_MONTH)));
			gregorianCalendar.set(greg.year, greg.month, greg.day,
					internalGet(HOUR_OF_DAY), internalGet(MINUTE), internalGet(SECOND));
			time = gregorianCalendar.getTimeInMillis();
		}
	}

	@Override
	protected void computeFields() {
		boolean temp = isTimeSet;
		if (!areFieldsSet) {
			setMinimalDaysInFirstWeek(1);
			setFirstDayOfWeek(7);

			int dayOfYear = 0;
			for (int i = 0; i < fields[2]; i++) {
				dayOfYear += JALALI_DAYS_IN_MONTH[i];
			}
			dayOfYear += fields[5];

			set(DAY_OF_YEAR, dayOfYear);
			set(DAY_OF_WEEK, getDayOfWeek(new YearMonthDay(fields[1], fields[2], fields[5])));
			set(DAY_OF_WEEK_IN_MONTH, (fields[5] - 1) / 7 + 1);
			set(WEEK_OF_YEAR, weekOfYear(fields[6], fields[1]));
			set(WEEK_OF_MONTH,
					weekOfYear(fields[6], fields[1]) - weekOfYear(fields[6] - fields[5], fields[1]) + 1);
			isTimeSet = temp;
		}
	}

	@Override
	public void add(int field, int amount) {
		if (field == YEAR || field == MONTH || field == DAY_OF_MONTH) {
			YearMonthDay jd = new YearMonthDay(fields[YEAR], fields[MONTH], fields[DAY_OF_MONTH]);
			jd = switch (field) {
				case YEAR -> addYears(jd, amount);
				case MONTH -> addMonths(jd, amount);
				default -> addDays(jd, amount);   // DAY_OF_MONTH
			};
			super.set(YEAR, jd.year);
			super.set(MONTH, jd.month);
			super.set(DAY_OF_MONTH, jd.day);
		}
		complete();
	}

	@Override
	public void roll(int field, boolean up) {
		roll(field, up ? +1 : -1);
	}

	@Override
	public void roll(int field, int amount) {
		if (field == YEAR || field == MONTH || field == DAY_OF_MONTH) {
			YearMonthDay jd = new YearMonthDay(fields[YEAR], fields[MONTH], fields[DAY_OF_MONTH]);
			jd = switch (field) {
				case MONTH -> addMonths(jd, amount);
				case DAY_OF_MONTH -> addDays(jd, amount);
				default -> addYears(jd, amount);
			};
			super.set(YEAR, jd.year);
			super.set(MONTH, jd.month);
			super.set(DAY_OF_MONTH, jd.day);
		}
	}

	@Override
	public void set(int field, int value) {
		switch (field) {
			case YEAR -> {
				fields[YEAR] = value;
				areFieldsSet = false;
			}
			case MONTH -> {
				fields[MONTH] = value;
				areFieldsSet = false;
			}
			case DAY_OF_MONTH -> {
				fields[DAY_OF_MONTH] = value;
				areFieldsSet = false;
			}
			default -> super.set(field, value);
		}
	}

	@Override
	public int getMinimum(int field) {
		return MIN_VALUES[field];
	}

	@Override
	public int getMaximum(int field) {
		return MAX_VALUES[field];
	}

	@Override
	public int getGreatestMinimum(int field) {
		return MIN_VALUES[field];
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	@Override
	public int getLeastMaximum(int field) {
		return MAX_VALUES[field];
	}

	/**
	 * Sets HOUR, HOUR_OF_DAY, and AM_PM consistently from a 0-23 hour value.
	 */
	private void setHourFields(int hourOfDay) {
		set(HOUR_OF_DAY, hourOfDay);
		if (hourOfDay >= 12) {
			set(AM_PM, PM);
			set(HOUR, hourOfDay - 12);
		} else {
			set(AM_PM, AM);
			set(HOUR, hourOfDay);
		}
	}

	private void setDefaultFieldsIfNotSet(Calendar cal) {
		int[] fieldsToCheck = {HOUR_OF_DAY, HOUR, MINUTE, SECOND, MILLISECOND, ZONE_OFFSET, DST_OFFSET, AM_PM};
		int[] defaultValues = {
				cal.get(HOUR_OF_DAY), cal.get(HOUR), cal.get(MINUTE), cal.get(SECOND),
				cal.get(MILLISECOND), cal.get(ZONE_OFFSET), cal.get(DST_OFFSET), cal.get(AM_PM)
		};
		for (int i = 0; i < fieldsToCheck.length; i++) {
			if (!isSet(fieldsToCheck[i])) {
				set(fieldsToCheck[i], defaultValues[i]);
			}
		}
	}

	// -------------------------------------------------------------------------
	// Value object
	// -------------------------------------------------------------------------

	@Getter
	public static class YearMonthDay {

		private int year;
		private int month;  // 0-based (0 = Farvardin)
		private int day;

		public YearMonthDay(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

		/**
		 * Parses a Jalali date string in {@code yyyy/MM/dd} format.
		 */
		public YearMonthDay(String jalaliDate) {
			String[] parts = jalaliDate.split("/");
			if (parts.length != 3) {
				throw new IllegalArgumentException(
						"Invalid Jalali date format. Expected yyyy/MM/dd, got: " + jalaliDate);
			}
			this.year = Integer.parseInt(parts[0]);
			this.month = Integer.parseInt(parts[1]) - 1;  // convert to 0-based
			this.day = Integer.parseInt(parts[2]);
		}

		public void setYear(int year) {
			this.year = year;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public void setDay(int day) {
			this.day = day;
		}

		@Override
		public String toString() {
			return String.format("%04d/%02d/%02d", year, month + 1, day);
		}
	}
}