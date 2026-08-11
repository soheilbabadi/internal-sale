package com.nicico.internal.sales.broker.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.broker.dto.BrokerDto;

import java.util.List;
import java.util.Map;

public interface BrokerService {
	SearchDTO.SearchRs<BrokerDto.Info> search(SearchDTO.SearchRq request);

	BrokerDto.Info getById(long id);

	List<BrokerDto.Info> getAll();

	BrokerDto.Info save(BrokerDto.Create request);

	Map<String, String> contactMissing(Long id);

	BrokerDto.Info getByTradeId(long id);
}
