package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaMasterMapper {
	ProformaMasterDTO.Info toDTO(ProformaMasterModel request);

	ProformaMasterModel fromDTO(ProformaMasterDTO.Create request);
}
