package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.proforma.dto.*;

import java.util.List;

public interface ProformaValidationService {

	List<String> validateProformaData(PerfomaCreateRequest requestDto);

	List<String> validateProformaData(BaseOrderRequest requestDto);

	List<String> validateProformaData(PreciousMetalProfomaCreateRequest requestDto);

	void validateDate(PerfomaCreateRequest requestDto);

	void validateDate(CashSaleCreateRequest requestDto);

	List<String> validateReversal(Long masterId);

	boolean canStartReversal(Long contractNo);

	List<String> validateCashSaleData(CashSaleCreateRequest requestDto);


	List<String> validateMixedProforma(MixedProformaRequest requestDto);
}
