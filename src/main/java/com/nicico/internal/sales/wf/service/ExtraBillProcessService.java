package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ExtraBillProcessService {
	ProcessInstance startExtraBillProcess(Long masterId);

	ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto);


	void approveTask(TaskActionDto taskActionDto);

	void rejectTask(TaskActionDto taskActionDto);

	void refreshExtraBillStatus();

	boolean canStartProcess();


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void refreshSingleBillStatus(Long billId);

	void rejectExtraBill(String processId);

	boolean canFinishProcess();

	ExtraBillProcessVariable detectExtraBillStep(String processInstanceId);

	ExtraBillProcessVariable detectExtraBillStep(long extraBillId);
}
