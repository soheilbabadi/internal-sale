package com.nicico.internal.sales.penalty.dto;

import com.nicico.internal.sales.penalty.model.DimDateModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DimDateMapper {
	DimDateDto.Info toDTO(DimDateModel request);

	DimDateModel fromDTO(DimDateDto.Create request);
}
