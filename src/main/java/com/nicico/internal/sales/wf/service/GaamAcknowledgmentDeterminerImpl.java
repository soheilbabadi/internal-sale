package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.wf.enums.GaamProcessVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GaamAcknowledgmentDeterminerImpl implements GaamAcknowledgmentDeterminer {

	private static final String APPROVED_KEY = "approved";

	private final ProcessService processService;

	@Override
	public Acknowledgment determine(GaamModel gaamModel) {
		Map<String, List<UserTaskReportDTO>> report = getUserTaskReportOrEmpty(gaamModel.getProcessId());

		List<UserTaskReportDTO> allActivities = report.values().stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.toList();

		if (gaamModel.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
			return Acknowledgment.FINISHED;
		}

		if (gaamModel.getWorkflowApproveStatus() == WorkflowApproveStatus.CANCELED
				|| gaamModel.getWorkflowApproveStatus() == WorkflowApproveStatus.REVERSAL) {
			return Acknowledgment.CANCELED;
		}

		if (hasApprovedFinalCheck(allActivities)) {
			return Acknowledgment.FINAL_CHECK;
		}

		if (hasApprovedRemittance(allActivities) || gaamModel.getAgentBankName() != null && gaamModel.getAcknowledgment() == Acknowledgment.RECKONING) {
			return Acknowledgment.REMITTANCE;
		}

		if (hasApprovedReckoning(allActivities) && gaamModel.getAcknowledgment() != Acknowledgment.RECKONING) {
			return Acknowledgment.RECKONING;
		}

		return Acknowledgment.UNKNOWN;
	}

	private Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId) {
		Map<String, List<UserTaskReportDTO>> report = processService.getUserTasksReport(processInstanceId);
		return report == null ? Collections.emptyMap() : report;
	}

	private boolean hasApprovedFinalCheck(List<UserTaskReportDTO> activities) {
		return activities.get(0).getActivityName().contains("بررسی نهایی") && activities.size() > 2;
	}

	private boolean hasApprovedReckoning(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity ->
				isActivityType(activity, GaamProcessVariable.GaamSettleSure) && isApproved(activity)
		);
	}

	private boolean hasApprovedRemittance(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity ->
				isActivityType(activity, GaamProcessVariable.GaamRemitSure) && isApproved(activity)
		);
	}

	private boolean isActivityType(UserTaskReportDTO activity, GaamProcessVariable processVariable) {
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

	private Acknowledgment resolveAcknowledgmentFromStep(GaamProcessVariable step) {
		if (step == null) return Acknowledgment.UNKNOWN;
		return switch (step) {
			case GaamDraftRegistration -> Acknowledgment.RECKONING;
			case GaamSettleSure -> Acknowledgment.REMITTANCE;
			case GaamFinalCheck -> Acknowledgment.FINISHED;
			default -> Acknowledgment.UNKNOWN;
		};
	}

}
