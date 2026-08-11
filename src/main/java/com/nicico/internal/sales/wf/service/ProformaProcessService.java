package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.flowable.task.GridDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;

import java.util.List;

public interface ProformaProcessService {
	ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto);

	ProcessInstance startProformaProcess(ProformaVariablesInput input);

	List<String> cancelDoungelingProcess(GridDTO gridDTO);

	ProcessInstance startProformaProcess(Long masterId);

	void reviewTask(ReviewTaskRequest reviewTaskRequestDto);


	void startFailedProcess();

	void approveTask(TaskActionDto taskActionDto);

	void rejectTask(TaskActionDto taskActionDto);

	void refreshProformaStatus();

	boolean canStartProcess();
}
