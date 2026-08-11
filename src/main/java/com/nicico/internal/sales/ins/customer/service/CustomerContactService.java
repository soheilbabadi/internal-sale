package com.nicico.internal.sales.ins.customer.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ins.customer.dto.CustomerContactDto;

public interface CustomerContactService {
	CustomerContactDto.Create save(CustomerContactDto.Create request);

	SearchDTO.SearchRs<CustomerContactDto.Create> filterByCustomer(long customerId, SearchDTO.SearchRq request);

	void delete(Long id);

	CustomerContactDto.Create update(Long id, CustomerContactDto.Create request);
}
