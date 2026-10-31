package com.nicico.internal.sales.wf.detection;

import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.wf.enums.GaamProcessVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GaamStepDetectorImpl implements GaamStepDetector {

	private static final String APPROVED_KEY = "approved";
	private final ProcessHistoryService processHistoryService;
	private final GaamRepository gaamRepository;

	@Override
	public Acknowledgment updateStep(GaamModel gaamModel) {
		Acknowledgment acknowledgment = detectStep(gaamModel);
		if (gaamModel.getAcknowledgment() == acknowledgment) return acknowledgment;
		gaamModel.setAcknowledgment(acknowledgment);
		gaamRepository.saveAndFlush(gaamModel);
		return acknowledgment;
	}

	@Override
	public void updateAllAcknowledgments() {
		List<GaamModel> gaamModels = gaamRepository.findAll();

		for (GaamModel item : gaamModels) {
			if (item.getAcknowledgment() == Acknowledgment.CANCELED || item.getAcknowledgment() == Acknowledgment.FINISHED)
				return;
			updateStep(item);
		}
	}

	@Override
	public Acknowledgment detectStep(GaamModel gaamModel) {
		if (gaamModel == null) {
			return Acknowledgment.FINISHED;
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
		Map<String, List<UserTaskReportDTO>> report = processHistoryService.getUserTaskReportOrEmpty(gaamModel.getProcessId());
		if (report.isEmpty()) return Acknowledgment.FINISHED;

		// 3. Extract all valid tasks
		List<UserTaskReportDTO> allActivities = report.values().stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.filter(task -> task.getActivityName() != null)
				.toList();

		if (allActivities.isEmpty()) {
			return Acknowledgment.FINISHED;
		}

		// 4. Check if any task resulted in rejection/cancellation
		boolean isRejected = allActivities.stream().anyMatch(activity -> {
			Map<String, Object> localVars = activity.getLocalVariable();
			return localVars != null
					&& localVars.containsKey(APPROVED_KEY)
					&& Boolean.FALSE.equals(localVars.get(APPROVED_KEY));
		});
		if (isRejected) {
			return Acknowledgment.CANCELED;
		}

		Optional<UserTaskReportDTO> currentActiveTask = allActivities.stream()
				.filter(task -> task.getEndDate() == null)
				.findFirst();

		if (currentActiveTask.isPresent()) {
			GaamProcessVariable currentStep = resolveActivityVariable(currentActiveTask.get());
			return resolveCurrentAcknowledgment(currentStep);
		}

		return resolveFromLatestCompletedTask(allActivities);
	}

	private Acknowledgment resolveCurrentAcknowledgment(GaamProcessVariable step) {
		if (step == null) {
			return Acknowledgment.FINAL_CHECK;
		}
		return switch (step) {
			case GaamDraftRegistration, GaamSettleSure -> Acknowledgment.RECKONING;    // تسویه
			case GaamRemitSure -> Acknowledgment.REMITTANCE;   // حواله
			case GaamFinalCheck -> Acknowledgment.FINAL_CHECK;  // بررسی نهایی
		};
	}

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

		return Acknowledgment.RECKONING;
	}

	private boolean isActivityType(UserTaskReportDTO activity, GaamProcessVariable processVariable) {
		GaamProcessVariable detected = resolveActivityVariable(activity);
		return detected == processVariable;
	}

	private GaamProcessVariable resolveActivityVariable(UserTaskReportDTO activity) {
		if (activity == null || activity.getActivityName() == null) {
			return null;
		}
		String activityName = activity.getActivityName().trim();
		for (GaamProcessVariable variable : GaamProcessVariable.values()) {
			if (variable.getValue().equals(activityName)) {
				return variable;
			}
		}
		return null;
	}

	private boolean isApproved(UserTaskReportDTO activity) {
		Map<String, Object> localVars = activity.getLocalVariable();
		return localVars != null
				&& localVars.containsKey(APPROVED_KEY)
				&& Boolean.TRUE.equals(localVars.get(APPROVED_KEY));
	}



}