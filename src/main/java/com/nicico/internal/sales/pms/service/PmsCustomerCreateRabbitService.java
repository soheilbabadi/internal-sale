package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.pms.dto.PMSCreateCustomerDto;

public interface PmsCustomerCreateRabbitService {
	void createCustomer(PMSCreateCustomerDto.RabbitListenerRequestDTO customer);

	void createCustomer(CustomerModel customer);

	void createCustomer(Long customerId);
}
