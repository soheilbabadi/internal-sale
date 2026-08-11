package com.nicico.internal.sales.wf.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.wf.dto.ProcessUserAccessDto;
import com.nicico.internal.sales.wf.dto.ProcessUserAccessRequest;
import com.nicico.internal.sales.wf.dto.request.UserDataDto;

import java.util.List;

public interface ProcessUserAccessService {
	List<ProcessUserAccessDto.Info> getMyAccess();

	List<UserDataDto> getUserList(String keyword);

	void delete(String processTitle);

	ProcessUserAccessDto.Info save(ProcessUserAccessRequest request);

	SearchDTO.SearchRs<ProcessUserAccessDto.Info> search(SearchDTO.SearchRq request);

	void deleteById(Long id);
}
