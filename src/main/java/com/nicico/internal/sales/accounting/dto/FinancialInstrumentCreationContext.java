package com.nicico.internal.sales.accounting.dto;

public record FinancialInstrumentCreationContext(
		Long sequenceNumber,
		Long detailNumber,
		String expectedCode,
		DetailDto parent,
		DetailDto.DetailInfoDto detailInfo
) {
}
