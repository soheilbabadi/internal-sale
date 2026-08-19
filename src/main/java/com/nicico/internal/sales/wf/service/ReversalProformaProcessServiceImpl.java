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

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReversalProformaProcessServiceImpl implements ReversalProformaProcessService {
	public static final String PROCESS_TITLE_REVERSAL = "REVERSAL";
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final ProformaValidationService proformaValidationService;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final WorkflowRepository workflowRepository;
	private final ProformaDetailRepository proformaDetailRepository;

	@Transactional(isolation = Isolation.READ_COMMITTED, label = "start_reversal")
	@Override
	public ProcessInstance startReversal(Long masterId) {

		if (!canStartProcess()) {
			throw new InternalSaleCustomException.ValidationException("شما اجازه شروع فرایند ابطال پیش فاکتور را ندارید");
		}
		var workflow = workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_REVERSAL)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("فرایند برگشت پیش فاکتور وجود ندارد"));


		proformaValidationService.validateReversal(masterId);

		var masterModel = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("قراردادی با ای مشخصات یافت نشد"));

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

		ProformaVariablesInput input = new ProformaVariablesInput();
		var performaDetailList = masterModel.getProformaDetailModelLists();
		input.setProformaMasterId(masterModel.getId());
		input.setContractDate(performaDetailList.get(0).getContractDate());
		input.setGoodId(performaDetailList.get(0).getProformaGoodItemModels().get(0).getGoodId());
		input.setGoodName(performaDetailList.get(0).getProformaGoodItemModels().get(0).getGoodName());
		input.setCustomerName(masterModel.getCustomerName());
		input.setContractNo(String.valueOf(masterModel.getContractNo()));
		input.setCommission(masterModel.getCommissionPercentage());
		StartProcessWithDataDTO startProcessDto = new StartProcessWithDataDTO();
		startProcessDto.setProcessDefinitionKey(workflow.getDefinitionKey());
		startProcessDto.setVariables(processVariableProvider.createReversalRequestVariables(input));
		ProcessInstance instance = startProcessWithData(startProcessDto);

		for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
			detailModel.setProformaReversalStatus(ProformaReversalStatus.CANCELED);
			proformaDetailRepository.save(detailModel);
		}
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
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		}
	}


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
			this.reviewTask(reviewTaskRequest);
		} catch (Exception ex) {
			throw new InternalSaleCustomException.ValidationException("خطا در انجام تسک", List.of(ex.getMessage()));
		}
	}

	@Override
	public void reviewTask(ReviewTaskRequest reviewTaskRequest) {
		try {
			bpmsClientService.reviewTask(reviewTaskRequest);
			if (!reviewTaskRequest.getApprove()) {
				proformaMasterRepository.findByReversalProcessId(reviewTaskRequest.getProcessInstanceId()).ifPresent(masterModel -> {
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					masterModel.setReversalProcessId("-");
					masterModel.setIsReversalProcessFinal(false);
					proformaMasterRepository.save(masterModel);
					for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
						detailModel.setProformaReversalStatus(ProformaReversalStatus.CANCELED);
						proformaDetailRepository.save(detailModel);
					}
					proformaMasterRepository.save(masterModel);
				});
			}
		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
		} finally {
			refreshReversalProformaStatus();
		}
	}

	public void refreshReversalProformaStatus() {
		if (!canStartProcess()) return;
		try {
			List<ProformaMasterModel> masterModelList = proformaMasterRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.REVERSAL));
			for (ProformaMasterModel masterModel : masterModelList) {
				String reversalId = masterModel.getReversalProcessId();
				if (Boolean.TRUE.equals(masterModel.getIsReversalProcessFinal()) || reversalId == null || "-".equals(reversalId))
					continue;
				var acceptedFinally = processVariableProvider.isProcessAcceptedFinally(reversalId);
				var status = bpmsClientService.getProcessInstanceHistoryById(reversalId);
				switch (status.getStatus()) {
					case ACTIVE:
						masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
						masterModel.setIsProcessFinal(true);
						masterModel.setIsReversalProcessFinal(false);
						for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
							detailModel.setProformaReversalStatus(ProformaReversalStatus.CANCELED);
							proformaDetailRepository.save(detailModel);
						}
						proformaMasterRepository.save(masterModel);
						break;
					case CANCELED:
						masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
						masterModel.setIsProcessFinal(true);
						masterModel.setIsReversalProcessFinal(true);
						for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
							detailModel.setProformaReversalStatus(ProformaReversalStatus.NORMAL);
							proformaDetailRepository.save(detailModel);
						}
						proformaMasterRepository.save(masterModel);
						break;
					case FINISHED:
						if (acceptedFinally) {
							masterModel.setIsProcessFinal(true);
							masterModel.setIsReversalProcessFinal(true);
							masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
							for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
								detailModel.setProformaReversalStatus(ProformaReversalStatus.CANCELED);
								proformaDetailRepository.save(detailModel);
							}
						}
						proformaMasterRepository.save(masterModel);
						break;
					default:
						masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
						masterModel.setIsProcessFinal(true);
						masterModel.setIsReversalProcessFinal(false);
						masterModel.setReversalProcessId("-");
						for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
							detailModel.setProformaReversalStatus(ProformaReversalStatus.NORMAL);
							proformaDetailRepository.save(detailModel);
						}
						proformaMasterRepository.save(masterModel);
				}
				if (reversalId != null && processVariableProvider.isProcessFinished(reversalId) && processVariableProvider.isProcessAcceptedFinally(reversalId)) {
					masterModel.setIsProcessFinal(true);
					masterModel.setIsReversalProcessFinal(true);
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
					for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
						detailModel.setProformaReversalStatus(ProformaReversalStatus.CANCELED);
						proformaDetailRepository.save(detailModel);
					}
					proformaMasterRepository.save(masterModel);
				} else if (reversalId != null && processVariableProvider.isProcessFinished(reversalId) && !processVariableProvider.isProcessAcceptedFinally(reversalId)) {
					masterModel.setIsProcessFinal(true);
					masterModel.setIsReversalProcessFinal(true);
					masterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					for (ProformaDetailModel detailModel : masterModel.getProformaDetailModelLists()) {
						detailModel.setProformaReversalStatus(ProformaReversalStatus.NORMAL);
						proformaDetailRepository.save(detailModel);
					}
					proformaMasterRepository.save(masterModel);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	@Override
	public boolean canStartProcess() {
		var list = processUserAccessRepository.findAllByProcessTitle(PROCESS_TITLE_REVERSAL)
				.stream()
				.filter(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
						&& ReversalProcessVariable.salesExpert.name().equalsIgnoreCase(access.getProcessVariable()))
				.toList();
		return !list.isEmpty();
	}
}