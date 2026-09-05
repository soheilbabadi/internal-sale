package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LcAcknowledgmentDeterminerImpl implements LcAcknowledgmentDeterminer {

	private static final String APPROVED_KEY = "approved";

	private final ProcessService processService;

	@Override
	public Acknowledgment determine(LcModel lcModel) {
		Map<String, List<UserTaskReportDTO>> report = getUserTaskReportOrEmpty(lcModel.getProcessId());
		if (report.isEmpty()) {
			return Acknowledgment.UNKNOWN;
		}

		List<UserTaskReportDTO> allActivities = report.values().stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.toList();

		if (lcModel.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
			return Acknowledgment.FINISHED;
		}

		if (hasCancelledActivity(allActivities)) {
			return Acknowledgment.CANCELED;
		}

		if (hasApprovedFinalCheck(allActivities)) {
			return Acknowledgment.FINAL_CHECK;
		}

		if (hasApprovedRemittance(allActivities)) {
			return Acknowledgment.REMITTANCE;
		}

		if (hasApprovedReckoning(allActivities)) {
			return Acknowledgment.RECKONING;
		}

		return Acknowledgment.UNKNOWN;
	}

	private Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId) {
		Map<String, List<UserTaskReportDTO>> report = processService.getUserTasksReport(processInstanceId);
		return report == null ? Collections.emptyMap() : report;
	}

	private boolean hasCancelledActivity(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity -> {
			Map<String, Object> localVars = activity.getLocalVariable();
			return localVars != null
					&& localVars.containsKey(APPROVED_KEY)
					&& Boolean.FALSE.equals(localVars.get(APPROVED_KEY));
		});
	}

	private boolean hasApprovedFinalCheck(List<UserTaskReportDTO> activities) {
		return activities.get(0).getActivityName().contains("بررسی نهایی") && activities.size() > 2;
	}

	private boolean hasApprovedReckoning(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity ->
				isActivityType(activity, LcProcessVariable.SettleSure) && isApproved(activity)
		);
	}

	private boolean hasApprovedRemittance(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity ->
				isActivityType(activity, LcProcessVariable.RemitSure) && isApproved(activity)
		);
	}

	private boolean isActivityType(UserTaskReportDTO activity, LcProcessVariable processVariable) {
		if (activity == null || activity.getActivityName() == null) {
			return false;
		}
		return processVariable.getValue().equals(activity.getActivityName());
	}

	private boolean isApproved(UserTaskReportDTO activity) {
		Map<String, Object> localVars = activity.getLocalVariable();
		return localVars != null
				&& localVars.containsKey(APPROVED_KEY)
				&& Boolean.TRUE.equals(localVars.get(APPROVED_KEY));
	}



}
