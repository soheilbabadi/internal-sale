package com.nicico.internal.sales.loading.dto;

import com.nicico.internal.sales.loading.model.IssuePlaceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IssuePlaceMapper {
	IssuePlaceDto.Info toDTO(IssuePlaceModel request);

	IssuePlaceModel fromDTO(IssuePlaceDto.Create request);
}
