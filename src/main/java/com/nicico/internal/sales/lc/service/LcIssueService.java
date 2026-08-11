package com.nicico.internal.sales.lc.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.lc.dto.LcIssueProviderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LcIssueService {
	LcIssueProviderDto.Info getById(Long id);

	List<LcIssueProviderDto.Info> getByMasterId(Long masterId);

	Page<LcIssueProviderDto.Info> getAll(Pageable pageable);

	SearchDTO.SearchRs<LcIssueProviderDto.Info> search(SearchDTO.SearchRq request);
}
