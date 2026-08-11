package com.nicico.internal.sales.ime.commodity.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.ime.commodity.dto.IMECommodityDTO;
import com.nicico.internal.sales.ime.commodity.dto.IMECommodityMapper;
import com.nicico.internal.sales.ime.commodity.repository.IMECommodityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IMECommodityServiceImpl implements IMECommodityService {
	private final IMECommodityRepository repository;
	private final IMECommodityMapper mapper;

	@Override
	public SearchDTO.SearchRs<IMECommodityDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}
}
