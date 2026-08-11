package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;

public interface LcProcessService {
	ProcessInstance startLcProcess(Long masterId);

	ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto);


	void approveTask(TaskActionDto taskActionDto);

	void rejectTask(TaskActionDto taskActionDto);

	void refreshLcStatus();

	boolean canStartProcess();

	boolean canFinishProcess();

	void rejectLc(String processId);

	LcProcessVariable detectLcStep(String processInstanceId);

	LcProcessVariable detectLcStep(long lcId);
}
