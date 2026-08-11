package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.model.PricingCommodityWithAvgModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PricingCommodityAvgMapper {
	PricingCommodityWithAvgDto.Info toDTO(PricingCommodityWithAvgModel request);

	PricingCommodityWithAvgModel fromDTO(PricingCommodityWithAvgDto.Create request);
}
