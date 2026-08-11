package com.nicico.internal.sales.ime.commodity.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ime.commodity.dto.IMECommodityDTO;

public interface IMECommodityService {
	SearchDTO.SearchRs<IMECommodityDTO.Info> search(SearchDTO.SearchRq request);
}
