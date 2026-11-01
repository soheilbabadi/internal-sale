package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.customer.dto.CustomerDTO;

import java.util.List;

public interface PmsCustomerCreateRabbitService {
	/**
	 * Single entry point for provisioning a customer in PMS: loads the row by internal id,
	 * builds the Rabbit payload and publishes it.
	 */
	void createCustomer(Long customerId);

	/**
	 * Compensation path for customers whose PMS response was lost (listener error, dropped message
	 * or optimistic-lock conflict on save). Re-sends every customer that still has no
	 * pmsCustomerCode. Idempotent: rows already carrying a code are filtered out by the query and
	 * the response listener skips duplicates, so running this repeatedly is safe.
	 */
	List<CustomerDTO.Info> sendCustomersWithoutPmsCode();
}
