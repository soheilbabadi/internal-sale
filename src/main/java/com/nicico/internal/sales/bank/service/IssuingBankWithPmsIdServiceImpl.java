package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.IssuingBankWithPmsIdDto;
import com.nicico.internal.sales.bank.dto.IssuingBankWithPmsIdMapper;
import com.nicico.internal.sales.bank.repository.IssuingBankWithPmsIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IssuingBankWithPmsIdServiceImpl implements IssuingBankWithPmsIdService {
	private final IssuingBankWithPmsIdRepository repository;
	private final IssuingBankWithPmsIdMapper mapper;

	public SearchDTO.SearchRs<IssuingBankWithPmsIdDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, item -> {
			var dto = mapper.toDTO(item);
			if (dto.getLastModifiedDate() == null) {
				dto.setLastModifiedDate(dto.getCreatedDate());
			}
			return dto;
		});
	}
}
