package com.nicico.internal.sales.loading.services;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.loading.dto.IssuePlaceDto;

import java.util.List;

public interface IssuePlaceService {
	SearchDTO.SearchRs<IssuePlaceDto.Info> search(SearchDTO.SearchRq request);

	IssuePlaceDto.Info get(long id);

	List<IssuePlaceDto.Info> getAll();

	IssuePlaceDto.Info save(IssuePlaceDto.Create request);

	void deleteById(long id);

}
