package com.nicico.internal.sales.extrabill.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.*;

import java.util.List;
import java.util.Map;

public interface ExtraBillService {


	SearchDTO.SearchRs<ProformaBankBillDto.Info> search(SearchDTO.SearchRq request);


	SearchDTO.SearchRs<ProformaBankBillReportDto.Info> searchReport(SearchDTO.SearchRq request);


	List<ProformaBankBillDto.Info> saveAll(List<ProformaBankBillRequest> requests);

	ProformaBankBillDto.Info save(ProformaBankBillRequest extraBillIssue);

	List<ProformaBankBillDto.Info> getByMasterId(Long proformaMasterId);


	ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto);

	void sendReckoningEmail(Long extraBillId);

	ProformaBankBillDto.Info updateExtraBill(UpdateExtraBillRequest updateExtraBillRequest);

	List<ProformaBankBillAuditDto> getAuditHistory(Long extraBillId);


	public SearchDTO.SearchRs<ProformaBankBillReportDto.Info> findReadyReckoning(SearchDTO.SearchRq request);

	String generateExtraBillBrokerEmailContent(long extraBillId);

	ProcessInstanceHistory getHistoryDetail(Long extraBillId);
	Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long extraBillId);

	void markAllAsReckoning(Long proformaMasterId);

	void cancel(ExtraBillCancelRequest request);

}
