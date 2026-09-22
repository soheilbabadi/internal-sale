package com.nicico.internal.sales.ins.customer.service;

import com.nicico.internal.sales.ins.customer.dto.CustomerContactDto;
import com.nicico.internal.sales.ins.customer.dto.CustomerDTO;

import java.util.List;

public interface CustomerValidationService {

    List<String> validateCreateCustomer(CustomerDTO.Create requestDto);

    List<String> validateUpdateCustomer(CustomerDTO.Create requestDto);

    List<String> validateCreateCustomerContact(CustomerContactDto.Create requestDto);

    List<String> validateUpdateCustomerContact(CustomerContactDto.Create requestDto);
}
