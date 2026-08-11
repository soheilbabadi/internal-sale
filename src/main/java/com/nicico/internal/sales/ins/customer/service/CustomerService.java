package com.nicico.internal.sales.ins.customer.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ins.customer.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
	CustomerDTO.Info save(CustomerDTO.Create request);

	SearchDTO.SearchRs<CustomerDTO.Info> search(SearchDTO.SearchRq request);

	void delete(Long id);

	CustomerDTO.Info update(Long id, CustomerDTO.Create request);

	void importTradeData();

	List<CustomerDTO.Info> findSimilarNames(String name);
}
