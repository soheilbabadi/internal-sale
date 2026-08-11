package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceGoodItemDto;
import com.nicico.internal.sales.remittance.model.RemittanceGoodItemModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RemittanceGoodItemMapper {
	RemittanceGoodItemDto.Info toDTO(RemittanceGoodItemModel model);

	RemittanceGoodItemModel fromDTO(RemittanceGoodItemDto.Create request);
}
