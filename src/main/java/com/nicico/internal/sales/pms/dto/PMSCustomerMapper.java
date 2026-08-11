package com.nicico.internal.sales.pms.dto;

import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PMSCustomerMapper {
	PMSCustomerDTO.Info toDTO(PMSCustomerModel imeTrade);
}
