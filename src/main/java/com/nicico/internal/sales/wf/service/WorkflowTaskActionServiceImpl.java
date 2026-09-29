package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.task.TaskDetail;
import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.util.TextUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTaskActionServiceImpl implements WorkflowTaskActionService {
	private final BpmsClientService bpmsClientService;


	/**
	 * Reads all tasks for an instance and rejects them in a loop.
	 */
	@Override
	public void rejectAllTasksByInstanceId(String instanceId ) {
		if (!TextUtility.isValidUUID(instanceId)) {
			log.warn("Invalid process instance UUID: {}", instanceId);
			return;
		}

		List<TaskInfo> tasks = bpmsClientService.getProcessInstanceTasks(instanceId);
		if (tasks == null || tasks.isEmpty()) {
			return;
		}

		for (TaskInfo task : tasks) {
			if (task.getTaskId() != null) {
				rejectTask(task.getTaskId());
			}
		}
		bpmsClientService.cancelProcessInstance(instanceId);
	}

	/**
	 * Prepares a ReviewTaskRequest for rejection (approve = false).
	 */
	private ReviewTaskRequest prepareRejectTaskRequest(String taskId ) {
		TaskDetail taskInfo = bpmsClientService.getTaskDetail(taskId);

		ReviewTaskRequest reviewTaskRequest = new ReviewTaskRequest();
		reviewTaskRequest.setTaskId(taskId);
		reviewTaskRequest.setProcessInstanceId(taskInfo.getProcessInstanceId());
		reviewTaskRequest.setUserId(String.valueOf(SecurityUtil.getUserId()));
		reviewTaskRequest.setUserName(SecurityUtil.getUsername());
		reviewTaskRequest.setApprove(false);
		reviewTaskRequest.setDescription("رد پروسه");

		return reviewTaskRequest;
	}

	/**
	 * Rejects a single task in BPMS.
	 */

	private void rejectTask(String taskId  ) {
		try {
			ReviewTaskRequest reviewTaskRequest = prepareRejectTaskRequest(taskId);
			bpmsClientService.reviewTask(reviewTaskRequest);
			log.info("Task {} rejected successfully", taskId);
		} catch (Exception ex) {
			log.error("Failed to reject task {}: {}", taskId, ex.getMessage(), ex);
			throw new InternalSaleCustomException.ValidationException("خطا در رد تسک: " + ex.getMessage());
		}
	}


}
