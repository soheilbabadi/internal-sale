package com.nicico.internal.sales.pricing.enums;

import lombok.Getter;

@Getter
public enum QCAnalysisStage {
	PRE_OFFER("آنالیز قبل عرضه"),
	POST_OFFER("آنالیز بعد عرضه"),
	CORRECTION("آنالیز اصلاحی / توافقی");

	private final String title;

	QCAnalysisStage(String title) {
		this.title = title;
	}
}
