package com.nicico.internal.sales.wf.detection;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;

import java.util.List;
import java.util.Map;

public interface ProcessHistoryService {
	ProcessInstanceHistory getLcHistoryDetail(Long lcId);

	Map<String, List<UserTaskReportDTO>> getLcSummaryReport(Long lcId);


	ProcessInstanceHistory getProformaHistoryDetail(Long proformaMasterId);

	Map<String, List<UserTaskReportDTO>> getProformaSummaryReport(Long proformaMasterId);

	ProcessInstanceHistory getRemittanceHistoryDetail(Long remittanceId);

	ProcessInstanceHistory getGaamHistoryDetail(Long gaamId);

	Map<String, List<UserTaskReportDTO>> getGaamSummaryReport(Long gaamId);

	Map<String, List<UserTaskReportDTO>> getRemittanceSummaryReport(Long remittanceId);

	ProcessInstanceHistory getProformaBankBillHistoryDetail(Long billId);

	Map<String, List<UserTaskReportDTO>> getProformaBankBillSummaryReport(Long billId);

	Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId);
}
