package com.nicico.internal.sales.pms.service;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.dto.grid.TotalResponse;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pms.dto.PMSGoodsDTO;

public interface PMSGoodsService {
	SearchDTO.SearchRs<PMSGoodsDTO.Info> search(SearchDTO.SearchRq request);

	TotalResponse<PMSGoodsDTO.Info> search(NICICOCriteria request);
}
