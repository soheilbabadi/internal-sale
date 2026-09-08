//package com.nicico.internal.sales.gaam.service;
//
//import com.nicico.copper.common.domain.criteria.SearchUtil;
//import com.nicico.copper.common.dto.search.SearchDTO;
//import com.nicico.internal.sales.exception.InternalSaleCustomException;
//import com.nicico.internal.sales.gaam.mapper.GaamIssueMapper;
//import com.nicico.internal.sales.gaam.dto.GaamIssueProviderDto;
//import com.nicico.internal.sales.gaam.model.GaamIssueProviderModel;
//import com.nicico.internal.sales.gaam.repository.GaamIssueRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class GaamIssueServiceImpl implements GaamIssueService {
//	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "رکورد پیش فاکتور صدور اعتبار اسنادی یافت نشد";
//	private final GaamIssueRepository repository;
//
//	private final GaamIssueMapper mapper;
//
//
//
//	@Override
//	public GaamIssueProviderDto.Info getById(Long id) {
//		GaamIssueProviderModel entity = repository.findById(id)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
//						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
//		return mapper.toDTO(entity);
//	}
//
//
//	@Override
//	public List<GaamIssueProviderDto.Info> getByMasterId(Long masterId) {
//		var entities = repository.findByMasterId(masterId);
//		return entities.stream()
//				.map(mapper::toDTO)
//				.toList();
//	}
//
//
//	@Override
//	public SearchDTO.SearchRs<GaamIssueProviderDto.Info> search(SearchDTO.SearchRq request) {
//		return SearchUtil.search(repository, request, mapper::toDTO);
//	}
//
//}
