package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.TextUtility;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtraBillProcessServiceImpl implements ExtraBillProcessService {

	private static final String PROCESS_TITLE_EXTRA_BILL = "EXTRA_BILL";
	private static final String BPMS_ERROR = "خطا در اتصال به کارتابل";
	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند برات الکترونیک را ندارید";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور پیدا نشد";
	private static final String PROFORMA_DUPLICATE_START = "برای این پیش فاکتور قبلا برات صادر شده است";
	private static final String LC_ALREADY_EXISTS = "برای این پیش فاکتور اعتبار اسنادی فعال وجود دارد";
	private static final String ERROR_REFRESHING_STATUS = "خطا در بروز رسانی وضعیت براتها";
	private static final String ERROR_REJECTING_EXTRA_BILL = "خطا در رد کردن فرایند {}";
	private static final String ERROR_DETECTING_STEP = "خطا در تشخیص مرحله فرایند {}";
	private static final String ERROR_HANDLING_TASK_ACTION = "خطا در انجام عملیات تسک {}";
	private static final String PROCESS_ID_PLACEHOLDER = "-";
	private final ObjectProvider<ExtraBillProcessService> self;

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final ProcessVariableProvider processVariableProvider;
	private final ExtraBillRepository extraBillRepository;
	private final LcRepository lcRepository;


	// -------------------------------------------------------------------------
	// Process lifecycle
	// -------------------------------------------------------------------------

	@Override
	@Transactional
	public ProcessInstance startExtraBillProcess(Long masterId) {

		refreshExtraBillStatus();
		validateAccess();

		ProformaMasterModel proformaMaster = getProformaMasterOrThrow(masterId);
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

		boolean hasActiveLc = lcRepository.findAllByProformaMasterId(masterId)
				.stream()
				.anyMatch(lc -> lc.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED
						&& lc.getWorkflowApproveStatus() != WorkflowApproveStatus.REVERSAL);

		if (hasActiveLc) {
			throw new InternalSaleCustomException.ValidationException(LC_ALREADY_EXISTS);
		}

		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProformaMasterId(masterId);
		for (ExtraBankBillModel bill : bills) {
			if (bill.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED) {
				throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
			}
			if (bill.getProcessId() != null && !bill.getProcessId().equalsIgnoreCase(PROCESS_ID_PLACEHOLDER) && !processVariableProvider.isProcessFinished(bill.getProcessId())) {
				throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
			}
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
		dto.setVariables(processVariableProvider.createExtraBillRequestVariables(buildExtraBillVariablesInput(proformaMaster)));

		return dto;
	}


	private ProformaVariablesInput buildExtraBillVariablesInput(
			ProformaMasterModel proformaMaster) {

		ProformaVariablesInput input = new ProformaVariablesInput();

		input.setProformaMasterId(proformaMaster.getId());
		input.setContractDate(proformaMaster.getContractDate());
		input.setGoodId(proformaMaster.getGoodId());
		input.setGoodName(proformaMaster.getGoodName());
		input.setCustomerName(proformaMaster.getCustomerName());
		input.setContractNo(
				String.valueOf(proformaMaster.getContractNo())
		);
		input.setCommission(
				proformaMaster.getCommissionPercentage()
		);

		return input;
	}


	private ProformaMasterModel getProformaMasterOrThrow(Long masterId) {
		return proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(PROFORMA_NOT_FOUND_MESSAGE));
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

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void refreshExtraBillStatus() {

		List<ExtraBankBillModel> billModels = extraBillRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS));
		for (ExtraBankBillModel bankBillModel : billModels) {
			self.getObject().refreshSingleBillStatus(bankBillModel.getId());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void refreshSingleBillStatus(Long billId) {

		try {
			extraBillRepository.findById(billId).ifPresent(bill -> {
				applyCurrentStatus(bill);
				extraBillRepository.saveAndFlush(bill);
			});

		} catch (Exception ex) {
			log.error(ERROR_REFRESHING_STATUS, billId, ex);
		}
	}

	private void reviewTask(ReviewTaskRequest reviewTaskRequest) {

		try {

			bpmsClientService.reviewTask(reviewTaskRequest);

			String processInstanceId = reviewTaskRequest.getProcessInstanceId();
			if (Boolean.TRUE.equals(reviewTaskRequest.getApprove())) {
				updateBillsStatus(processInstanceId);
			} else {
				rejectExtraBill(processInstanceId);
			}

		} catch (Exception ex) {
			log.error(ERROR_HANDLING_TASK_ACTION, reviewTaskRequest.getTaskId(), ex);
		}
	}

	private void updateBillsStatus(String processInstanceId) {
		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProcessId(processInstanceId);
		if (bills.isEmpty()) return;
		bills.forEach(this::applyCurrentStatus);
		extraBillRepository.saveAll(bills);
	}


	// -------------------------------------------------------------------------
	// Reject
	// -------------------------------------------------------------------------

	@Override
	public void rejectExtraBill(String processInstanceId) {

		if (!TextUtility.isValidUUID(processInstanceId)) return;

		try {
			applyToBillsByProcessId(processInstanceId, bill -> {
						bill.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
						bill.setAcknowledgment(Acknowledgment.CANCELED);
					}
			);

		} catch (Exception ex) {
			log.error(ERROR_REJECTING_EXTRA_BILL, processInstanceId, ex);
		}
	}


	/**
	 * A process instance may have multiple ExtraBankBill records
	 * because every ProformaDetail creates a separate bill record.
	 */
	private void applyToBillsByProcessId(String processInstanceId, Consumer<ExtraBankBillModel> mutator) {

		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProcessId(processInstanceId);
		if (bills.isEmpty()) return;
		bills.forEach(mutator);
		extraBillRepository.saveAll(bills);
	}


	// -------------------------------------------------------------------------
	// Status synchronization
	// -------------------------------------------------------------------------

	private void applyCurrentStatus(ExtraBankBillModel bankBillModel) {

		String processId = bankBillModel.getProcessId();

		if (processId == null || processId.equalsIgnoreCase(PROCESS_ID_PLACEHOLDER)) return;

		boolean finished = processVariableProvider.isProcessFinished(processId);

		boolean accepted = processVariableProvider.isProcessAcceptedFinally(processId);

		if (accepted) {
			bankBillModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			bankBillModel.setAcknowledgment(Acknowledgment.FINISHED);

		} else if (finished) {

			bankBillModel.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
			bankBillModel.setAcknowledgment(Acknowledgment.CANCELED);

		} else {
			bankBillModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
			Acknowledgment acknowledgment = resolveAcknowledgmentFromStep(detectExtraBillStep(processId));
			if (acknowledgment != Acknowledgment.UNKNOWN) {
				bankBillModel.setAcknowledgment(acknowledgment);
			}
		}
	}


	private Acknowledgment resolveAcknowledgmentFromStep(ExtraBillProcessVariable step) {
		if (step == null) return Acknowledgment.UNKNOWN;
		return switch (step) {
			case BillDraftRegistration -> Acknowledgment.RECKONING;
			case BillSettleSure -> Acknowledgment.REMITTANCE;
			case BillFinalCheck -> Acknowledgment.FINISHED;
			default -> Acknowledgment.UNKNOWN;
		};
	}


	// -------------------------------------------------------------------------
	// Workflow step detection
	// -------------------------------------------------------------------------

	@Override
	public ExtraBillProcessVariable detectExtraBillStep(String processInstanceId) {

		if (!TextUtility.isValidUUID(processInstanceId)) return null;

		try {

			List<TaskInfo> tasks = bpmsClientService.getProcessInstanceTasks(processInstanceId);

			if (tasks == null || tasks.isEmpty()) return null;
			String taskName = tasks.get(0).getName();
			return ExtraBillProcessVariable.fromString(taskName);

		} catch (Exception ex) {
			log.debug(ERROR_DETECTING_STEP, processInstanceId, ex);
			return null;
		}
	}

	@Override
	public ExtraBillProcessVariable detectExtraBillStep(
			long extraBillId) {

		return extraBillRepository.findById(extraBillId)
				.map(ExtraBankBillModel::getProcessId)
				.map(this::detectExtraBillStep)
				.orElse(null);
	}


	// -------------------------------------------------------------------------
	// Access
	// -------------------------------------------------------------------------

	@Override
	public boolean canStartProcess() {
//		return hasAccessForVariable(ExtraBillProcessVariable.BillDraftRegistration);
		return true;
	}


	@Override
	public boolean canFinishProcess() {
		return hasAccessForVariable(ExtraBillProcessVariable.BillFinalCheck);
	}


	private void validateAccess() {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ACCESS_DENIED_MESSAGE);
		}
	}

	private boolean hasAccessForVariable(ExtraBillProcessVariable variable) {
		return processUserAccessRepository.findAllByProcessTitle(PROCESS_TITLE_EXTRA_BILL).stream()
				.anyMatch(access ->
						Objects.equals(access.getUserId(), SecurityUtil.getUserId()) && variable.name().equalsIgnoreCase(access.getProcessVariable())
				);
	}
}
