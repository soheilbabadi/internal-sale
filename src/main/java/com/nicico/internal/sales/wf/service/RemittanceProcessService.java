package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.internal.sales.wf.dto.RemittanceVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;

public interface RemittanceProcessService {
	ProcessInstance startProcess(Long masterId);

	ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto);

	ProcessInstance startProcess(RemittanceVariablesInput input);

	//GridDTO getUserTasks(Pageable pageable);

	void approveTask(TaskActionDto taskActionDto);

	void rejectTask(TaskActionDto taskActionDto);

	void reviewTask(ReviewTaskRequest reviewTaskRequest);

	void refreshRemittanceStatus();

	boolean canStartProcess();
}
