package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.proforma.dto.PreciousMetalProfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import org.springframework.transaction.annotation.Transactional;

public interface PreciousMetalService {
	String create(PreciousMetalProfomaCreateRequest requestDto);

	@Transactional
	ProformaMasterModel createProformaMaster(PreciousMetalProfomaCreateRequest requestDto);

	ProformaModelResponse getContractDetail(PreciousMetalProfomaCreateRequest requestDto);

	boolean isPreciousMetalByPaymentCode(String paymentCode);


}
