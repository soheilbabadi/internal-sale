package com.nicico.internal.sales.lc.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.lc.dto.LcIssueProviderDto;

import java.util.List;

public interface LcIssueService {
	LcIssueProviderDto.Info getById(Long id);

	List<LcIssueProviderDto.Info> getByMasterId(Long masterId);
	SearchDTO.SearchRs<LcIssueProviderDto.Info> search(SearchDTO.SearchRq request);
}
