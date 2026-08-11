package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.task.GridDTO;
import com.nicico.bpmsclient.model.request.TaskSearchDto;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WorkflowProcessSupport {
	private static final String DEFAULT_BPMS_ERROR = "خطا در اتصال به کارتابل";

	private final BpmsClientService bpmsClientService;
	private final ProcessUserAccessRepository processUserAccessRepository;

	public void ensureAccess(boolean allowed, String message) {
		if (!allowed) {
			throw new InternalSaleCustomException.AccessDeniedException(message);
		}
	}

	public boolean hasAccess(String processTitle, String processVariable) {
		return processUserAccessRepository.findAllByProcessTitle(processTitle)
				.stream()
				.anyMatch(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
						&& processVariable.equalsIgnoreCase(access.getProcessVariable()));
	}

	public GridDTO getUserTasks(String processDefinitionId, String taskTitle, Pageable pageable) {
		var userId = SecurityUtil.getUserId().toString();
		var dto = new TaskSearchDto();
		dto.setProcessDefinitionId(processDefinitionId);
		dto.setUserId(userId);
		dto.setUserIds(Collections.singletonList(userId));
		dto.setWithLastComment(false);

		var userTasks = bpmsClientService.infiniteSearchTask(dto, pageable.getPageNumber(), pageable.getPageSize());
		if (userTasks != null && userTasks.getData() != null) {
			userTasks.getData().forEach(task -> task.setTaskDefinitionKey(taskTitle));
		}
		return userTasks;
	}

	public InternalSaleCustomException.BpmsClientException bpmsException(Exception ex) {
		return bpmsException(DEFAULT_BPMS_ERROR, ex);
	}

	public InternalSaleCustomException.BpmsClientException bpmsException(String message, Exception ex) {
		return new InternalSaleCustomException.BpmsClientException(message, List.of(ex.getMessage()));
	}
}
