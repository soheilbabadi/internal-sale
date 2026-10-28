package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.proforma.dto.ExtraBillCalculationContext;
import com.nicico.internal.sales.proforma.dto.PerfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.ProformaResponseDto;

public interface ExtraBillProformaIssueService {

	String create(PerfomaCreateRequest requestDto);


	ProformaResponseDto getDetailByContractNo(Long contractNo);

	default ProformaResponseDto getByContractNo(Long contractNo) {
		return getDetailByContractNo(contractNo);
	}









	ProformaResponseDto updateProformaExtraBillPercent(Long contractNo);


}

