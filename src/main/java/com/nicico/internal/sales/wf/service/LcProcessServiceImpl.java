package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInsHistoryDTO;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.lc.service.LcValidationService;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.detection.LcStepDetector;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class LcProcessServiceImpl implements LcProcessService {

	private static final String BPMS_ERROR = "خطا در اتصال به کارتابل";
	private static final String LC_SAVE_ERROR = "خطا در ذخیره وضعیت اعتبار اسنادی پس از تایید در کارتابل";
	private static final String REVERSAL_PROCESS_ID_DEFAULT = "-";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور پیدا نشد";
	private static final String MSG_ACCESS_DENIED_START_LC = "شما اجازه شروع فرایند اعتبار اسنادی را ندارید";
	private static final int OPTIMISTIC_LOCK_RETRY_COUNT = 3;

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final LcRepository lcRepository;
	private final LcValidationService lcValidationService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final LcStepDetector lcStepDetector;

	@Override
	public ProcessInstance startLcProcess(Long masterId) {
		refreshStatus();
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(MSG_ACCESS_DENIED_START_LC);
		}

		lcValidationService.validateStart(masterId);
		ProformaMasterModel masterModel = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PROFORMA_NOT_FOUND));
		List<ProformaDetailModel> details = proformaDetailRepository.findAllByProformaMasterId(masterId);
		StartProcessWithDataDTO startProcessDto = new StartProcessWithDataDTO();
		startProcessDto.setProcessDefinitionKey(processVariableProvider.getLcWorkflowByTitle().getDefinitionKey());
		startProcessDto.setVariables(processVariableProvider.createLCRequestVariables(processVariableProvider.buildProformaVariablesInput(masterModel)));

		ProcessInstance instance = startProcessWithData(startProcessDto);

		details.stream()
				.filter(detail -> detail.getProformaReversalStatus() != ProformaReversalStatus.CANCELED)
				.map(detail -> buildLcModel(instance, masterModel, detail))
				.forEach(lcRepository::save);

		return instance;
	}

	@Override
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(MSG_ACCESS_DENIED_START_LC);
		}
		try {
			startProcessDto.setProcessDefinitionKey(
					processVariableProvider.getLcWorkflowByTitle().getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);
		} catch (Exception ex) {
			throw bpmsException(ex);
		}
	}

	@Override
	public void approveTask(TaskActionDto taskActionDto) {
		reviewTask(taskActionDto, true);
	}

	@Override
	public void rejectTask(TaskActionDto taskActionDto) {
		reviewTask(taskActionDto, false);
	}

	private void reviewTask(TaskActionDto dto, boolean approve) {

		dto.setApprove(approve);
		var reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(dto);

		try {
			bpmsClientService.reviewTask(reviewTaskRequest);
		} catch (Exception ex) {
			throw bpmsException(ex);
		}
		List<LcModel> lcList = lcRepository.findByProcessId(reviewTaskRequest.getProcessInstanceId());
		try {
			if (!approve) {
				for (LcModel lc : lcList) {
					saveLcRetryOnOptimisticLock(lc.getId(), model -> {
						model.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
						model.setAcknowledgment(Acknowledgment.CANCELED);
					});
				}
				return;
			}

			if (processVariableProvider.isProcessAcceptedFinally(reviewTaskRequest.getProcessInstanceId())) {
				for (LcModel lc : lcList) {
					saveLcRetryOnOptimisticLock(lc.getId(), model -> {
						model.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
						model.setAcknowledgment(Acknowledgment.FINISHED);
					});
				}
			}

		} catch (Exception ex) {
			log.error("Error while updating LC status for process {}: {}", reviewTaskRequest.getProcessInstanceId(), ex.getMessage(), ex);
		}
	}

	@Override
	public void refreshStatus() {
		try {
			List<LcModel> lcList = lcRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS));
			for (LcModel lc : lcList) {

				if (lc.getPmsLcId() != null) {
					saveLcRetryOnOptimisticLock(lc.getId(), model -> {
						model.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
						model.setAcknowledgment(Acknowledgment.FINISHED);
					});
					continue;
				}
				ProcessInsHistoryDTO status = bpmsClientService.getProcessInstanceHistory(lc.getProcessId());
				if (status == null) continue;
				Acknowledgment defaultAcknowledgment = lcStepDetector.detectStep(lc);
				WorkflowApproveStatus targetStatus;
				Acknowledgment targetAcknowledgment;
				switch (status.getStatus()) {
					case ACTIVE:
						targetStatus = WorkflowApproveStatus.IN_PROGRESS;
						targetAcknowledgment = defaultAcknowledgment;
						break;

					case CANCELED:
						targetStatus = WorkflowApproveStatus.CANCELED;
						targetAcknowledgment = Acknowledgment.CANCELED;
						break;

					case FINISHED:
						boolean accepted = processVariableProvider.isProcessAcceptedFinally(lc.getProcessId());
						if (accepted) {
							targetStatus = WorkflowApproveStatus.ACCEPTED;
							targetAcknowledgment = Acknowledgment.FINISHED;
						} else {
							targetStatus = WorkflowApproveStatus.CANCELED;
							targetAcknowledgment = Acknowledgment.CANCELED;
						}
						break;

					default:
						continue;
				}
				final WorkflowApproveStatus finalTargetStatus = targetStatus;
				final Acknowledgment finalTargetAcknowledgment = targetAcknowledgment;
				saveLcRetryOnOptimisticLock(lc.getId(), model -> {
					model.setWorkflowApproveStatus(finalTargetStatus);
					model.setAcknowledgment(finalTargetAcknowledgment);
				});
			}


		} catch (Exception ex) {
			log.error("Error refreshing LC status", ex);
		}
	}

	@Override
	public boolean canStartProcess() {
		//return hasAccessForVariable(LcProcessVariable.CreditBridge);
		return true;
	}

	private void saveLcRetryOnOptimisticLock(Long lcId, Consumer<LcModel> stateMutator) {
		for (int attempt = 1; ; attempt++) {
			try {
				LcModel lc = lcRepository.findById(lcId)
						.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("LC پیدا نشد: " + lcId));
				stateMutator.accept(lc);
				lcRepository.save(lc);
				return;
			} catch (ObjectOptimisticLockingFailureException e) {
				if (attempt >= OPTIMISTIC_LOCK_RETRY_COUNT) {
					log.error("Failed to save LC {} after {} attempts due to concurrent modification", lcId, attempt, e);
					throw e;
				}
				log.warn("Optimistic lock conflict while saving LC {}, retrying (attempt {}/{})", lcId, attempt, OPTIMISTIC_LOCK_RETRY_COUNT, e);
			}
		}
	}

	private LcModel buildLcModel(ProcessInstance instance, ProformaMasterModel masterModel, ProformaDetailModel detail) {
		var goodItem = proformaGoodItemRepository.findAllByProformaDetailModel(detail).get(0);
		var totalFinalAmount = goodItem.getCreditAmount().add(goodItem.getVatCreditAmount());

		LcModel lc = new LcModel();
		lc.setProcessId(instance.getId());
		lc.setLcInstanceId(instance.getId());
		lc.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		lc.setProformaDetailId(detail.getId());
		lc.setProformaMasterId(masterModel.getId());
		lc.setPerformaNo(detail.getPerformaNo());
		lc.setPerformaDate(DateUtility.getJalaliDate(detail.getPerformaDate()));
		lc.setContractNo(masterModel.getContractNo());
		lc.setCreditExpirePeriod(detail.getCreditExpirePeriod());
		lc.setPaymentDeferral(detail.getPaymentDeferral());
		lc.setDeadlineDays(detail.getDeadlineDays());
		lc.setRequireDispatchFile(false);
		lc.setPaymentCode(masterModel.getPaymentCode());
		lc.setAcknowledgment(Acknowledgment.RECKONING);
		lc.setReckoningSend(false);
		lc.setContractDate(masterModel.getContractDate());
		lc.setBrokerId(masterModel.getBrokerId());
		lc.setBrokerName(masterModel.getBrokerName());
		lc.setBrokerNationalCode(REVERSAL_PROCESS_ID_DEFAULT);
		lc.setTotalQuantity(masterModel.getTotalQuantity());
		lc.setTotalFinalAmount(totalFinalAmount);
		lc.setOfferDescription(masterModel.getOfferDescription());
		lc.setImeCommoditySymbol(masterModel.getImeCommoditySymbol());
		lc.setGoodId(masterModel.getGoodId());
		lc.setGoodName(masterModel.getGoodName());
		lc.setCustomerName(masterModel.getCustomerName());
		lc.setCustomerId(masterModel.getCustomerId());
		return lc;
	}

	private InternalSaleCustomException.BpmsClientException bpmsException(Exception ex) {
		return new InternalSaleCustomException.BpmsClientException(BPMS_ERROR, List.of(ex.getMessage()));
	}
}