package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.IssuingBankWithPmsIdDto;

public interface IssuingBankWithPmsIdService {
	SearchDTO.SearchRs<IssuingBankWithPmsIdDto.Info> search(SearchDTO.SearchRq request);
}
