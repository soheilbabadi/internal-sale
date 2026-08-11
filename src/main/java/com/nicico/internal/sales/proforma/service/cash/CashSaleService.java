package com.nicico.internal.sales.proforma.service.cash;

import com.nicico.internal.sales.proforma.dto.CashSaleCreateRequest;

public interface CashSaleService {
	String create(CashSaleCreateRequest requestDto);
}
