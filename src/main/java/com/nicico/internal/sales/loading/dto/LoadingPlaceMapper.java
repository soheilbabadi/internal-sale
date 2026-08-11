package com.nicico.internal.sales.loading.dto;

import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoadingPlaceMapper {
	LoadingPlaceDto.Info toDTO(LoadingPlaceModel request);

	LoadingPlaceModel fromDTO(LoadingPlaceDto.Create request);
}
