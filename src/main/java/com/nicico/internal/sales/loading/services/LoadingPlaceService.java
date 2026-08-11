package com.nicico.internal.sales.loading.services;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.loading.dto.LoadingPlaceDto;

import java.util.List;

public interface LoadingPlaceService {
	SearchDTO.SearchRs<LoadingPlaceDto.Info> search(SearchDTO.SearchRq request);

	LoadingPlaceDto.Info get(long id);

	List<LoadingPlaceDto.Info> getAll();

	LoadingPlaceDto.Info save(LoadingPlaceDto.Create request);
}
