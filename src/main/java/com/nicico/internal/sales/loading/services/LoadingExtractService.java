package com.nicico.internal.sales.loading.services;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.loading.dto.CreateGoodLoading;
import com.nicico.internal.sales.loading.dto.CreateGoodLoadingBatch;
import com.nicico.internal.sales.loading.dto.LoadingExtractDto;

import java.util.List;

public interface LoadingExtractService {
	SearchDTO.SearchRs<LoadingExtractDto.Info> search(SearchDTO.SearchRq request);

	LoadingExtractDto.Info get(long id);

	List<LoadingExtractDto.Info> getAll();

	void save(CreateGoodLoading request);

	void saveBatch(CreateGoodLoadingBatch request);

	void deleteById(Long id);
}
