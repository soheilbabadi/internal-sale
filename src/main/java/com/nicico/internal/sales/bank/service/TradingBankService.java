package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.TradingBankDto;

import java.util.List;

public interface TradingBankService {
	TradingBankDto save(TradingBankDto.Create dto);

	SearchDTO.SearchRs<TradingBankDto.Info> search(SearchDTO.SearchRq request);

	List<TradingBankDto.Info> getAll();
}
