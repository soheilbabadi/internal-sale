package com.nicico.internal.sales.util.date;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import static com.nicico.internal.sales.util.date.Jalali.fromDate;

public class DateUtility {

	private static final ZoneOffset UTC = ZoneOffset.UTC;

	private DateUtility() {
	}

	public static Date getMidnightAfterNow() {
		return Date.from(
				Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS)
		);
	}

	public static Date getMidnightOfToday() {
		return Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS));
	}

	public static Date truncateToMidnight(Date inputDate) {
		Objects.requireNonNull(inputDate, "inputDate must not be null");
		return Date.from(inputDate.toInstant().truncatedTo(ChronoUnit.DAYS));
	}

	public static Date getMidnightOfPreviousDay(Date inputDate) {
		Objects.requireNonNull(inputDate, "inputDate must not be null");

		ZoneId tehranZone = ZoneId.of("Asia/Tehran");
		return Date.from(
				inputDate.toInstant()
						.atZone(tehranZone)
						.toLocalDate()
						.minusDays(1)
						.atStartOfDay(tehranZone)
						.toInstant()
		);
	}

	public static Date getMidnightAfter(Date inputDate) {
		Objects.requireNonNull(inputDate, "inputDate must not be null");
		return Date.from(
				inputDate.toInstant().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS)
		);
	}

	public static Integer getCurrentJalaliYear() {
		return getJalaliYear(new Date());
	}

	public static Integer getJalaliYear(Date inputDate) {
		return fromDate(inputDate != null ? inputDate : new Date()).getYear();
	}

	public static String getJalaliDate(Date inputDate) {
		return fromDate(inputDate != null ? inputDate : new Date()).toString();
	}

	public static String getJalaliDate(LocalDate inputDate) {
		return Jalali.fromLocalDate(inputDate != null ? inputDate : LocalDate.now(UTC)).toString();
	}

	public static Date toGregorianDate(String jalaliDate) {
		if (jalaliDate == null || jalaliDate.isEmpty()) {
			return new Date();
		}
		Jalali.YearMonthDay ymd = Jalali.parseJalali(jalaliDate);
		return toDate(Jalali.toGregorianDate(ymd));
	}


	public static Date addJalaliMonthsToGregorianDate(Date date, int monthsToAdd) {
		Objects.requireNonNull(date, "date must not be null");

		Jalali.YearMonthDay jalali = Jalali.fromDate(date);

		int jYear = jalali.getYear();
		int jMonth = jalali.getMonth() + monthsToAdd;
		int jDay = jalali.getDay();
		while (jMonth > 11) {
			jMonth -= 12;
			jYear++;
		}

		LocalDate gregorian = Jalali.toGregorianDate(new Jalali.YearMonthDay(jYear, jMonth, jDay));
		return toDate(gregorian);
	}

	public static LocalDateTime toLocalDateTime(Date epoch) {
		Objects.requireNonNull(epoch, "epoch must not be null");
		return LocalDateTime.ofInstant(epoch.toInstant(), UTC);
	}


	public static LocalDate toLocalDate(Date epoch) {
		Objects.requireNonNull(epoch, "epoch must not be null");
		return LocalDate.ofInstant(epoch.toInstant(), UTC);
	}

	public static Date toDate(LocalDate localDate) {
		Objects.requireNonNull(localDate, "localDate must not be null");
		return Date.from(localDate.atStartOfDay(UTC).toInstant());
	}

	public static int[] extractPersianDateParts(String persianDate) {
		if (persianDate == null || !persianDate.matches("\\d{4}/\\d{2}/\\d{2}")) {
			throw new IllegalArgumentException("Invalid Persian date format. Expected yyyy/MM/dd, got: " + persianDate);
		}
		String[] parts = persianDate.split("/");
		return new int[]{
				Integer.parseInt(parts[0]),  // year
				Integer.parseInt(parts[1]),  // month
				Integer.parseInt(parts[2])   // day
		};
	}
}