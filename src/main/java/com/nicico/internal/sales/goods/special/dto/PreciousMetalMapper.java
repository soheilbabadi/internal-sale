package com.nicico.internal.sales.goods.special.dto;

import com.nicico.internal.sales.goods.special.model.PreciousMetalModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PreciousMetalMapper {
	PreciousMetalDto.Info toDTO(PreciousMetalModel request);

	PreciousMetalModel fromDTO(PreciousMetalDto.Create request);
}
