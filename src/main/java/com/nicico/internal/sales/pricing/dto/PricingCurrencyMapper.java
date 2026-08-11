package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.model.PricingCurrencyModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PricingCurrencyMapper {
	PricingCurrencyDto.Info toDTO(PricingCurrencyModel request);

	PricingCurrencyModel fromDTO(PricingCurrencyDto.Create request);
}
