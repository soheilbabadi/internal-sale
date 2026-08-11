package com.nicico.internal.sales.pms.dto;

import com.nicico.internal.sales.pms.model.PMSGoodsModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PMSGoodsMapper {
	PMSGoodsDTO.Info toDTO(PMSGoodsModel imeTrade);

	List<PMSGoodsDTO.Info> toDTOS(List<PMSGoodsModel> imeTrade);
}
