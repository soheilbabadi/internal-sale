package com.nicico.internal.sales.history.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.history.dto.HistoryDetailResponse;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto;

public interface HistoryService {
	HistoryDetailResponse getHistoryDetails(Long historyId);

	SearchDTO.SearchRs<HistoryExtractMasterDto.Info> search(SearchDTO.SearchRq request);

	HistoryExtractMasterDto.Info findById(Long historyId);
}
