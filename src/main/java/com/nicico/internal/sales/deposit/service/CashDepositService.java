package com.nicico.internal.sales.deposit.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.deposit.dto.CashDepositIssueHistoryDto;
import com.nicico.internal.sales.deposit.dto.CashDepositReadyReckoningDto;
import com.nicico.internal.sales.lc.dto.request.BrokerEmailRequest;

import java.util.List;
import java.util.Map;

public interface CashDepositService {

	CashDepositIssueHistoryDto.Info getById(Long id);

	List<CashDepositIssueHistoryDto.Info> getByMasterId(Long masterId);
	SearchDTO.SearchRs<CashDepositReadyReckoningDto.Info> findReadyReckoning(SearchDTO.SearchRq request);

//	SearchDTO.SearchRs<CashDepositDto.Info> search(SearchDTO.SearchRq request);
	SearchDTO.SearchRs<CashDepositIssueHistoryDto.Info> searchIssueHistory(SearchDTO.SearchRq request);
	void sendReckoningEmail(Long id);
	String generateBrokerEmailContent(long id);

	void sendBrokerReckoningEmail(BrokerEmailRequest emailRequest, String emailContent);

	ProcessInstanceHistory getHistoryDetail(Long id);

	Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long id);
}
