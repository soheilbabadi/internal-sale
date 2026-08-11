package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInsHistoryDTO;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.GridDTO;
import com.nicico.bpmsclient.model.flowable.task.TaskDetail;
import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.bpmsclient.model.request.TaskSearchDto;
import com.nicico.internal.sales.wf.dto.WorkflowDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProcessService {

	List<TaskInfo> getInstanceTasks(String processInstanceId);

	GridDTO searchTaskInbox(TaskSearchDto taskSearchDto, int page, int size);

	List<Map<String, Object>> searchProcess(Pageable pageable);

	ProcessInstance cancelProcessInstance(String processInstanceId);

	boolean checkProcessInstanceEnded(String processInstanceId);

	TaskDetail getTaskDetail(String taskId);

	ProcessInsHistoryDTO getProcessInstanceHistory(String processInstanceId);

	ProcessInstanceHistory getProcessInstanceHistoryById(String processInstanceId);

	Map<String, List<UserTaskReportDTO>> getUserTasksReport(String processInstanceId);

	Long taskCount();

	String createProcess(WorkflowDto.Create dto);

}
