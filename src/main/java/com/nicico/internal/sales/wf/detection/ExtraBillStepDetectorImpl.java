package com.nicico.internal.sales.wf.detection;

import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
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
public class ExtraBillStepDetectorImpl implements ExtraBillStepDetector {

	private static final String APPROVED_KEY = "approved";
	private final ProcessHistoryService processHistoryService;
	private final ExtraBillRepository extraBillRepository;
	@Override
	public void updateAllAcknowledgments() {
		List<ExtraBankBillModel> all = extraBillRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS));
		for (ExtraBankBillModel item : all) {
			updateStep(item);
		}
	}
	@Override
	public Acknowledgment detectStep(ExtraBankBillModel extraBankBillModel) {
		if (extraBankBillModel == null) {
			return Acknowledgment.FINISHED;
		}

		// 1. Check overall workflow terminal status
		WorkflowApproveStatus status = extraBankBillModel.getWorkflowApproveStatus();
		if (status == WorkflowApproveStatus.ACCEPTED) {
			return Acknowledgment.FINISHED;
		}
		if (status == WorkflowApproveStatus.CANCELED || status == WorkflowApproveStatus.REVERSAL) {
			return Acknowledgment.CANCELED;
		}

		// 2. Retrieve user task reports for the process instance
		Map<String, List<UserTaskReportDTO>> report = processHistoryService.getUserTaskReportOrEmpty(extraBankBillModel.getProcessId());
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
			ExtraBillProcessVariable currentStep = resolveActivityVariable(currentActiveTask.get());
			return resolveCurrentAcknowledgment(currentStep);
		}

		return resolveFromLatestCompletedTask(allActivities);
	}

	@Override
	public Acknowledgment updateStep(ExtraBankBillModel extraBankBillModel) {
		Acknowledgment acknowledgment=  detectStep(extraBankBillModel);
		extraBankBillModel.setAcknowledgment(acknowledgment);
		extraBillRepository.saveAndFlush(extraBankBillModel);
		return  acknowledgment;
	}

	private Acknowledgment resolveCurrentAcknowledgment(ExtraBillProcessVariable step) {
		if (step == null) {
			return Acknowledgment.FINISHED;
		}
		return switch (step) {
			case BillDraftRegistration -> Acknowledgment.RECKONING;
			case BillSettleSure -> Acknowledgment.RECKONING;    // تسویه
			case BillRemitSure -> Acknowledgment.REMITTANCE;   // حواله
			case BillFinalCheck -> Acknowledgment.FINAL_CHECK;  // بررسی نهایی
		};
	}

	private Acknowledgment resolveFromLatestCompletedTask(List<UserTaskReportDTO> activities) {
		boolean hasFinalCheck = activities.stream().anyMatch(t ->
				isActivityType(t, ExtraBillProcessVariable.BillFinalCheck) && isApproved(t));
		if (hasFinalCheck) {
			return Acknowledgment.FINAL_CHECK;
		}

		boolean hasRemittanceApproved = activities.stream().anyMatch(t ->
				isActivityType(t, ExtraBillProcessVariable.BillRemitSure) && isApproved(t));
		if (hasRemittanceApproved) {
			return Acknowledgment.FINISHED;
		}

		boolean hasSettleApproved = activities.stream().anyMatch(t ->
				isActivityType(t, ExtraBillProcessVariable.BillSettleSure) && isApproved(t));
		if (hasSettleApproved) {
			return Acknowledgment.REMITTANCE;
		}

		return Acknowledgment.RECKONING;
	}

	private boolean isActivityType(UserTaskReportDTO activity, ExtraBillProcessVariable processVariable) {
		ExtraBillProcessVariable detected = resolveActivityVariable(activity);
		return detected == processVariable;
	}

	private ExtraBillProcessVariable resolveActivityVariable(UserTaskReportDTO activity) {
		if (activity == null || activity.getActivityName() == null) {
			return null;
		}
		String activityName = activity.getActivityName().trim();
		for (ExtraBillProcessVariable variable : ExtraBillProcessVariable.values()) {
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

//	private Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId) {
//		if (processInstanceId == null || processInstanceId.trim().isEmpty() || "-".equals(processInstanceId)) {
//			return Collections.emptyMap();
//		}
//		Map<String, List<UserTaskReportDTO>> report = processService.getUserTasksReport(processInstanceId);
//		return report == null ? Collections.emptyMap() : report;
//	}

}