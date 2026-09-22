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
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LcProcessServiceImpl implements LcProcessService {

	private static final String BPMS_ERROR = "خطا در اتصال به کارتابل";
	private static final String LC_SAVE_ERROR = "خطا در ذخیره وضعیت اعتبار اسنادی پس از تایید در کارتابل";
	private static final String REVERSAL_PROCESS_ID_DEFAULT = "-";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور پیدا نشد";
	private static final String MSG_ACCESS_DENIED_START_LC = "شما اجازه شروع فرایند اعتبار اسنادی را ندارید";

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final LcRepository lcRepository;
	private final LcValidationService lcValidationService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final AcknowledgmentDeterminer acknowledgmentDeterminer;


	@Override
	public ProcessInstance startLcProcess(Long masterId) {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(MSG_ACCESS_DENIED_START_LC);
		}

		refreshStatus();
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

	/**
	 * NOTE ON BEHAVIOR CHANGE vs. the original:
	 * `lcList` used to be loaded BEFORE the BPMS call and reused for the save afterwards.
	 * If the BPMS call causes those same rows to be updated in another transaction
	 * (e.g. a synchronous listener), the entities we hold go stale and
	 * saveAllAndFlush() throws StaleObjectStateException/ObjectOptimisticLockingFailureException.
	 * Fix: re-fetch lcList AFTER the BPMS call, immediately before mutating/saving it,
	 * so we always work off the latest committed version.
	 */
	private void reviewTask(TaskActionDto dto, boolean approve) {

		dto.setApprove(approve);
		var reviewTaskRequest = processVariableProvider.prepareReviewTaskRequest(dto);

		try {
			bpmsClientService.reviewTask(reviewTaskRequest);
		} catch (Exception ex) {
			// Only a genuine BPMS call failure gets reported as a BPMS connection error.
			throw bpmsException(ex);
		}

		// Fetch fresh, post-callback state - avoids acting on a stale version.
		List<LcModel> lcList = lcRepository.findByProcessId(reviewTaskRequest.getProcessInstanceId());

		try {
			if (!approve) {
				for (LcModel lc : lcList) {
					lc.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
					lc.setAcknowledgment(Acknowledgment.CANCELED);
				}
				lcRepository.saveAllAndFlush(lcList);
				return;
			}

			if (processVariableProvider.isProcessAcceptedFinally(reviewTaskRequest.getProcessInstanceId())) {
				for (LcModel lc : lcList) {
					lc.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					lc.setAcknowledgment(Acknowledgment.FINISHED);
				}
				lcRepository.saveAllAndFlush(lcList);
				return;
			}

			for (LcModel lc : lcList) {
				if (lc.getPmsLcId() != null) {
					// Fixed: this branch was previously always overwritten by the
					// unconditional IN_PROGRESS/determine(...) calls below it and could
					// never take effect. Now it's a proper else-branch.
					lc.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					lc.setAcknowledgment(Acknowledgment.FINISHED);
				} else {
					lc.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
					lc.setAcknowledgment(acknowledgmentDeterminer.determine(lc));
				}
			}
			lcRepository.saveAllAndFlush(lcList);
		} catch (ObjectOptimisticLockingFailureException ex) {
			// BPMS review already succeeded at this point - don't mask it as a BPMS error.
			log.error("Optimistic lock conflict while persisting LC status for process {}: {}",
					reviewTaskRequest.getProcessInstanceId(), ex.getMessage(), ex);
			throw new InternalSaleCustomException.BpmsClientException(LC_SAVE_ERROR, List.of(ex.getMessage()));
		} catch (Exception ex) {
			if (!approve) {
				// Preserve prior behavior for the reject path: BPMS reject succeeded,
				// so we log a local persistence failure rather than failing the request.
				log.error("Error while rejecting LC for process {}: {}", reviewTaskRequest.getProcessInstanceId(), ex.getMessage(), ex);
				return;
			}
			throw bpmsException(ex);
		}
	}


	// -------------------------------------------------------------------------
	// Status refresh
	// -------------------------------------------------------------------------

	@Override
	public void refreshStatus() {
		try {
			List<LcModel> lcList = lcRepository.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS));
			for (LcModel lc : lcList) {
				lc.setAcknowledgment(acknowledgmentDeterminer.determine(lc));


				if (lc.getPmsLcId() != null) {
					lc.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					lc.setAcknowledgment(Acknowledgment.FINISHED);
				}
				ProcessInsHistoryDTO status = bpmsClientService.getProcessInstanceHistory(lc.getProcessId());
				if (status == null) continue;

				switch (status.getStatus()) {
					case ACTIVE:
						lc.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);

						break;

					case CANCELED:
						lc.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
						break;

					case FINISHED:
						boolean accepted = processVariableProvider.isProcessAcceptedFinally(lc.getProcessId());
						if (accepted) {
							lc.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
						} else {
							lc.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
						}
						break;
				}


			}
			lcRepository.saveAll(lcList);

		} catch (Exception ex) {
			log.error("Error refreshing LC status", ex);
		}
	}

//	private Acknowledgment applyCurrentStatus(LcModel lc) {
//		String processId = lc.getProcessId();
//		lc.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
//		return resolveAcknowledgmentFromStep(detectLcStep(processId));
//	}


	@Override
	public boolean canStartProcess() {
		//return hasAccessForVariable(LcProcessVariable.CreditBridge);
		return true;
	}


//
//	@Override
//	public LcProcessVariable detectLcStep(String processInstanceId) {
//		if (!TextUtility.isValidUUID(processInstanceId)) {
//			return null;
//		}
//		try {
//			List<TaskInfo> tasks = bpmsClientService.getProcessInstanceTasks(processInstanceId);
//			if (tasks == null || tasks.isEmpty()) {
//				return null;
//			}
//			String taskName = tasks.get(0).getName();
//			return Arrays.stream(LcProcessVariable.values())
//					.filter(v -> v.getValue().equals(taskName) || v.name().equalsIgnoreCase(taskName))
//					.findFirst()
//					.orElse(null);
//		} catch (Exception ex) {
//			log.debug("Failed to detect LC step for process {}", processInstanceId, ex);
//			return null;
//		}
//	}

//	@Override
//	public LcProcessVariable detectLcStep(long lcId) {
//		return lcRepository.findById(lcId)
//				.map(lc -> detectLcStep(lc.getProcessId()))
//				.orElse(null);
//	}
//
//	private Acknowledgment resolveAcknowledgmentFromStep(LcProcessVariable step) {
//		return switch (step) {
//			case RemitSure -> Acknowledgment.REMITTANCE;
//			case FinalCheck -> Acknowledgment.FINISHED;
//			case CreditBridge, SettleSure -> Acknowledgment.RECKONING;
//		};
//	}


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