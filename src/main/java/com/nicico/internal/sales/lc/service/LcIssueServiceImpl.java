package com.nicico.internal.sales.lc.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.dto.LcIssueMapper;
import com.nicico.internal.sales.lc.dto.LcIssueProviderDto;
import com.nicico.internal.sales.lc.model.LcIssueProviderModel;
import com.nicico.internal.sales.lc.repository.LcIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LcIssueServiceImpl implements LcIssueService {
	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "رکورد پیش فاکتور جهت صدور اعتبار پیدا نشد";
	private final LcIssueRepository repository;
	private final LcIssueMapper mapper;

	@Override
	public LcIssueProviderDto.Info getById(Long id) {
		LcIssueProviderModel entity = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		return mapper.toDTO(entity);
	}

	@Override
	public List<LcIssueProviderDto.Info> getByMasterId(Long masterId) {
		List<LcIssueProviderModel> entities = repository.findByMasterId(masterId);

		return entities.stream()
				.map(mapper::toDTO)
				.toList();
	}

	@Override
	public SearchDTO.SearchRs<LcIssueProviderDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

}