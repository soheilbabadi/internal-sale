package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtraBillProcessServiceImpl implements ExtraBillProcessService {

	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند برات الکترونیک را ندارید";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور پیدا نشد";
	private static final String PROFORMA_DUPLICATE_START = "برای این پیش فاکتور قبلا برات صادر شده است";
	private static final String LC_ALREADY_EXISTS = "برای این پیش فاکتور اعتبار اسنادی فعال وجود دارد";
	private static final String PROCESS_ID_PLACEHOLDER = "-";
	private static final String MSG_PROFORMA_NOT_ACCEPTED = "پیش فاکتور در وضعیت تایید شده نیست";
	private static final String MSG_PROFORMA_MASTER_NOT_FOUND = "قرارداد فروش وجود ندارد";
	private static final String MSG_PROFORMA_DETAIL_CANCELED = " پیش فاکتور ابطال شده است";
	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final ExtraBillRepository extraBillRepository;
	private final LcRepository lcRepository;
	private final AcknowledgmentDeterminer acknowledgmentDeterminer;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final ProformaDetailRepository proformaDetailRepository;

	@Override
	@Transactional
	public ProcessInstance startProcess(Long masterId) {


		validateAccess();
		ProformaMasterModel proformaMaster = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(PROFORMA_NOT_FOUND_MESSAGE));
		validateNoActiveExtraBill(masterId);
		StartProcessWithDataDTO startProcessDto = buildStartProcessDto(proformaMaster);
		ProcessInstance processInstance = startProcessWithData(startProcessDto);
		List<ExtraBankBillModel> billModels = buildExtraBankBills(proformaMaster, processInstance);
		extraBillRepository.saveAll(billModels);
		return processInstance;
	}


	@Override
	@Transactional
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		try {
			startProcessDto.setProcessDefinitionKey(processVariableProvider.getExtraBillWorkflowByTitle().getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex.fillInStackTrace());
		}
		return null;
	}


	// -------------------------------------------------------------------------
	// Process start helpers
	// -------------------------------------------------------------------------

	private void validateNoActiveExtraBill(Long masterId) {

		ProformaMasterModel master = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_MASTER_NOT_FOUND));

		if (master.getWorkflowApproveStatus() != WorkflowApproveStatus.ACCEPTED) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_NOT_ACCEPTED);
		}

		ProformaDetailModel detail = proformaDetailRepository.findById(master.getProformaDetailModelLists().get(0).getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));

		if (detail.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_CANCELED);
		}


		List<LcModel> lcs = lcRepository.findAllByProformaMasterId(masterId);

		long activeLcCount = lcs.stream()
				.filter(lc -> lc.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED)
				.count();

		if (activeLcCount > 0) {
			throw new InternalSaleCustomException.ValidationException(LC_ALREADY_EXISTS);
		}

		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProformaMasterId(masterId);

		long nonCanceledCount = bills.stream()
				.filter(bill -> bill.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED)
				.count();

		if (nonCanceledCount > 0) {
			throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
		}

	}
	private List<ExtraBankBillModel> buildExtraBankBills(ProformaMasterModel proformaMaster, ProcessInstance processInstance) {

		List<ExtraBankBillModel> billModels = new ArrayList<>();
		for (ProformaDetailModel detailModel : proformaMaster.getProformaDetailModelLists()) {
			ExtraBankBillModel bankBillModel = new ExtraBankBillModel();
			bankBillModel.setProcessId(processInstance.getId());
			bankBillModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
			bankBillModel.setReversalProcessId(PROCESS_ID_PLACEHOLDER);
			bankBillModel.setReckoningSend(false);
			bankBillModel.setProformaMasterId(proformaMaster.getId());
			bankBillModel.setProformaDetailId(detailModel.getId());
			bankBillModel.setAcknowledgment(Acknowledgment.RECKONING);
			bankBillModel.setTradeId(proformaMaster.getTradeId());
			bankBillModel.setContractNo(proformaMaster.getContractNo());
			billModels.add(bankBillModel);
		}

		return billModels;
	}


	private StartProcessWithDataDTO buildStartProcessDto(ProformaMasterModel proformaMaster) {
		StartProcessWithDataDTO dto = new StartProcessWithDataDTO();
		dto.setProcessDefinitionKey(processVariableProvider.getExtraBillWorkflowByTitle().getDefinitionKey());
		dto.setVariables(processVariableProvider.createExtraBillRequestVariables(processVariableProvider.buildExtraBillVariablesInput(proformaMaster)));

		return dto;
	}


	// -------------------------------------------------------------------------
	// Task actions
	// -------------------------------------------------------------------------

	@Override
	@Transactional
	public void approveTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(true);
		ReviewTaskRequest reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(taskActionDto);
		reviewTask(reviewTaskRequest);
	}


	@Override
	@Transactional
	public void rejectTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(false);
		ReviewTaskRequest reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(taskActionDto);
		reviewTask(reviewTaskRequest);
	}

	private void reviewTask(ReviewTaskRequest reviewTaskRequest) {
		bpmsClientService.reviewTask(reviewTaskRequest);

		String processInstanceId = reviewTaskRequest.getProcessInstanceId();
		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProcessId(processInstanceId);

		if (Boolean.FALSE.equals(reviewTaskRequest.getApprove())) {
			cancelBills(bills);
		} else {
			approveBills(bills, processInstanceId);
		}

		extraBillRepository.saveAll(bills);
	}

	private void cancelBills(List<ExtraBankBillModel> bills) {
		bills.forEach(bill -> {
			bill.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
			bill.setAcknowledgment(Acknowledgment.CANCELED);
		});
	}

	private void approveBills(List<ExtraBankBillModel> bills, String processInstanceId) {
		boolean acceptedFinally = processVariableProvider.isProcessAcceptedFinally(processInstanceId);

		bills.forEach(bill -> {
			bill.setAcknowledgment(bill.getAcknowledgment() == Acknowledgment.RECKONING
					? Acknowledgment.REMITTANCE
					: acknowledgmentDeterminer.determine(bill));

			if (acceptedFinally) {
				bill.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
				bill.setAcknowledgment(Acknowledgment.FINISHED);
			}
		});
	}


	@Override
	public boolean canStartProcess() {

		var workflow = processVariableProvider.getExtraBillWorkflowByTitle();
		return processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle())
				.stream()
				.anyMatch(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
						&& ExtraBillProcessVariable.BillDraftRegistration.name().equalsIgnoreCase(access.getProcessVariable()));

	}


	private void validateAccess() {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ACCESS_DENIED_MESSAGE);
		}
	}


	@Override
	public void refreshStatus() {
		var masterIds = extraBillRepository
				.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS))
				.stream()
				.map(ExtraBankBillModel::getId)
				.toList();

		for (Long masterId : masterIds) {
			try {
				refreshOne(masterId);
			} catch (Exception ex) {
				log.error("Error while refreshing status for extra bill master id={}", masterId, ex);
			}
		}
	}


	public void refreshOne(Long masterId) {
		ExtraBankBillModel master = extraBillRepository.findById(masterId)
				.orElseThrow(() -> new EntityNotFoundException("ExtraBankBillModel not found: " + masterId));

		Acknowledgment determined = acknowledgmentDeterminer.determine(master);
		if (master.getAcknowledgment() != determined) {
			master.setAcknowledgment(determined);
		}

		if (master.getPmsBillId() != null) {
			master.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			master.setAcknowledgment(Acknowledgment.FINISHED);
			extraBillRepository.save(master);
			return;
		}
		var processHistory = bpmsClientService.getProcessInstanceHistoryById(master.getProcessId());
		switch (processHistory.getStatus()) {
			case ACTIVE -> master.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
			case CANCELED -> {
				master.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
				master.setAcknowledgment(Acknowledgment.CANCELED);
			}
			case FINISHED -> {
				boolean acceptedFinally = processVariableProvider.isProcessAcceptedFinally(master.getProcessId());
				if (acceptedFinally) {
					master.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					master.setAcknowledgment(Acknowledgment.FINISHED);
				} else {
					master.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
					master.setAcknowledgment(Acknowledgment.CANCELED);
				}
			}
			default -> {
				master.setWorkflowApproveStatus(WorkflowApproveStatus.DRAFT);
				master.setAcknowledgment(Acknowledgment.UNKNOWN);
			}
		}

		extraBillRepository.save(master);
	}
}

