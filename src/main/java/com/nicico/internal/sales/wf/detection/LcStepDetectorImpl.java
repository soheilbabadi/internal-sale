package com.nicico.internal.sales.wf.detection;

import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LcStepDetectorImpl implements LcStepDetector {

	private static final String APPROVED_KEY = "approved";
	private final ProcessHistoryService processHistoryService;
	private final LcRepository lcRepository;

	@Override
	public void updateAllAcknowledgments() {
		List<LcModel> all = lcRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS));
		for (LcModel item : all) {
			updateStep(item);
		}
	}

	@Override
	public Acknowledgment updateStep(LcModel lcModel) {
		Acknowledgment acknowledgment = detectStep(lcModel);
		lcModel.setAcknowledgment(acknowledgment);
		lcRepository.saveAndFlush(lcModel);
		return acknowledgment;
	}

	@Override
	public Acknowledgment detectStep(LcModel LcModel) {

		Map<String, List<UserTaskReportDTO>> report = processHistoryService.getUserTaskReportOrEmpty(LcModel.getProcessId());
		if (report.isEmpty()) return Acknowledgment.FINISHED;

		List<UserTaskReportDTO> allActivities = report.values().stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.filter(task -> task.getActivityName() != null)
				.toList();

		if (allActivities.isEmpty()) {
			return Acknowledgment.FINISHED;
		}

		WorkflowApproveStatus status = LcModel.getWorkflowApproveStatus();
		if (status == WorkflowApproveStatus.ACCEPTED) {
			return Acknowledgment.FINISHED;
		}
		if (status == WorkflowApproveStatus.CANCELED || status == WorkflowApproveStatus.REVERSAL) {
			return Acknowledgment.CANCELED;
		}


		Optional<UserTaskReportDTO> currentActiveTask = allActivities.stream()
				.filter(task -> task.getEndDate() == null)
				.findFirst();

		if (currentActiveTask.isPresent()) {
			LcProcessVariable currentStep = resolveActivityVariable(currentActiveTask.get());
			return resolveCurrentAcknowledgment(currentStep);
		}

		return resolveFromLatestCompletedTask(allActivities);
	}

	private Acknowledgment resolveCurrentAcknowledgment(LcProcessVariable step) {
		return switch (step) {
			case CreditBridge -> Acknowledgment.RECKONING;
			case SettleSure -> Acknowledgment.RECKONING;    // تسویه
			case RemitSure -> Acknowledgment.REMITTANCE;   // حواله
			case FinalCheck -> Acknowledgment.FINAL_CHECK;  // بررسی نهایی
			default -> Acknowledgment.FINAL_CHECK;
		};
	}

	private Acknowledgment resolveFromLatestCompletedTask(List<UserTaskReportDTO> activities) {
		boolean hasFinalCheck = activities.stream().anyMatch(t ->
				isActivityType(t, LcProcessVariable.FinalCheck) && isApproved(t));
		if (hasFinalCheck) {
			return Acknowledgment.FINISHED;
		}

		boolean hasRemittanceApproved = activities.stream().anyMatch(t ->
				isActivityType(t, LcProcessVariable.RemitSure) && isApproved(t));
		if (hasRemittanceApproved) {
			return Acknowledgment.FINAL_CHECK;
		}

		boolean hasSettleApproved = activities.stream().anyMatch(t ->
				isActivityType(t, LcProcessVariable.SettleSure) && isApproved(t));
		if (hasSettleApproved) {
			return Acknowledgment.REMITTANCE;
		}

		return Acknowledgment.RECKONING;
	}

	private boolean isActivityType(UserTaskReportDTO activity, LcProcessVariable processVariable) {
		LcProcessVariable detected = resolveActivityVariable(activity);
		return detected == processVariable;
	}

	private LcProcessVariable resolveActivityVariable(UserTaskReportDTO activity) {
		if (activity == null || activity.getActivityName() == null) {
			return null;
		}
		String activityName = activity.getActivityName().trim();
		for (LcProcessVariable variable : LcProcessVariable.values()) {
			if (variable.getValue().equals(activityName)) {
				return variable;
			}
		}
		return LcProcessVariable.FinalCheck;
	}

	private boolean isApproved(UserTaskReportDTO activity) {
		Map<String, Object> localVars = activity.getLocalVariable();
		return localVars != null
				&& localVars.containsKey(APPROVED_KEY)
				&& Boolean.TRUE.equals(localVars.get(APPROVED_KEY));
	}


}