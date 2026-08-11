package com.nicico.internal.sales.extrabill.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.dto.ExtraBillIssueMapper;
import com.nicico.internal.sales.extrabill.dto.ExtraBillIssueProviderDto;
import com.nicico.internal.sales.extrabill.model.ExtraBillIssueProviderModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExtraBillIssueServiceImpl implements ExtraBillIssueService {
	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "رکورد پیش فاکتور صدور اعتبار اسنادی یافت نشد";
	private final ExtraBillIssueRepository repository;

	private final ExtraBillIssueMapper mapper;


	@Override
	public ExtraBillIssueProviderDto.Info getById(Long id) {
		ExtraBillIssueProviderModel entity = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		return mapper.toDTO(entity);
	}

	@Override
	public List<ExtraBillIssueProviderDto.Info> getByMasterId(Long masterId) {
		var entities = repository.findByMasterId(masterId);
		return entities.stream()
				.map(mapper::toDTO)
				.toList();
	}

	@Override
	public SearchDTO.SearchRs<ExtraBillIssueProviderDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

}
