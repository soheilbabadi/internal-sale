package com.nicico.internal.sales.wf.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.dto.WorkflowDto;
import com.nicico.internal.sales.wf.dto.mapper.WorkflowMapper;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {
	private final WorkflowRepository repository;
	private final WorkflowMapper mapper;

	@Override
	public SearchDTO.SearchRs<WorkflowDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public WorkflowDto.Info get(String id) {
		return mapper.toDTO(repository.findById(id).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("Workflow not found")));
	}

	@Override
	public List<Map<String, String>> getAll() {
		var list = repository.findAll();
		return list.stream().map(workflow -> Map.of("id", workflow.getId(), "title", workflow.getProcessTitle())).toList();
	}

	@Override
	public List<WorkflowDto.Info> getDtoList() {
		var list = repository.findAll();
		return list.stream().map(mapper::toDTO).toList();
	}

	@Override
	public WorkflowDto.Info save(WorkflowDto.Create request) {
		WorkflowModel model = mapper.fromDTO(request);
		WorkflowModel save = repository.save(model);
		return mapper.toDTO(save);
	}

	@Override
	public void delete(String id) {
		repository.deleteById(id);
	}

	@Override
	public WorkflowDto.Info getByProcessName(String name) {
		var entity = repository.findByProcessTitleIgnoreCase(name).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("Workflow not found"));
		return mapper.toDTO(entity);
	}
}
