package com.nicico.internal.sales.wf.detection;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.oauth.common.repository.OAUserDAO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.wf.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProcessHistoryServiceImpl implements ProcessHistoryService {

	private static final String RESOURCE_NOT_FOUND_MESSAGE = "آیتم مورد درخواست وجود ندارد";

	private final LcRepository lcRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessService processService;
	private final OAUserDAO oaUserDAO;
	private final ExtraBillRepository extraBillRepository;
	private final GaamRepository gaamRepository;
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
	public ProcessInstanceHistory getGaamHistoryDetail(Long gaamId) {
		GaamModel gaamModel = findGaamOrThrow(gaamId);
		return getHistoryWithResolvedAssignees(gaamModel.getProcessId());
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getGaamSummaryReport(Long gaamId) {
		GaamModel gaamModel = findGaamOrThrow(gaamId);
		return getUserTaskReportOrEmpty(gaamModel.getProcessId());
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

	private GaamModel findGaamOrThrow(Long gaamId) {
		return gaamRepository.findById(gaamId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}

	private LcModel findLcOrThrow(Long lcId) {
		return lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}
	private ExtraBankBillModel findExtraBillOrThrow(Long extraBillId) {
		return extraBillRepository.findById(extraBillId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}


	private RemittanceMasterModel findRemittanceOrThrow(Long remittanceId) {
		return remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
	}


	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTaskReportOrEmpty(String processInstanceId) {
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



}