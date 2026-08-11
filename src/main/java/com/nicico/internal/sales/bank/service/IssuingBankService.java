package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.IssuingBankDto;

import java.util.List;

public interface IssuingBankService {
	IssuingBankDto save(IssuingBankDto.Create dto);

	SearchDTO.SearchRs<IssuingBankDto.Info> search(SearchDTO.SearchRq request);

	List<IssuingBankDto.Info> getAll();

	IssuingBankDto.Info getById(long id);
}
