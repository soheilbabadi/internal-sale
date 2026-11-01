package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.remittance.model.RemittanceMasterSulfuricAcidModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RemittanceMasterSulfuricAcidMapper {
	RemittanceMasterDto.Info toDTO(RemittanceMasterSulfuricAcidModel model);

	RemittanceMasterSulfuricAcidModel fromDTO(RemittanceMasterDto.Create request);
}
