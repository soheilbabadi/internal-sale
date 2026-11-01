package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.customer.dto.CustomerDTO;
import com.nicico.internal.sales.customer.model.CustomerModel;
import com.nicico.internal.sales.pms.dto.PMSCreateCustomerDto;

import java.util.List;

public interface PmsCustomerCreateRabbitService {
	void createCustomer(PMSCreateCustomerDto.RabbitListenerRequestDTO customer);

	void createCustomer(CustomerModel customer);

	void createCustomer(Long customerId);

	/**
	 * Compensation path for customers whose PMS response was lost (listener error, dropped message
	 * or optimistic-lock conflict on save). Re-sends every customer that still has no
	 * pmsCustomerCode. Idempotent: rows already carrying a code are filtered out by the query and
	 * the response listener skips duplicates, so running this repeatedly is safe.
	 */
	List<CustomerDTO.Info> sendCustomersWithoutPmsCode();
}
