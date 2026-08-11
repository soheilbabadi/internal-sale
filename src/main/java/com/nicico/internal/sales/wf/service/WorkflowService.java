package com.nicico.internal.sales.wf.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.wf.dto.WorkflowDto;

import java.util.List;
import java.util.Map;

public interface WorkflowService {
	SearchDTO.SearchRs<WorkflowDto.Info> search(SearchDTO.SearchRq request);

	WorkflowDto.Info get(String id);

	List<Map<String, String>> getAll();

	List<WorkflowDto.Info> getDtoList();

	WorkflowDto.Info save(WorkflowDto.Create request);

	void delete(String id);

	WorkflowDto.Info getByProcessName(String name);
}
