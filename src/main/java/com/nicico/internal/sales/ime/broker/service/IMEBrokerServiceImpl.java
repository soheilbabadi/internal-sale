package com.nicico.internal.sales.ime.broker.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.ime.broker.dto.IMEBrokerDTO;
import com.nicico.internal.sales.ime.broker.dto.IMEBrokerMapper;
import com.nicico.internal.sales.ime.broker.repository.IMEBrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IMEBrokerServiceImpl implements IMEBrokerService {
	private final IMEBrokerRepository imeBrokerRepository;
	private final IMEBrokerMapper imeBrokerMapper;

	@Override
	public SearchDTO.SearchRs<IMEBrokerDTO.Info> search(SearchDTO.SearchRq request) {
		log.info("user {} asked for ime-broker search. criteria: {}", SecurityUtil.getUsername(), request);
		return SearchUtil.search(imeBrokerRepository, request, imeBrokerMapper::toDTO);
	}
}