package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.internal.sales.wf.dto.TaskActionDto;

public interface ExtraBillProcessService {
	ProcessInstance startExtraBillProcess(Long masterId);

	ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto);
	void approveTask(TaskActionDto taskActionDto);
	void rejectTask(TaskActionDto taskActionDto);
	boolean canStartProcess();

}
