package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.TextUtility;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.ExtraBillProcessVariable;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtraBillProcessServiceImpl implements ExtraBillProcessService {

	private static final String PROCESS_TITLE_EXTRA_BILL = "EXTRA_BILL";
	private static final String BPMS_ERROR = "خطا در اتصال به کارتابل";
	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند برات الکترونیک را ندارید";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور پیدا نشد";
	private static final String ERROR_REFRESHING_STATUS = "خطا در بروز رسانی وضعیت براتها";
	private static final String ERROR_REJECTING_EXTRA_BILL = "خطا در رد کردن فرایند {}";
	private static final String ERROR_DETECTING_STEP = "خطا در تشخیص مرحله فرایند {}";
	private static final String ERROR_HANDLING_TASK_ACTION = "خطا در انجام عملیات تسک {}";

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final ProcessVariableProvider processVariableProvider;
	private final ExtraBillRepository extraBillRepository;

	@Override
	@Transactional
	public ProcessInstance startExtraBillProcess(Long masterId) {
		validateAccess();
		
		ProformaMasterModel proformaMaster = getProformaMasterOrThrow(masterId);
		
		StartProcessWithDataDTO startProcessDto = buildStartProcessDto(proformaMaster);
		ProcessInstance processInstance = startProcessWithData(startProcessDto);
		
		refreshExtraBillStatus();
		
		return processInstance;
	}

	@Override
	@Transactional
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		try {
			startProcessDto.setProcessDefinitionKey(
					processVariableProvider.getExtraBillWorkflowByTitle().getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);
		} catch (Exception ex) {
			throw wrapBpmsException(ex);
		}
	}

	@Override
	@Transactional
	public void approveTask(TaskActionDto taskActionDto) {
		handleTaskAction(taskActionDto, true);
	}

	@Override
	@Transactional
	public void rejectTask(TaskActionDto taskActionDto) {
		handleTaskAction(taskActionDto, false);
	}

	private void handleTaskAction(TaskActionDto dto, boolean approve) {
		dto.setApprove(approve);
		var reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(dto);
		
		try {
			bpmsClientService.reviewTask(reviewTaskRequest);

			if (!approve) {
				rejectExtraBill(reviewTaskRequest.getProcessInstanceId());
				return;
			}

			if (processVariableProvider.isProcessAcceptedFinally(reviewTaskRequest.getProcessInstanceId())) {
				acceptByProcessId(reviewTaskRequest.getProcessInstanceId());
			}

			updateBillStatusByProcessId(reviewTaskRequest.getProcessInstanceId());

		} catch (Exception ex) {
			log.error(ERROR_HANDLING_TASK_ACTION, dto.getTaskId(), ex);
			throw wrapBpmsException(ex);
		}
	}

	private void updateBillStatusByProcessId(String processInstanceId) {
		Optional<ProformaBankBillModel> billOpt = Optional.ofNullable(
				extraBillRepository.findByProcessId(processInstanceId));
		
		billOpt.ifPresent(bill -> {
			applyCurrentStatus(bill);
			extraBillRepository.save(bill);
		});
	}

	private void acceptByProcessId(String processInstanceId) {
		Optional<ProformaBankBillModel> billOpt = Optional.ofNullable(
				extraBillRepository.findByProcessId(processInstanceId));
		
		billOpt.ifPresent(bill -> {
			bill.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			bill.setAcknowledgment(Acknowledgment.FINISHED);
			extraBillRepository.save(bill);
		});
	}

	@Override
	@Transactional
	public void refreshExtraBillStatus() {
		try {
			List<ProformaBankBillModel> billModels = extraBillRepository.findAllByWorkflowApproveStatusIn(
					List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS));

			billModels.forEach(this::applyCurrentStatus);
			extraBillRepository.saveAll(billModels);

		} catch (Exception ex) {
			log.error(ERROR_REFRESHING_STATUS, ex);
		}
	}

	private void applyCurrentStatus(ProformaBankBillModel bankBillModel) {
		String processId = bankBillModel.getProcessId();
		if (processId == null) {
			return;
		}

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

	@Override
	@Transactional
	public void rejectExtraBill(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) {
			return;
		}
		
		try {
			Optional<ProformaBankBillModel> billOpt = Optional.ofNullable(
					extraBillRepository.findByProcessId(processInstanceId));
			
			billOpt.ifPresent(bill -> {
				bill.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
				bill.setAcknowledgment(Acknowledgment.CANCELED);
				extraBillRepository.save(bill);
			});
		} catch (Exception ex) {
			log.error(ERROR_REJECTING_EXTRA_BILL, processInstanceId, ex);
		}
	}

	@Override
	public boolean canStartProcess() {
		return hasAccessForVariable(ExtraBillProcessVariable.BillDraftRegistration);
	}

	private void validateAccess() {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ACCESS_DENIED_MESSAGE);
		}
	}

	private boolean hasAccessForVariable(ExtraBillProcessVariable variable) {
		return processUserAccessRepository.findAllByProcessTitle(PROCESS_TITLE_EXTRA_BILL)
				.stream()
				.anyMatch(access ->
						Objects.equals(access.getUserId(), SecurityUtil.getUserId()) &&
								variable.name().equalsIgnoreCase(access.getProcessVariable()));
	}

	@Override
	public ExtraBillProcessVariable detectExtraBillStep(String processInstanceId) {
		if (!TextUtility.isValidUUID(processInstanceId)) {
			return null;
		}
		try {
			List<TaskInfo> tasks = bpmsClientService.getProcessInstanceTasks(processInstanceId);
			if (tasks == null || tasks.isEmpty()) {
				return null;
			}
			String taskName = tasks.get(0).getName();
			return ExtraBillProcessVariable.fromString(taskName);
		} catch (Exception ex) {
			log.debug(ERROR_DETECTING_STEP, processInstanceId, ex);
			return null;
		}
	}

	@Override
	public ExtraBillProcessVariable detectExtraBillStep(long extraBillId) {
		return Optional.ofNullable(extraBillRepository.findById(extraBillId).orElse(null))
				.map(ProformaBankBillModel::getProcessId)
				.map(this::detectExtraBillStep)
				.orElse(null);
	}

	private Acknowledgment resolveAcknowledgmentFromStep(ExtraBillProcessVariable step) {
		if (step == null) {
			return Acknowledgment.UNKNOWN;
		}
		return switch (step) {
			case BillDraftRegistration -> Acknowledgment.RECKONING;
			case BillSettleSure -> Acknowledgment.REMITTANCE;
			case BillFinalCheck -> Acknowledgment.FINISHED;
			default -> Acknowledgment.UNKNOWN;
		};
	}

	private ProformaVariablesInput buildExtraBillVariablesInput(ProformaMasterModel proformaMasterModel) {
		ProformaVariablesInput input = new ProformaVariablesInput();
		input.setProformaMasterId(proformaMasterModel.getId());
		input.setContractDate(proformaMasterModel.getContractDate());
		input.setGoodId(proformaMasterModel.getGoodId());
		input.setGoodName(proformaMasterModel.getGoodName());
		input.setCustomerName(proformaMasterModel.getCustomerName());
		input.setContractNo(String.valueOf(proformaMasterModel.getContractNo()));
		input.setCommission(proformaMasterModel.getCommissionPercentage());
		return input;
	}

	private StartProcessWithDataDTO buildStartProcessDto(ProformaMasterModel proformaMaster) {
		StartProcessWithDataDTO dto = new StartProcessWithDataDTO();
		dto.setProcessDefinitionKey(processVariableProvider.getExtraBillWorkflowByTitle().getDefinitionKey());
		dto.setVariables(processVariableProvider.createExtraBillRequestVariables(
				buildExtraBillVariablesInput(proformaMaster)));
		return dto;
	}

	private ProformaMasterModel getProformaMasterOrThrow(Long masterId) {
		return proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(PROFORMA_NOT_FOUND_MESSAGE));
	}

	private InternalSaleCustomException.BpmsClientException wrapBpmsException(Exception ex) {
		return new InternalSaleCustomException.BpmsClientException(BPMS_ERROR, List.of(ex.getMessage()));
	}
}