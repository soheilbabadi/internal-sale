package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
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
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReversalProformaProcessServiceImpl implements ReversalProformaProcessService {
	public static final String PROCESS_TITLE_REVERSAL = "REVERSAL";
	public static final String REVERSAL_PROCESS_ID_DEFAULT = "-";
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final ProformaValidationService proformaValidationService;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final WorkflowRepository workflowRepository;
	private final ProformaDetailRepository proformaDetailRepository;


	@Override
	public ProcessInstance startReversal(Long masterId) {

		refreshOne(masterId);
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.ValidationException("شما اجازه شروع فرایند ابطال پیش فاکتور را ندارید");
		}
		var masterModel = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("قراردادی با ای مشخصات یافت نشد"));

		var workflow = workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_REVERSAL)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("فرایند برگشت پیش فاکتور وجود ندارد"));

		proformaValidationService.validateReversal(masterId);


		if (masterModel.getProformaDetailModelLists().isEmpty()) {
			throw new InternalSaleCustomException.ValidationException("برای این قرارداد هیچ پیش فاکتوری ثبت نشده است");
		}

		Optional<ProformaDetailModel> emptyDetail = masterModel.getProformaDetailModelLists()
				.stream()
				.filter(detail -> detail.getProformaGoodItemModels() == null ||
						detail.getProformaGoodItemModels().isEmpty())
				.findFirst();

		if (emptyDetail.isPresent()) {
			long detailId = emptyDetail.get().getId();
			throw new InternalSaleCustomException.ValidationException(
					String.format("برای پیش فاکتور %d هیچ اقلام کالایی ثبت نشده است", detailId)
			);
		}

		ProformaVariablesInput input = processVariableProvider.buildProformaVariablesInput(masterModel);
		StartProcessWithDataDTO startProcessDto = new StartProcessWithDataDTO();
		startProcessDto.setProcessDefinitionKey(workflow.getDefinitionKey());
		startProcessDto.setVariables(processVariableProvider.createReversalRequestVariables(input));
		ProcessInstance instance = startProcessWithData(startProcessDto);

		updateDetailStatuses(masterModel, ProformaReversalStatus.CANCELED);
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
		masterModel.setReversalProcessId(instance.getId());
		proformaMasterRepository.saveAndFlush(masterModel);

		return instance;
	}

	@Override
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		try {
			var workflow = processVariableProvider.getReversalWorkflowByTitle();
			startProcessDto.setProcessDefinitionKey(workflow.getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		}
	}


	@Override
	public void approveTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(true);
		reviewTask(processVariableProvider.prepareReviewTaskRequest(taskActionDto));
	}

	@Override
	public void rejectTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(false);
		reviewTask(processVariableProvider.prepareReviewTaskRequest(taskActionDto));
	}


	private void reviewTask(ReviewTaskRequest reviewTaskRequest) {

		try {
			bpmsClientService.reviewTask(reviewTaskRequest);
			if (!reviewTaskRequest.getApprove()) {
				proformaMasterRepository.findByReversalProcessId(reviewTaskRequest.getProcessInstanceId())
						.ifPresent(masterModel -> {
							masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
							masterModel.setReversalProcessId(REVERSAL_PROCESS_ID_DEFAULT);
							masterModel.setIsReversalProcessFinal(false);
							updateDetailStatuses(masterModel, ProformaReversalStatus.CANCELED);
							proformaMasterRepository.saveAndFlush(masterModel);
						});
			}
		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		} finally {
			refreshStatus();
		}

	}


	private void updateDetailStatuses(ProformaMasterModel masterModel, ProformaReversalStatus status) {
		List<Long> detailIds = masterModel.getProformaDetailModelLists().stream()
				.map(ProformaDetailModel::getId)
				.toList();
		if (!detailIds.isEmpty()) {
			proformaDetailRepository.bulkUpdateReversalStatus(detailIds, status.getValue());
		}
	}

	@Override
	public void refreshStatus() {
		if (!canStartProcess()) return;
		var masterIds = proformaMasterRepository.findIdsByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.REVERSAL));

		for (Long masterId : masterIds) {
			try {
				refreshOne(masterId);
			} catch (Exception ex) {
				log.error("Error while refreshing status for reversal proforma master id={}", masterId, ex);
			}
		}
	}

	@Override
	public void refreshOne(Long masterId) {
		ProformaMasterModel masterModel = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new EntityNotFoundException("ProformaMasterModel not found: " + masterId));

		String reversalId = masterModel.getReversalProcessId();
		if (Boolean.TRUE.equals(masterModel.getIsReversalProcessFinal()) || reversalId == null || REVERSAL_PROCESS_ID_DEFAULT.equals(reversalId)) {
			return;
		}
		var status = bpmsClientService.getProcessInstanceHistoryById(reversalId);
		var acceptedFinally = processVariableProvider.isProcessAcceptedFinally(reversalId);

		switch (status.getStatus()) {
			case ACTIVE:
				masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
				masterModel.setIsProcessFinal(true);
				masterModel.setIsReversalProcessFinal(false);
				updateDetailStatuses(masterModel, ProformaReversalStatus.CANCELED);
				proformaMasterRepository.saveAndFlush(masterModel);
				break;
			case CANCELED:
				masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
				masterModel.setIsProcessFinal(true);
				masterModel.setIsReversalProcessFinal(true);
				updateDetailStatuses(masterModel, ProformaReversalStatus.NORMAL);
				proformaMasterRepository.saveAndFlush(masterModel);
				break;
			case FINISHED:
				masterModel.setIsProcessFinal(true);
				masterModel.setIsReversalProcessFinal(true);
				if (acceptedFinally) {
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
					updateDetailStatuses(masterModel, ProformaReversalStatus.CANCELED);
				}
				else {
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					updateDetailStatuses(masterModel, ProformaReversalStatus.NORMAL);
				}
				proformaMasterRepository.saveAndFlush(masterModel);
				break;
			default:
				masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
				masterModel.setIsProcessFinal(true);
				masterModel.setIsReversalProcessFinal(false);
				masterModel.setReversalProcessId(REVERSAL_PROCESS_ID_DEFAULT);
				updateDetailStatuses(masterModel, ProformaReversalStatus.NORMAL);
				proformaMasterRepository.save(masterModel);
		}
		if (processVariableProvider.isProcessFinished(reversalId) && processVariableProvider.isProcessAcceptedFinally(reversalId)) {
			masterModel.setIsProcessFinal(true);
			masterModel.setIsReversalProcessFinal(true);
			masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
			updateDetailStatuses(masterModel, ProformaReversalStatus.CANCELED);
			proformaMasterRepository.saveAndFlush(masterModel);
		} else if (processVariableProvider.isProcessFinished(reversalId) && !processVariableProvider.isProcessAcceptedFinally(reversalId)) {
			masterModel.setIsProcessFinal(true);
			masterModel.setIsReversalProcessFinal(true);
			masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			updateDetailStatuses(masterModel, ProformaReversalStatus.NORMAL);
			proformaMasterRepository.saveAndFlush(masterModel);
		}
	}

	@Override
	public boolean canStartProcess() {
//		var list = processUserAccessRepository.findAllByProcessTitle(PROCESS_TITLE_REVERSAL)
//				.stream()
//				.filter(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
//						&& ReversalProcessVariable.salesExpert.name().equalsIgnoreCase(access.getProcessVariable()))
//				.toList();
//		return !list.isEmpty();
		return true;
	}
}