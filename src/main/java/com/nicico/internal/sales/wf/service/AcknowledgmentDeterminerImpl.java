package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.util.TextUtility;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
import com.nicico.internal.sales.wf.enums.GaamProcessVariable;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AcknowledgmentDeterminerImpl implements AcknowledgmentDeterminer {

	private static final String APPROVED_KEY = "approved";

	private final ProcessService processService;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;

	private final LcHelper lcHelper = new LcHelper();
	private final GaamHelper gaamHelper = new GaamHelper();
	private final ExtraBillHelper extraBillHelper = new ExtraBillHelper();

	@Override
	public Acknowledgment determine(ExtraBankBillModel extraBankBillModel) {
		return extraBillHelper.determine(extraBankBillModel);
	}

	@Override
	public Acknowledgment determine(GaamModel gaamModel) {
		return gaamHelper.determine(gaamModel);
	}

	@Override
	public Acknowledgment determine(LcModel lcModel) {
		return lcHelper.determine(lcModel);
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

	// --- Shared Common Logic ---

	private boolean isApproved(UserTaskReportDTO activity) {
		Map<String, Object> localVars = activity.getLocalVariable();
		return localVars != null
				&& localVars.containsKey(APPROVED_KEY)
				&& Boolean.TRUE.equals(localVars.get(APPROVED_KEY));
	}

	// Unused methods removed as logic is now encapsulated in helpers
	private Acknowledgment resolveAcknowledgmentFromStep(ExtraBillProcessVariable step) {
		if (step == null) return Acknowledgment.UNKNOWN;
		return switch (step) {
			case BillDraftRegistration -> Acknowledgment.RECKONING;
			case BillSettleSure -> Acknowledgment.REMITTANCE;
			case BillFinalCheck -> Acknowledgment.FINISHED;
			default -> Acknowledgment.UNKNOWN;
		};
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

	private Acknowledgment resolveAcknowledgmentFromStep(LcProcessVariable step) {
		if (step == null) return Acknowledgment.UNKNOWN;
		return switch (step) {
			case RemitSure -> Acknowledgment.REMITTANCE;
			case FinalCheck -> Acknowledgment.FINISHED;
			case CreditBridge, SettleSure -> Acknowledgment.RECKONING;
			default -> Acknowledgment.UNKNOWN;
		};
	}

	private LcProcessVariable detectLcStep(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) {
			return null;
		}
		try {
			List<TaskInfo> tasks = bpmsClientService.getProcessInstanceTasks(processInstanceId);
			if (tasks == null || tasks.isEmpty()) {
				return null;
			}
			String taskName = tasks.get(0).getName();
			return Arrays.stream(LcProcessVariable.values())
					.filter(v -> v.getValue().equals(taskName) || v.name().equalsIgnoreCase(taskName))
					.findFirst()
					.orElse(null);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Helper class containing all logic specific to LC Acknowledgment determination.
	 */
	private class LcHelper {
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

			if (hasApprovedRemittance(allActivities, LcProcessVariable.RemitSure)) {
				return Acknowledgment.REMITTANCE;
			}

			if (hasApprovedReckoning(allActivities, LcProcessVariable.SettleSure)
					&& lcModel.getAcknowledgment() != Acknowledgment.REMITTANCE) {
				return Acknowledgment.RECKONING;
			}

			return Acknowledgment.UNKNOWN;
		}

		private boolean hasApprovedReckoning(List<UserTaskReportDTO> activities, LcProcessVariable processVariable) {
			return activities.stream().anyMatch(activity ->
					isActivityType(activity, processVariable) && isApproved(activity)
			);
		}

		private boolean hasApprovedRemittance(List<UserTaskReportDTO> activities, LcProcessVariable processVariable) {
			return activities.stream().anyMatch(activity ->
					isActivityType(activity, processVariable) && isApproved(activity)
			);
		}

		private boolean isActivityType(UserTaskReportDTO activity, LcProcessVariable processVariable) {
			if (activity == null || activity.getActivityName() == null) {
				return false;
			}
			return processVariable.getValue().equals(activity.getActivityName());
		}
	}

	/**
	 * Helper class containing all logic specific to GAAM Acknowledgment determination.
	 */
	private class GaamHelper {
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

			if (hasApprovedRemittance(allActivities, GaamProcessVariable.GaamRemitSure)
					|| gaamModel.getAgentBankName() != null && gaamModel.getAcknowledgment() == Acknowledgment.RECKONING) {
				return Acknowledgment.REMITTANCE;
			}

			if (hasApprovedReckoning(allActivities, GaamProcessVariable.GaamSettleSure)
					&& gaamModel.getAcknowledgment() != Acknowledgment.RECKONING) {
				return Acknowledgment.RECKONING;
			}

			return Acknowledgment.UNKNOWN;
		}

		private boolean hasApprovedReckoning(List<UserTaskReportDTO> activities, GaamProcessVariable processVariable) {
			return activities.stream().anyMatch(activity ->
					isActivityType(activity, processVariable) && isApproved(activity)
			);
		}

		private boolean hasApprovedRemittance(List<UserTaskReportDTO> activities, GaamProcessVariable processVariable) {
			return activities.stream().anyMatch(activity ->
					isActivityType(activity, processVariable) && isApproved(activity)
			);
		}

		private boolean isActivityType(UserTaskReportDTO activity, GaamProcessVariable processVariable) {
			if (activity == null || activity.getActivityName() == null) {
				return false;
			}
			return processVariable.getValue().equals(activity.getActivityName());
		}
	}

	/**
	 * Helper class containing all logic specific to ExtraBill Acknowledgment determination.
	 */
	private class ExtraBillHelper {
		public Acknowledgment determine(ExtraBankBillModel extraBankBillModel) {
			Map<String, List<UserTaskReportDTO>> report = getUserTaskReportOrEmpty(extraBankBillModel.getProcessId());

			List<UserTaskReportDTO> allActivities = report.values().stream()
					.filter(Objects::nonNull)
					.flatMap(List::stream)
					.filter(Objects::nonNull)
					.toList();

			if (extraBankBillModel.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
				return Acknowledgment.FINISHED;
			}

			if (extraBankBillModel.getWorkflowApproveStatus() == WorkflowApproveStatus.CANCELED
					|| extraBankBillModel.getWorkflowApproveStatus() == WorkflowApproveStatus.REVERSAL) {
				return Acknowledgment.CANCELED;
			}

			if (hasApprovedFinalCheck(allActivities)) {
				return Acknowledgment.FINAL_CHECK;
			}

			if (hasApprovedRemittance(allActivities, ExtraBillProcessVariable.BillRemitSure)
					|| extraBankBillModel.getAgentBankName() != null && extraBankBillModel.getAcknowledgment() == Acknowledgment.RECKONING) {
				return Acknowledgment.REMITTANCE;
			}

			if (hasApprovedReckoning(allActivities, ExtraBillProcessVariable.BillSettleSure)
					&& extraBankBillModel.getAcknowledgment() != Acknowledgment.RECKONING) {
				return Acknowledgment.RECKONING;
			}

			return Acknowledgment.UNKNOWN;
		}

		private boolean hasApprovedReckoning(List<UserTaskReportDTO> activities, ExtraBillProcessVariable processVariable) {
			return activities.stream().anyMatch(activity ->
					isActivityType(activity, processVariable) && isApproved(activity)
			);
		}

		private boolean hasApprovedRemittance(List<UserTaskReportDTO> activities, ExtraBillProcessVariable processVariable) {
			return activities.stream().anyMatch(activity ->
					isActivityType(activity, processVariable) && isApproved(activity)
			);
		}

		private boolean isActivityType(UserTaskReportDTO activity, ExtraBillProcessVariable processVariable) {
			if (activity == null || activity.getActivityName() == null) {
				return false;
			}
			return processVariable.getValue().equals(activity.getActivityName());
		}
	}

}
