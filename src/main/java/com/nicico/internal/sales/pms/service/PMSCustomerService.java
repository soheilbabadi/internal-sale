package com.nicico.internal.sales.pms.service;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.dto.grid.TotalResponse;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pms.dto.PMSCustomerDTO;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;

public interface PMSCustomerService {
	SearchDTO.SearchRs<PMSCustomerDTO.Info> search(SearchDTO.SearchRq request);

	TotalResponse<PMSCustomerDTO.Info> search(NICICOCriteria request);

	PMSCustomerModel findByEconomicCodeOrRegisterNumber(String economicCode, String registerNumber);
}
