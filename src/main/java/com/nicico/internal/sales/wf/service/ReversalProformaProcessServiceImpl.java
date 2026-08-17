package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.proforma.service.ProformaValidationService;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.ReversalProcessVariable;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReversalProformaProcessServiceImpl implements ReversalProformaProcessService {

	public static final String PROCESS_TITLE_REVERSAL = "REVERSAL";

	private static final String BPMS_ERROR = "خطا در اتصال به کارتابل";
	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند ابطال پیش فاکتور را ندارید";
	private static final String WORKFLOW_NOT_FOUND_MESSAGE = "فرایند برگشت پیش فاکتور وجود ندارد";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "قراردادی با ای مشخصات یافت نشد";
	private static final String NO_PROFORMA_DETAILS_MESSAGE = "برای این قرارداد هیچ پیش فاکتوری ثبت نشده است";
	private static final String NO_GOOD_ITEMS_MESSAGE_TEMPLATE = "برای پیش فاکتور %d هیچ اقلام کالایی ثبت نشده است";
	private static final String TASK_ACTION_ERROR = "خطا در انجام تسک";
	private static final String ERROR_REFRESHING_STATUS = "خطا در بروز رسانی وضعیت ابطال پیش فاکتور";

	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final ProformaValidationService proformaValidationService;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final WorkflowRepository workflowRepository;
	private final ProformaDetailRepository proformaDetailRepository;

	// Process lifecycle
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, label = "start_reversal")
	public ProcessInstance startReversal(Long masterId) {
		validateAccess();

		var workflow = workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_REVERSAL)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(WORKFLOW_NOT_FOUND_MESSAGE));

		proformaValidationService.validateReversal(masterId);

		var masterModel = getProformaMasterOrThrow(masterId);
		validateReversableDetails(masterModel);

		StartProcessWithDataDTO startProcessDto = buildStartProcessDto(workflow.getDefinitionKey(), masterModel);
		ProcessInstance instance = startProcessWithData(startProcessDto);

		markDetailsReversalStatus(masterModel.getProformaDetailModelLists(), ProformaReversalStatus.CANCELED);
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
		masterModel.setReversalProcessId(instance.getId());
		proformaMasterRepository.save(masterModel);

		refreshReversalProformaStatus();
		return instance;
	}

	@Override
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		try {
			var workflow = processVariableProvider.getReversalWorkflowByTitle();
			startProcessDto.setProcessDefinitionKey(workflow.getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);
		} catch (Exception ex) {
			throw wrapBpmsException(ex);
		}
	}

	private StartProcessWithDataDTO buildStartProcessDto(String definitionKey, ProformaMasterModel masterModel) {
		var performaDetailList = masterModel.getProformaDetailModelLists();
		var firstDetail = performaDetailList.get(0);
		var firstGoodItem = firstDetail.getProformaGoodItemModels().get(0);

		ProformaVariablesInput input = new ProformaVariablesInput();
		input.setProformaMasterId(masterModel.getId());
		input.setContractDate(firstDetail.getContractDate());
		input.setGoodId(firstGoodItem.getGoodId());
		input.setGoodName(firstGoodItem.getGoodName());
		input.setCustomerName(masterModel.getCustomerName());
		input.setContractNo(String.valueOf(masterModel.getContractNo()));
		input.setCommission(masterModel.getCommissionPercentage());

		StartProcessWithDataDTO dto = new StartProcessWithDataDTO();
		dto.setProcessDefinitionKey(definitionKey);
		dto.setVariables(processVariableProvider.createReversalRequestVariables(input));
		return dto;
	}

	private void validateReversableDetails(ProformaMasterModel masterModel) {
		if (masterModel.getProformaDetailModelLists().isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(NO_PROFORMA_DETAILS_MESSAGE);
		}

		Optional<ProformaDetailModel> emptyDetail = masterModel.getProformaDetailModelLists()
				.stream()
				.filter(detail -> detail.getProformaGoodItemModels() == null ||
						detail.getProformaGoodItemModels().isEmpty())
				.findFirst();

		if (emptyDetail.isPresent()) {
			throw new InternalSaleCustomException.ValidationException(
					String.format(NO_GOOD_ITEMS_MESSAGE_TEMPLATE, emptyDetail.get().getId()));
		}
	}

	private ProformaMasterModel getProformaMasterOrThrow(Long masterId) {
		return proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
	}

	// Task actions (approve / reject a BPMS task)
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
		try {
			ReviewTaskRequest reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(dto);
			reviewTask(reviewTaskRequest);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.ValidationException(TASK_ACTION_ERROR, List.of(ex.getMessage()));
		}
	}

	@Override
	@Transactional
	public void reviewTask(ReviewTaskRequest reviewTaskRequest) {
		try {
			bpmsClientService.reviewTask(reviewTaskRequest);
			if (!reviewTaskRequest.getApprove()) {
				proformaMasterRepository.findByReversalProcessId(reviewTaskRequest.getProcessInstanceId())
						.ifPresent(this::revertReversal);
			}
		} catch (Exception ex) {
			throw wrapBpmsException(ex);
		} finally {
			refreshReversalProformaStatus();
		}
	}

	private void revertReversal(ProformaMasterModel masterModel) {
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
		markDetailsReversalStatus(masterModel.getProformaDetailModelLists(), ProformaReversalStatus.NORMAL);
		proformaMasterRepository.save(masterModel);
	}

	// Reversal status synchronization
	@Override
	@Transactional
	public void refreshReversalProformaStatus() {
		if (!canStartProcess()) {
			return;
		}
		try {
			List<ProformaMasterModel> masterModelList = proformaMasterRepository
					.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.REVERSAL));

			for (ProformaMasterModel masterModel : masterModelList) {
				applyReversalHistoryStatus(masterModel);
			}
		} catch (Exception ex) {
			log.error(ERROR_REFRESHING_STATUS, ex);
		}
	}

	private void applyReversalHistoryStatus(ProformaMasterModel masterModel) {
		String reversalId = masterModel.getReversalProcessId();
		if (Boolean.TRUE.equals(masterModel.getIsReversalProcessFinal()) || reversalId == null || "-".equals(reversalId)) {
			return;
		}

		var acceptedFinally = processVariableProvider.isProcessAcceptedFinally(reversalId);
		var status = bpmsClientService.getProcessInstanceHistoryById(reversalId);

		switch (status.getStatus()) {
			case ACTIVE -> markMasterStatus(masterModel, WorkflowApproveStatus.REVERSAL, true, false);
			case CANCELED -> markMasterStatus(masterModel, WorkflowApproveStatus.ACCEPTED, true, true);
			case FINISHED -> {
				if (acceptedFinally) {
					markMasterStatus(masterModel, WorkflowApproveStatus.REVERSAL, true, true);
				} else {
					markMasterStatus(masterModel, WorkflowApproveStatus.ACCEPTED, true, true);
				}
			}
			default -> {
				masterModel.setReversalProcessId("-");
				markMasterStatus(masterModel, WorkflowApproveStatus.ACCEPTED, true, false);
			}
		}
	}

	/**
	 * Sets the master's workflow status and finality flags, applies the matching reversal
	 * status to its details (CANCELED while the reversal stands, NORMAL once it's reverted),
	 * and persists both.
	 */
	private void markMasterStatus(ProformaMasterModel masterModel, WorkflowApproveStatus status,
	                              boolean processFinal, boolean reversalFinal) {
		masterModel.setWorkflowApproveStatus(status);
		masterModel.setIsProcessFinal(processFinal);
		masterModel.setIsReversalProcessFinal(reversalFinal);

		ProformaReversalStatus detailStatus = status == WorkflowApproveStatus.REVERSAL
				? ProformaReversalStatus.CANCELED
				: ProformaReversalStatus.NORMAL;
		markDetailsReversalStatus(masterModel.getProformaDetailModelLists(), detailStatus);

		proformaMasterRepository.save(masterModel);
	}

	private void markDetailsReversalStatus(List<ProformaDetailModel> details, ProformaReversalStatus status) {
		details.forEach(detail -> detail.setProformaReversalStatus(status));
		proformaDetailRepository.saveAll(details);
	}

	// Access control
	@Override
	public boolean canStartProcess() {
		return processUserAccessRepository.findAllByProcessTitle(PROCESS_TITLE_REVERSAL)
				.stream()
				.anyMatch(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
						&& ReversalProcessVariable.salesExpert.name().equalsIgnoreCase(access.getProcessVariable()));
	}

	private void validateAccess() {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ACCESS_DENIED_MESSAGE);
		}
	}

	// Exception helpers
	private InternalSaleCustomException.BpmsClientException wrapBpmsException(Exception ex) {
		return new InternalSaleCustomException.BpmsClientException(BPMS_ERROR, List.of(ex.getMessage()));
	}
}