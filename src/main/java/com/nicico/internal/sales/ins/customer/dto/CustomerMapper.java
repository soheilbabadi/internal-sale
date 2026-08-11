package com.nicico.internal.sales.ins.customer.dto;

import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
	CustomerDTO.Info toDTO(CustomerModel request);

	CustomerModel fromDTO(CustomerDTO.Create request);
}
