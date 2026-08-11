package com.nicico.internal.sales.loading.dto;

import com.nicico.internal.sales.loading.model.LoadingExtractModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoadingExtractMapper {
	LoadingExtractDto.Info toDTO(LoadingExtractModel request);

	LoadingExtractModel fromDTO(LoadingExtractDto.Create request);
}
