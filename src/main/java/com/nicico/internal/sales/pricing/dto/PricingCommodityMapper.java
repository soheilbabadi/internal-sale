package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.model.PricingCommodityModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PricingCommodityMapper {
	PricingCommodityDto.Info toDTO(PricingCommodityModel request);

	PricingCommodityModel fromDTO(PricingCommodityDto.Create request);
}
