package com.nicico.internal.sales.gaam.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.gaam.dto.*;
import com.nicico.internal.sales.gaam.model.GaamModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface GaamService {
	SearchDTO.SearchRs<GaamDto.Info> search(SearchDTO.SearchRq request);

	SearchDTO.SearchRs<GaamReportDto.Info> searchReport(SearchDTO.SearchRq request);

	@Transactional
	List<GaamDto.Info> saveAll(	List<GaamRequest> requests);



	@Transactional
	GaamDto.Info save(GaamRequest request);

	List<GaamDto.Info> getByMasterId(Long proformaMasterId);

	
	@Transactional
	GaamDto.Info updateGaamFiles(GaamFileUpdateDto updateDto);

	@Transactional
	void sendReckoningEmail(Long gaamId);

	String generateGaamBrokerEmailContent(long gaamId);

	Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long gaamID);

	ProcessInstanceHistory getHistoryDetail(Long gaamId);

	@Transactional
	GaamDto.Info update(UpdateGaamRequest updateGaamRequest);

	

	@Transactional(readOnly = true)
	List<GaamAuditDto> getAuditHistory(Long gaamID);

	SearchDTO.SearchRs<GaamReportDto.Info> findReadyReckoning(SearchDTO.SearchRq request);

	void markAllAsReckoning(Long proformaMasterId);

	void cancel(GaamCancelRequest request);

	void cancelGaamModel(GaamModel model, GaamCancelRequest request);
}
