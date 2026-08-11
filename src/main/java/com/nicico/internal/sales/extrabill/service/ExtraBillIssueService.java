package com.nicico.internal.sales.extrabill.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.ExtraBillIssueProviderDto;

import java.util.List;

public interface ExtraBillIssueService {
	ExtraBillIssueProviderDto.Info getById(Long id);

	List<ExtraBillIssueProviderDto.Info> getByMasterId(Long masterId);


	SearchDTO.SearchRs<ExtraBillIssueProviderDto.Info> search(SearchDTO.SearchRq request);
}
