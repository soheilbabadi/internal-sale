package com.nicico.internal.sales.remittance.service;

import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;

public interface RemittanceTaxService {
	Double calculateTax(RemittanceCreateDto dto);

}
