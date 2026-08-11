package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RemittanceMasterMapper {
	RemittanceMasterDto.Info toDTO(RemittanceMasterModel model);

	RemittanceMasterModel fromDTO(RemittanceMasterDto.Create request);
}
