package com.nicico.internal.sales.lc.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.dto.LcIssueMapper;
import com.nicico.internal.sales.lc.dto.LcIssueProviderDto;
import com.nicico.internal.sales.lc.model.LcIssueProviderModel;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcIssueRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import com.nicico.internal.sales.wf.service.ProcessVariableProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LcIssueServiceImpl implements LcIssueService {
	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "رکورد پیش فاکتور صدور اعتبار اسنادی یافت نشد";
	private final LcIssueRepository repository;
	private final LcRepository lcRepository;
	private final LcIssueMapper mapper;
	private final ProcessVariableProvider processVariableProvider;
	private final ProcessStatusDeterminerService processStatusDeterminerService;

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


		var lcList = lcRepository.findByMasterId(masterId);
		for (LcModel entity : lcList) {
			processStatusDeterminerService.updateLcAcknowledgment(entity.getId());
		}


		return entities.stream()
				.map(mapper::toDTO)
				.toList();
	}

	@Override
	public Page<LcIssueProviderDto.Info> getAll(Pageable pageable) {
		Page<LcIssueProviderModel> page = repository.findAll(pageable);
		return page.map(mapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<LcIssueProviderDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

}
