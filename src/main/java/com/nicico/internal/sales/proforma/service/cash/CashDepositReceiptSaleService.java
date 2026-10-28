package com.nicico.internal.sales.proforma.service.cash;

import com.nicico.internal.sales.proforma.dto.CashDepositCreateRequest;
import com.nicico.internal.sales.proforma.dto.CashSaleCreateRequest;

public interface CashDepositReceiptSaleService {

	CashDepositCreateRequest generateSuggestion(CashSaleCreateRequest cashSaleCreateRequest);
	String create(CashDepositCreateRequest requestDto);
}

