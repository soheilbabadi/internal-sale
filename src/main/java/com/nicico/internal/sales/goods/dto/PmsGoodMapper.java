package com.nicico.internal.sales.goods.dto;

import com.nicico.internal.sales.goods.model.PmsMappingModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PmsGoodMapper {
	PmsMappingDto.Info toDTO(PmsMappingModel request);

	PmsMappingModel fromDTO(PmsMappingDto.Create request);
}
