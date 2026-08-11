package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.BaseBankDto;
import org.springframework.transaction.annotation.Transactional;

public interface BaseBankService {
	BaseBankDto save(BaseBankDto.Create dto);

	SearchDTO.SearchRs<BaseBankDto.Info> search(SearchDTO.SearchRq request);

	BaseBankDto.Info getById(Long id);

	@Transactional
	int updateAllBaseNosaCodes();


}
