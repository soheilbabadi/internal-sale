package com.nicico.internal.sales.pricing.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pricing.dto.LastUsdRateInfo;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyDto;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyRequest;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyTypeDto;

import java.math.BigDecimal;

public interface PricingCurrencyService {

	PricingCurrencyDto.Info save(PricingCurrencyRequest request);

	SearchDTO.SearchRs<PricingCurrencyDto.Info> search(SearchDTO.SearchRq request);

	void deleteById(String persianShortDate);

	PricingCurrencyTypeDto.Info saveType(PricingCurrencyTypeDto request);

	BigDecimal getAverageUsdRateForLastDay();

	LastUsdRateInfo getLastUsdRateInfo();

	String getLastPricingType();
}