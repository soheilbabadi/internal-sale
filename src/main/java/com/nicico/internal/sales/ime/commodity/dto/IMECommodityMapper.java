package com.nicico.internal.sales.ime.commodity.dto;

import com.nicico.internal.sales.ime.commodity.model.IMECommodityModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IMECommodityMapper {
	IMECommodityDTO.Info toDTO(IMECommodityModel imeTrade);
}
