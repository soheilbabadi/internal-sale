package com.nicico.internal.sales.ime.broker.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ime.broker.dto.IMEBrokerDTO;

public interface IMEBrokerService {
	SearchDTO.SearchRs<IMEBrokerDTO.Info> search(SearchDTO.SearchRq request);
}
