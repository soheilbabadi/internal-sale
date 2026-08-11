package com.nicico.internal.sales.pms.service;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.grid.TotalResponse;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pms.dto.PMSGoodsDTO;
import com.nicico.internal.sales.pms.dto.PMSGoodsMapper;
import com.nicico.internal.sales.pms.repository.PMSGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PMSGoodsServiceImpl implements PMSGoodsService {
	private final PMSGoodsRepository imeTradeRepository;
	private final PMSGoodsMapper imeTradeMapper;

	@Override
	public SearchDTO.SearchRs<PMSGoodsDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(imeTradeRepository, request, imeTradeMapper::toDTO);
	}

	@Override
	public TotalResponse<PMSGoodsDTO.Info> search(NICICOCriteria request) {
		return SearchUtil.search(imeTradeRepository, request, imeTradeMapper::toDTO);
	}
}
