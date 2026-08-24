package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;

import java.util.List;
import java.util.Map;

public interface ProcessStatusDeterminerService {
	ProcessInstanceHistory getLcHistoryDetail(Long lcId);

	Map<String, List<UserTaskReportDTO>> getLcSummaryReport(Long lcId);

	void updateLcAcknowledgment(Long lcId);

	Acknowledgment determineAcknowledgment(Long lcId);


	Acknowledgment determineAcknowledgment(LcModel lcModel);

	ProcessInstanceHistory getProformaHistoryDetail(Long proformaMasterId);

	Map<String, List<UserTaskReportDTO>> getProformaSummaryReport(Long proformaMasterId);


	ProcessInstanceHistory getRemittanceHistoryDetail(Long remittanceId);

	Map<String, List<UserTaskReportDTO>> getRemittanceSummaryReport(Long remittanceId);

	ProcessInstanceHistory getProformaBankBillHistoryDetail(Long billId);

	Map<String, List<UserTaskReportDTO>> getProformaBankBillSummaryReport(Long billId);

	void updateAllLcAcknowledgments();
}
