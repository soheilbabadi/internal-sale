package com.nicico.internal.sales.bill.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bill.dto.BillingTradeExtractDto;

public interface BillingDataProviderService {
	SearchDTO.SearchRs<BillingTradeExtractDto.Info> searchBillingStartDataProvider(SearchDTO.SearchRq request);
}
