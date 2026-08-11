package com.nicico.internal.sales.remittance.service;

import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;

public interface RemittanceValidation {
	void validateCreateTradeRemittance(RemittanceCreateDto dto);

	void validateCreateProformaRemittance(RemittanceCreateDto dto);
}
