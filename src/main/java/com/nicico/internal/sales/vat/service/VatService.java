package com.nicico.internal.sales.vat.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.vat.dto.VatDTO;

public interface VatService {
	VatDTO.Info save(VatDTO.Create request);

	SearchDTO.SearchRs<VatDTO.Info> search(SearchDTO.SearchRq request);

	void delete(Long ids);
}
