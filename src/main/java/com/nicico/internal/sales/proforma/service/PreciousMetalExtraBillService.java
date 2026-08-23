package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.proforma.dto.PreciousMetalProfomaCreateRequest;
import org.springframework.transaction.annotation.Transactional;

public interface PreciousMetalExtraBillService {
	@Transactional
	String create(PreciousMetalProfomaCreateRequest requestDto);
}
