package com.nicico.internal.sales.wf.service;


import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.wf.enums.GaamProcessVariable;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GaamProcessVariableDetectorImpl implements GaamProcessVariableDetector {

	private static final String APPROVED_KEY = "approved";


	private final ProcessService processService;
	private final BpmsClientService bpmsClientService;

	@Override
	public Acknowledgment detectStep(GaamModel gaamModel) {
		if (gaamModel == null) {
			return Acknowledgment.UNKNOWN;
		}

		// 1. Check overall workflow terminal status
		WorkflowApproveStatus status = gaamModel.getWorkflowApproveStatus();
		if (status == WorkflowApproveStatus.ACCEPTED) {
			return Acknowledgment.FINISHED;
		}
		if (status == WorkflowApproveStatus.CANCELED || status == WorkflowApproveStatus.REVERSAL) {
			return Acknowledgment.CANCELED;
		}

		// 2. Retrieve user task reports for the process instance
		Map<String, List<UserTaskReportDTO>> report = getUserTaskReportOrEmpty(gaamModel.getProcessId());
		if (report.isEmpty()) 			return Acknowledgment.UNKNOWN;

		// 3. Extract all valid tasks
		List<UserTaskReportDTO> allActivities = report.values().stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.filter(task -> task.getActivityName() != null)
				.toList();

		if (allActivities.isEmpty()) {
			return Acknowledgment.UNKNOWN;
		}

		// 4. Check if any task resulted in rejection/cancellation
		boolean isRejected = allActivities.stream().anyMatch(activity -> {
			Map<String, Object> localVars = activity.getLocalVariable();
			return localVars != null
					&& localVars.containsKey("approved")
					&& Boolean.FALSE.equals(localVars.get("approved"));
		});
		if (isRejected) {
			return Acknowledgment.CANCELED;
		}

		// 5. Look for the CURRENT ACTIVE (in-progress) task (endDate == null)
		Optional<UserTaskReportDTO> currentActiveTask = allActivities.stream()
				.filter(task -> task.getEndDate() == null)
				.findFirst();

		if (currentActiveTask.isPresent()) {
			GaamProcessVariable currentStep = GaamProcessVariable.fromString(currentActiveTask.get().getActivityName());
			return resolveCurrentAcknowledgment(currentStep);
		}

		// 6. If all tasks are completed, determine from the latest approved task
		return resolveFromLatestCompletedTask(allActivities);
	}

	/**
	 * Maps the active GaamProcessVariable step to the corresponding Acknowledgment state.
	 */
	private Acknowledgment resolveCurrentAcknowledgment(GaamProcessVariable step) {
		if (step == null) {
			return Acknowledgment.UNKNOWN;
		}
		return switch (step) {
			case GaamDraftRegistration, GaamSettleSure -> Acknowledgment.RECKONING;    // تسویه
			case GaamRemitSure                        -> Acknowledgment.REMITTANCE;   // حواله
			case GaamFinalCheck                       -> Acknowledgment.FINAL_CHECK;  // بررسی نهایی
		};
	}

	/**
	 * Fallback to resolve status from completed tasks if no active task is currently waiting.
	 */
	private Acknowledgment resolveFromLatestCompletedTask(List<UserTaskReportDTO> activities) {
		boolean hasFinalCheck = activities.stream().anyMatch(t ->
				isActivityType(t, GaamProcessVariable.GaamFinalCheck) && isApproved(t));
		if (hasFinalCheck) {
			return Acknowledgment.FINISHED;
		}

		boolean hasRemittanceApproved = activities.stream().anyMatch(t ->
				isActivityType(t, GaamProcessVariable.GaamRemitSure) && isApproved(t));
		if (hasRemittanceApproved) {
			return Acknowledgment.FINAL_CHECK;
		}

		boolean hasSettleApproved = activities.stream().anyMatch(t ->
				isActivityType(t, GaamProcessVariable.GaamSettleSure) && isApproved(t));
		if (hasSettleApproved) {
			return Acknowledgment.REMITTANCE;
		}

		return Acknowledgment.UNKNOWN;
	}

	private boolean isActivityType(UserTaskReportDTO activity, GaamProcessVariable processVariable) {
		return activity != null
				&& activity.getActivityName() != null
				&& (processVariable.getValue().equals(activity.getActivityName())
				|| processVariable.name().equalsIgnoreCase(activity.getActivityName()));
	}

	private boolean isApproved(UserTaskReportDTO activity) {
		Map<String, Object> localVars = activity.getLocalVariable();
		return localVars != null
				&& localVars.containsKey("approved")
				&& Boolean.TRUE.equals(localVars.get("approved"));
	}

	private Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId) {
		if (processInstanceId == null || processInstanceId.trim().isEmpty() || "-".equals(processInstanceId)) {
			return Collections.emptyMap();
		}
		Map<String, List<UserTaskReportDTO>> report = processService.getUserTasksReport(processInstanceId);
		return report == null ? Collections.emptyMap() : report;
	}

}