package com.nicico.internal.sales.salecondition.dto;

import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleConditionMapper {
	SaleConditionDto.Info toDTO(SaleConditionModel request);

	SaleConditionModel fromDTO(SaleConditionDto.Create request);
}
