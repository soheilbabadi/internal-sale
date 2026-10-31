package com.nicico.internal.sales.pricing.enums;

import lombok.Getter;

@Getter
public enum QCAnalysisStatus {
	DRAFT("پیش‌نویس"),
	CONFIRMED("تایید شده"),
	SUPERSEDED("جایگزین / منسوخ شده"),
	CANCELLED("ابطال شده");

	private final String title;

	QCAnalysisStatus(String title) {
		this.title = title;
	}
}
