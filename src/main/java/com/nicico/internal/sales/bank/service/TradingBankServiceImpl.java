package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.BankMapper;
import com.nicico.internal.sales.bank.dto.TradingBankDto;
import com.nicico.internal.sales.bank.repository.TradingBankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TradingBankServiceImpl implements TradingBankService {
	private final TradingBankRepository repository;
	private final BankMapper mapper;

	public TradingBankDto save(TradingBankDto.Create dto) {
		dto.setIban(dto.getIban().toUpperCase());
		var bankBranch = mapper.fromDTO(dto);
		repository.save(bankBranch);
		return mapper.toDTO(bankBranch);
	}

	public SearchDTO.SearchRs<TradingBankDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, item -> {
			var dto = mapper.toDTO(item);
			if (dto.getLastModifiedDate() == null) {
				dto.setLastModifiedDate(dto.getCreatedDate());
			}
			return dto;
		});
	}

	@Override
	public List<TradingBankDto.Info> getAll() {
		var list = repository.findAll();
		return list.stream().map(mapper::toDTO).toList();
	}
}
