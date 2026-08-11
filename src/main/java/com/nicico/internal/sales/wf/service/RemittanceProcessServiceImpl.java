package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.wf.dto.RemittanceVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.RemittanceProcessVariable;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemittanceProcessServiceImpl implements RemittanceProcessService {
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final ProcessUserAccessRepository processUserAccessRepository;

	@Transactional(isolation = Isolation.READ_COMMITTED, label = "start_remittance_process")
	@Override
	public ProcessInstance startProcess(Long remittanceId) {

		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException("شما اجازه شروع فرایند حواله  را ندارید");
		}
		var workflow = processVariableProvider.getRemittanceWorkflowByTitle();
		var masterModel = remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("حواله وجود ندارد"));
		RemittanceVariablesInput input = new RemittanceVariablesInput();
		input.setRemittanceMasterId(masterModel.getId());
		input.setContractDate(String.valueOf(masterModel.getContractDate()));
		input.setRemittanceDate(masterModel.getRemittanceDate());
		input.setGoodId(masterModel.getGoodId());
		input.setGoodName(masterModel.getGoodName());
		input.setCustomerName(masterModel.getCustomerName());
		input.setContractNo(String.valueOf(masterModel.getContractNo()));
		input.setRemittanceNumber(masterModel.getRemittanceNumber());
		input.setIssuerId(masterModel.getIssuerId());
		input.setIssuerName(masterModel.getIssuerName());

		StartProcessWithDataDTO startProcessDto = new StartProcessWithDataDTO();
		startProcessDto.setProcessDefinitionKey(workflow.getDefinitionKey());
		startProcessDto.setVariables(processVariableProvider.createRemittanceRequestVariable(input));
		ProcessInstance instance = startProcessWithData(startProcessDto);
		masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		masterModel.setProcessId(instance.getId());
		remittanceMasterRepository.save(masterModel);
		refreshRemittanceStatus();
		return instance;
	}

	@Override
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException("شما اجازه شروع فرایند حواله  را ندارید");
		}
		try {
			var workflow = processVariableProvider.getRemittanceWorkflowByTitle();
			startProcessDto.setProcessDefinitionKey(workflow.getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		}
	}

	@Override
	public ProcessInstance startProcess(RemittanceVariablesInput input) {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException("شما اجازه شروع فرایند حواله  را ندارید");
		}
		try {
			Map<String, Object> requestVariableWithDetail = processVariableProvider.createRemittanceRequestVariable(input);
			StartProcessWithDataDTO dataDTO = new StartProcessWithDataDTO();
			var workflow = processVariableProvider.getRemittanceWorkflowByTitle();
			dataDTO.setProcessDefinitionKey(workflow.getDefinitionKey());
			dataDTO.setVariables(requestVariableWithDetail);
			return this.startProcessWithData(dataDTO);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		}
	}

//    @Override
//    public GridDTO getUserTasks(Pageable pageable) {
//        try {
//            var workflow = processVariableProvider.getRemittanceWorkflowByTitle();
//            TaskSearchDto dto = new TaskSearchDto();
//            dto.setProcessDefinitionId(workflow.getDefinitionKey());
//            dto.setUserIds(Collections.singletonList(SecurityUtil.getUserId().toString()));
//            dto.setWithLastComment(false);
//            GridDTO userTasks = bpmsClientService.infiniteSearchTask(dto, pageable.getPageNumber(), pageable.getPageSize());
//            userTasks.getData().forEach(item -> item.setTaskDefinitionKey(workflow.getProcessLocalTitle()));
//            return userTasks;
//        } catch (Exception ex) {
//            throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
//        }
//    }

	@Override
	public void approveTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(true);
		try {
			ReviewTaskRequest reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(taskActionDto);
			this.reviewTask(reviewTaskRequest);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.ValidationException("خطا در انجام تسک", List.of(ex.getMessage()));
		}
	}

	@Override
	public void rejectTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(false);
		try {
			ReviewTaskRequest reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(taskActionDto);
			reviewTask(reviewTaskRequest);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.ValidationException("خطا در انجام تسک", List.of(ex.getMessage()));
		}
	}

	@Override
	public void reviewTask(ReviewTaskRequest reviewTaskRequest) {
		bpmsClientService.reviewTask(reviewTaskRequest);

		try {
			if (!reviewTaskRequest.getApprove()) {
				remittanceMasterRepository.findByProcessId(reviewTaskRequest.getProcessInstanceId()).ifPresent(masterModel -> {
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
					masterModel.setProcessFinal(true);
					remittanceMasterRepository.save(masterModel);
				});
				return;
			}
			var acceptedFinally = processVariableProvider.isProcessAcceptedFinally(reviewTaskRequest.getProcessInstanceId());
			if (acceptedFinally) {
				remittanceMasterRepository.findByProcessId(reviewTaskRequest.getProcessInstanceId()).ifPresent(masterModel -> {
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					masterModel.setProcessFinal(true);
					remittanceMasterRepository.save(masterModel);
				});
			}


		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		} finally {
			refreshRemittanceStatus();
		}
	}

	@Override
	public void refreshRemittanceStatus() {

		try {
			var masters = remittanceMasterRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS));
			for (RemittanceMasterModel master : masters) {
				if (master.getPmsId() != null) {
					master.setProcessFinal(true);
					master.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					continue;
				}

				var processHistory = bpmsClientService.getProcessInstanceHistoryById(master.getProcessId());
				switch (processHistory.getStatus()) {

					case ACTIVE:
						master.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
						master.setProcessFinal(false);
						break;

					case CANCELED:
						master.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
						master.setProcessFinal(true);
						break;

					case FINISHED:
						master.setProcessFinal(true);
						boolean acceptedFinally = processVariableProvider.isProcessAcceptedFinally(master.getProcessId());
						master.setWorkflowApproveStatus(acceptedFinally ? WorkflowApproveStatus.ACCEPTED : WorkflowApproveStatus.CANCELED);
						break;

					default:
						master.setWorkflowApproveStatus(WorkflowApproveStatus.DRAFT);
						master.setProcessFinal(false);
						break;
				}
			}

			remittanceMasterRepository.saveAll(masters);

		} catch (Exception ex) {
			log.error("Error while refreshing remittance status", ex);
		}
	}


	@Override
	public boolean canStartProcess() {
		var workflow = processVariableProvider.getRemittanceWorkflowByTitle();
		var list = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle())
				.stream()
				.filter(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
						&& RemittanceProcessVariable.RemitForge.name().equalsIgnoreCase(access.getProcessVariable()))
				.toList();

		return !list.isEmpty();
	}

}