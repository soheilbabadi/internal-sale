package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.oauth.common.repository.OAUserDAO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProcessStatusDeterminerServiceImpl implements ProcessStatusDeterminerService {

	private static final String RESOURCE_NOT_FOUND_MESSAGE = "آیتم مورد درخواست وجود ندارد";

	private final LcRepository lcRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessService processService;
	private final OAUserDAO oaUserDAO;
	private final ExtraBillRepository extraBillRepository;
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final LcAcknowledgmentDeterminer lcAcknowledgmentDeterminer;
	private final ExtraBillAcknowledgmentDeterminerImpl extraBillAcknowledgmentDeterminer;


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
	public Acknowledgment determineAcknowledgment(LcModel lcModel) {
		return lcAcknowledgmentDeterminer.determine(lcModel);
	}


	@Override
	public Acknowledgment determineAcknowledgment(ExtraBankBillModel extraBankBillModel) {
		return extraBillAcknowledgmentDeterminer.determine(extraBankBillModel);
	}


	@Override
	public ProcessInstanceHistory getProformaHistoryDetail(Long proformaMasterId) {
		ProformaMasterModel masterModel = proformaMasterRepository.findById(proformaMasterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
		return getHistoryWithResolvedAssignees(masterModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getProformaSummaryReport(Long proformaMasterId) {
		ProformaMasterModel masterModel = proformaMasterRepository.findById(proformaMasterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
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
		ExtraBankBillModel billModel = findExtraBillOrThrow(billId);
		return getHistoryWithResolvedAssignees(billModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getProformaBankBillSummaryReport(Long billId) {
		ExtraBankBillModel billModel = findExtraBillOrThrow(billId);
		return getUserTaskReportOrEmpty(billModel.getProcessId());
	}

	private LcModel findLcOrThrow(Long lcId) {
		return lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}


	private ExtraBankBillModel findExtraBillOrThrow(Long extraBillId) {
		return extraBillRepository.findById(extraBillId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}


	@Override
	public void updateAllExtraBillAcknowledgments() {
		List<ExtraBankBillModel> extraBankBillModels = extraBillRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS));

		for (ExtraBankBillModel bankBillModel : extraBankBillModels) {
			if (isTerminalAcknowledgment(bankBillModel.getAcknowledgment())) {
				continue;
			}

			Acknowledgment determined = determineAcknowledgment(bankBillModel);

			if (bankBillModel.getAcknowledgment() != determined) {
				bankBillModel.setAcknowledgment(determined);
				try {
					extraBillRepository.saveAndFlush(bankBillModel);
				} catch (ObjectOptimisticLockingFailureException ex) {
					log.debug("Skipping concurrent extra-bill acknowledgment update for id={}", bankBillModel.getId(), ex);
				}
			}
		}
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

	@Override
	public boolean isTerminalAcknowledgment(Acknowledgment acknowledgment) {
		return acknowledgment == Acknowledgment.CANCELED || acknowledgment == Acknowledgment.FINISHED;
	}

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
				try {
					lcRepository.saveAndFlush(lcModel);
				} catch (ObjectOptimisticLockingFailureException ex) {
					log.debug("Skipping concurrent LC acknowledgment update for id={}", lcModel.getId(), ex);
				}
			}
		}
	}



}