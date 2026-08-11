package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.model.PricingCurrencyTypeModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PricingCurrencyTypeMapper {
	PricingCurrencyTypeDto.Info toDTO(PricingCurrencyTypeModel request);

	PricingCurrencyTypeModel fromDTO(PricingCurrencyTypeDto.Create request);
}
