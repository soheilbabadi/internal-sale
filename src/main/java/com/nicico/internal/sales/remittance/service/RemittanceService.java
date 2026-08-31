package com.nicico.internal.sales.remittance.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.remittance.dto.*;

import java.util.List;
import java.util.Map;

public interface RemittanceService {
	SearchDTO.SearchRs<RemittanceMasterDto.Info> search(SearchDTO.SearchRq request);

	RemittanceMasterDto.Info createFromTrade(RemittanceCreateDto request);

	RemittanceMasterDto.Info createFromProforma(RemittanceCreateDto request);

	SearchDTO.SearchRs<RemittanceTradeDataProviderDto.Info> searchFromTrade(SearchDTO.SearchRq request);

	SearchDTO.SearchRs<RemittanceProformaDataProviderDto.Info> searchFromProforma(SearchDTO.SearchRq request);

	RemittanceMasterDto.Info getDetailById(Long masterId);

	List<RemittanceMasterDto.Info> getByContractNo(Long contractNo);

	RemittanceMasterDto updateDescription(RemittanceUpdateRequest request);

	ProcessInstanceHistory getHistoryDetail(Long id);

	Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long id);
}
