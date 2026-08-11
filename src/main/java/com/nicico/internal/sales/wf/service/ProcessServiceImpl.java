package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.config.BPMNClientException;
import com.nicico.bpmsclient.model.flowable.enums.ProcessDefinitionStatus;
import com.nicico.bpmsclient.model.flowable.enums.SortType;
import com.nicico.bpmsclient.model.flowable.process.ProcessDefinitionRequestDTO;
import com.nicico.bpmsclient.model.flowable.process.ProcessInsHistoryDTO;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.GridDTO;
import com.nicico.bpmsclient.model.flowable.task.TaskDetail;
import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.bpmsclient.model.request.TaskSearchDto;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.copper.oauth.common.repository.OAUserDAO;
import com.nicico.internal.sales.util.TextUtility;
import com.nicico.internal.sales.wf.dto.WorkflowDto;
import com.nicico.internal.sales.wf.dto.mapper.WorkflowMapper;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {
	private final BpmsClientService bpmsClientService;
	private final OAUserDAO oaUserDAO;
	private final WorkflowRepository workflowRepository;
	private final WorkflowMapper workflowMapper;

	@Override
	public List<TaskInfo> getInstanceTasks(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) return null;
		var tasks = bpmsClientService.getProcessInstanceTasks(processInstanceId);
		tasks.forEach(task -> oaUserDAO.findById(Long.valueOf(task.getAssignee())).ifPresent(user -> task.setAssignee(user.getFullName())));
		return tasks;
	}

	@Override
	public GridDTO searchTaskInbox(TaskSearchDto taskSearchDto, int page, int size) {
		String currentUserId = SecurityUtil.getUserId().toString();
		taskSearchDto.setUserIds(List.of(currentUserId));
		taskSearchDto.setWithLastComment(false);
		GridDTO result = bpmsClientService.infiniteSearchTask(taskSearchDto, page, size);
		if (result == null || result.getData() == null) return null;
		String marker = "ثبت پیش فاکتور";
		result.getData().removeIf(task -> {
			String name = task.getName();
			if (!name.contains(marker)) return false;
			Map<?, ?> details = task.getInstanceDetails();
			if (details != null) {
				Object starterObj = details.get("starter");
				String starter = starterObj == null ? null : starterObj.toString();
				return !currentUserId.equals(starter);
			}
			return true;
		});
		return result.getData().isEmpty() ? null : result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> searchProcess(Pageable pageable) {
		try {
			var dto = new ProcessDefinitionRequestDTO();
			dto.setSortType(SortType.DESC);
			dto.setProcessDefinitionStatus(ProcessDefinitionStatus.ACTIVE);
			dto.setTenantId("internal-sales");
			Map<String, Object> result = (Map<String, Object>) bpmsClientService.searchProcess(dto, pageable.getPageNumber(), pageable.getPageSize());
			return (List<Map<String, Object>>) result.getOrDefault("content", Collections.emptyList());
		} catch (BPMNClientException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public ProcessInstance cancelProcessInstance(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) return null;
		try {
			return bpmsClientService.cancelProcessInstance(processInstanceId);
		} catch (BPMNClientException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean checkProcessInstanceEnded(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) return false;
		try {
			return bpmsClientService.checkProcessInstanceEnded(processInstanceId);
		} catch (BPMNClientException e) {
			log.error(e.getMessage());
			return true;
		}
	}

	@Override
	public TaskDetail getTaskDetail(String taskId) {
		if (!TextUtility.isValidUUID(taskId)) return null;
		try {
			return bpmsClientService.getTaskDetail(taskId);
		} catch (BPMNClientException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public ProcessInsHistoryDTO getProcessInstanceHistory(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) return null;
		try {
			return bpmsClientService.getProcessInstanceHistory(processInstanceId);
		} catch (BPMNClientException e) {
			log.error("Error fetching process instance history: {}", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public ProcessInstanceHistory getProcessInstanceHistoryById(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) return null;
		try {
			return bpmsClientService.getProcessInstanceHistoryById(processInstanceId);
		} catch (BPMNClientException e) {
			log.error("Error fetching process instance history: {}", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTasksReport(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) return null;
		var report = bpmsClientService.getUserTasksReport(List.of(processInstanceId));
		report.values().forEach(taskList -> taskList.forEach(task -> oaUserDAO.findById(Long.valueOf(task.getAssignee())).ifPresent(user -> task.setAssignee(user.getFullName()))));
		return report;
	}

	@Override
	public Long taskCount() {
		try {
			TaskSearchDto dto = new TaskSearchDto();
			dto.setUserIds(List.of(SecurityUtil.getUserId().toString()));
			return bpmsClientService.taskCount(dto);
		} catch (BPMNClientException e) {
			log.error(e.getMessage());
			return 0L;
		}
	}

	@Override
	public String createProcess(WorkflowDto.Create dto) {
		if (workflowRepository.existsById(dto.getId())) {
			return "";
		}
		var workflowModel = workflowMapper.fromDTO(dto);
		workflowRepository.save(workflowModel);
		return dto.getId();
	}


}
