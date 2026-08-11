package com.nicico.internal.sales.ins.customer.dto;

import com.nicico.internal.sales.ins.customer.model.CustomerContactModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerContactMapper {
	CustomerContactDto.Create toDTO(CustomerContactModel request);

	CustomerContactModel fromDTO(CustomerContactDto.Create request);
}
