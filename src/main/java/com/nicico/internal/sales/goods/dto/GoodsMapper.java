package com.nicico.internal.sales.goods.dto;

import com.nicico.internal.sales.goods.model.GoodsModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsMapper {
	@Mapping(target = "description", source = "description")
	GoodsDTO.Info toDTO(GoodsModel request);

	GoodsModel fromDTO(GoodsDTO.Create request);
}
