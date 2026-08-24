package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.oauth.common.repository.OAUserDAO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProcessStatusDeterminerServiceImpl implements ProcessStatusDeterminerService {

	private static final String RESOURCE_NOT_FOUND_MESSAGE = "آیتم مورد درخواست وجود ندارد";
	private static final String APPROVED_KEY = "approved";

	private final LcRepository lcRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessService processService;
	private final OAUserDAO oaUserDAO;
	private final ExtraBillRepository extraBillRepository;
	private final RemittanceMasterRepository remittanceMasterRepository;


	@Override
	public ProcessInstanceHistory getLcHistoryDetail(Long lcId) {
		LcModel lcModel = findLcOrThrow(lcId);
		return getHistoryWithResolvedAssignees(lcModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getLcSummaryReport(Long lcId) {
		LcModel lcModel = findLcOrThrow(lcId);
		return getUserTaskReportOrEmpty(lcModel.getProcessId());
	}

	@Override
	public void updateLcAcknowledgment(Long lcId) {
		LcModel lcModel = findLcOrThrow(lcId);

		if (isTerminalAcknowledgment(lcModel.getAcknowledgment())) {
			return;
		}

		Acknowledgment determined = determineAcknowledgment(lcModel); // no second DB hit

		if (lcModel.getAcknowledgment() != determined) {
			lcModel.setAcknowledgment(determined);
			lcRepository.saveAndFlush(lcModel);
		}
	}

	@Override
	public Acknowledgment determineAcknowledgment(Long lcId) {
		return determineAcknowledgment(findLcOrThrow(lcId));
	}

	@Override
	public Acknowledgment determineAcknowledgment(LcModel lcModel) {
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

		if (hasApprovedFinalCheckLC(allActivities)) {
			return Acknowledgment.FINAL_CHECK;
		}

		if (hasApprovedRemittanceLC(allActivities)) {
			return Acknowledgment.REMITTANCE;
		}

		if (hasApprovedReckoningLC(allActivities)) {
			return Acknowledgment.RECKONING;
		}

		return Acknowledgment.UNKNOWN;
	}


	public Acknowledgment determineAcknowledgment(ProformaBankBillModel proformaBankBillModel) {
		Map<String, List<UserTaskReportDTO>> report = getUserTaskReportOrEmpty(proformaBankBillModel.getProcessId());
		if (report.isEmpty()) {
			return Acknowledgment.UNKNOWN;
		}

		List<UserTaskReportDTO> allActivities = report.values().stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.filter(Objects::nonNull)
				.toList();


		if (proformaBankBillModel.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
			return Acknowledgment.FINISHED;
		}

		if (hasCancelledActivity(allActivities)) {
			return Acknowledgment.CANCELED;
		}

		if (hasApprovedFinalCheckLC(allActivities)) {
			return Acknowledgment.FINAL_CHECK;
		}

		if (hasApprovedRemittanceLC(allActivities)) {
			return Acknowledgment.REMITTANCE;
		}

		if (hasApprovedReckoningLC(allActivities)) {
			return Acknowledgment.RECKONING;
		}

		return Acknowledgment.UNKNOWN;
	}


	@Override
	public ProcessInstanceHistory getProformaHistoryDetail(Long proformaMasterId) {
		ProformaMasterModel masterModel = findProformaOrThrow(proformaMasterId);
		return getHistoryWithResolvedAssignees(masterModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getProformaSummaryReport(Long proformaMasterId) {
		ProformaMasterModel masterModel = findProformaOrThrow(proformaMasterId);
		return getUserTaskReportOrEmpty(masterModel.getProcessId());
	}

	@Override
	public ProcessInstanceHistory getRemittanceHistoryDetail(Long remittanceId) {
		RemittanceMasterModel remittanceModel = findRemittanceOrThrow(remittanceId);
		return getHistoryWithResolvedAssignees(remittanceModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getRemittanceSummaryReport(Long remittanceId) {
		RemittanceMasterModel remittanceModel = findRemittanceOrThrow(remittanceId);
		return getUserTaskReportOrEmpty(remittanceModel.getProcessId());
	}

	@Override
	public ProcessInstanceHistory getProformaBankBillHistoryDetail(Long billId) {
		ProformaBankBillModel billModel = findExtraBillOrThrow(billId);
		return getHistoryWithResolvedAssignees(billModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getProformaBankBillSummaryReport(Long billId) {
		ProformaBankBillModel billModel = findExtraBillOrThrow(billId);
		return getUserTaskReportOrEmpty(billModel.getProcessId());
	}

	private LcModel findLcOrThrow(Long lcId) {
		return lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}


	private ProformaBankBillModel findExtraBillOrThrow(Long extraBillId) {
		return extraBillRepository.findById(extraBillId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}


	@Override
	public void updateAllLcAcknowledgments() {
		List<LcModel> lcList = lcRepository.findAllByWorkflowApproveStatusIn(
				List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS));

		for (LcModel lcModel : lcList) {
			if (isTerminalAcknowledgment(lcModel.getAcknowledgment())) {
				continue;
			}

			Acknowledgment determined = determineAcknowledgment(lcModel);

			if (lcModel.getAcknowledgment() != determined) {
				lcModel.setAcknowledgment(determined);
				lcRepository.saveAndFlush(lcModel);
			}
		}
	}

	private ProformaMasterModel findProformaOrThrow(Long proformaMasterId) {
		return proformaMasterRepository.findById(proformaMasterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}

	private RemittanceMasterModel findRemittanceOrThrow(Long remittanceId) {
		return remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}

	private Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId) {
		Map<String, List<UserTaskReportDTO>> report = processService.getUserTasksReport(processInstanceId);
		return report == null ? Collections.emptyMap() : report;
	}

	private ProcessInstanceHistory getHistoryWithResolvedAssignees(String processInstanceId) {
		ProcessInstanceHistory processInstanceHistory = processService.getProcessInstanceHistoryById(processInstanceId);
		if (processInstanceHistory == null || processInstanceHistory.getTaskHistoryDetailList() == null) {
			return processInstanceHistory;
		}

		processInstanceHistory.getTaskHistoryDetailList().forEach(task -> {
			Long assigneeId = parseAssigneeId(task.getAssignee());
			if (assigneeId == null) {
				return;
			}
			oaUserDAO.findById(assigneeId).ifPresent(user -> task.setAssignee(user.getFullName()));
		});
		return processInstanceHistory;
	}

	private Long parseAssigneeId(String assignee) {
		if (assignee == null || assignee.isBlank()) {
			return null;
		}
		try {
			return Long.valueOf(assignee);
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private boolean isTerminalAcknowledgment(Acknowledgment acknowledgment) {
		return acknowledgment == Acknowledgment.CANCELED || acknowledgment == Acknowledgment.FINISHED;
	}

	private boolean hasCancelledActivity(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity -> {
			Map<String, Object> localVars = activity.getLocalVariable();
			return localVars != null &&
					localVars.containsKey(APPROVED_KEY) &&
					Boolean.FALSE.equals(localVars.get(APPROVED_KEY));
		});
	}

	private boolean hasApprovedFinalCheckLC(List<UserTaskReportDTO> activities) {
		return activities.get(0).getActivityName().contains("بررسی نهایی") && activities.size() > 2;
	}

	private boolean hasApprovedReckoningLC(List<UserTaskReportDTO> activities) {
		return activities.stream().anyMatch(activity ->
				isActivityType(activity, LcProcessVariable.SettleSure) && isApproved(activity)
		);
	}

	private boolean hasApprovedRemittanceLC(List<UserTaskReportDTO> activities) {
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

	private boolean isActivityType(UserTaskReportDTO activity, ExtraBillProcessVariable processVariable) {
		if (activity == null || activity.getActivityName() == null) {
			return false;
		}
		return processVariable.getValue().equals(activity.getActivityName());
	}

	private boolean isApproved(UserTaskReportDTO activity) {
		Map<String, Object> localVars = activity.getLocalVariable();
		return localVars != null &&
				localVars.containsKey(APPROVED_KEY) &&
				Boolean.TRUE.equals(localVars.get(APPROVED_KEY));
	}
}