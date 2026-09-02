package com.nicico.internal.sales.lc.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.lc.dto.LcAuditDto;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcFilesDto;
import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;
import com.nicico.internal.sales.lc.dto.request.LcCancelRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateAcceptedLcRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateStartedLcRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LcService {
	LcDto.Info updateStartedLc(UpdateStartedLcRequest lcRequest);

	Date calculateExpireDate(UpdateStartedLcRequest lcRequest);

	SearchDTO.SearchRs<LcDto.Info> search(SearchDTO.SearchRq request);

	LcDto.Info getLcData(Long id);

	LcDto.Info updateCurrentAcceptedLc(UpdateAcceptedLcRequest updateAcceptedLcRequest);

	LcFilesDto updateLcFiles(LcFilesDto lcFilesDto);

	List<LcDto.Info> getAllLcDataByProformaMasterId(Long proformaMasterId);

	LcDto.Info getByProformaDetailId(Long detailId);

	List<LcDto.Info> getAllLcDataByProcessInstanceId(String processInstanceId);

	List<LcDto.Info> getFailedLc(Pageable pageable, Sort sort);

	List<LcAuditDto> getAuditHistory(Long lcId);

	void sendReckoningEmail(Long lcId);

	List<LcDto.Info> findUnsentReckoning();

	SearchDTO.SearchRs<LcDto.Info> findReadyReckoning(SearchDTO.SearchRq request);

	String generateLcBrokerEmailContent(long lcId);

	Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long lcId);

	void cancel(LcCancelRequest lcCancelRequest);

	String generateLcBrokerEmailContent(LcBrokerEmailRequest dto);

	ProcessInstanceHistory getLcHistoryDetail(Long lcId);


	void updateLcAcknowledgment(Long lcId);




	void updateAllAcknowledgments();
}
