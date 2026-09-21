package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GaamProcessServiceImpl implements GaamProcessService {

	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند برات الکترونیک را ندارید";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور پیدا نشد";
	private static final String PROFORMA_DUPLICATE_START = "برای این پیش فاکتور قبلا برات صادر شده است";
	private static final String LC_ALREADY_EXISTS = "برای این پیش فاکتور اعتبار اسنادی فعال وجود دارد";
	private static final String EXTRABILL_ALREADY_EXISTS = "برای این پیش فاکتور برات الکترونیک فعال وجود دارد";
	private static final String PROCESS_ID_PLACEHOLDER = "-";

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final GaamRepository gaamRepository;
	private final ExtraBillRepository extraBillRepository;

	private final LcRepository lcRepository;
	private final AcknowledgmentDeterminer acknowledgmentDeterminer;


	@Override
	@Transactional
	public ProcessInstance startProcess(Long masterId) {

		validateAccess();
		ProformaMasterModel proformaMaster = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(PROFORMA_NOT_FOUND_MESSAGE));
		validateNoActiveGaam(masterId);
		StartProcessWithDataDTO startProcessDto = buildStartProcessDto(proformaMaster);
		ProcessInstance processInstance = startProcessWithData(startProcessDto);
		List<GaamModel> gaamModels = buildGaamModels(proformaMaster, processInstance);
		gaamRepository.saveAll(gaamModels);
		return processInstance;
	}


	@Override
	@Transactional
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		try {
			startProcessDto.setProcessDefinitionKey(processVariableProvider.getGaamWorkflowByTitle().getDefinitionKey());
			return bpmsClientService.startProcessWithData(startProcessDto);

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex.fillInStackTrace());
		}
		return null;
	}


	// -------------------------------------------------------------------------
	// Process start helpers
	// -------------------------------------------------------------------------

	private void validateNoActiveGaam(Long masterId) {
		boolean hasActiveLc = lcRepository.findAllByProformaMasterId(masterId)
				.stream()
				.anyMatch(lc -> lc.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED
						&& lc.getWorkflowApproveStatus() != WorkflowApproveStatus.REVERSAL);

		if (hasActiveLc) {
			throw new InternalSaleCustomException.ValidationException(LC_ALREADY_EXISTS);
		}


		boolean hasActiveExtraBill = extraBillRepository.findAllByProformaMasterId(masterId)
				.stream()
				.anyMatch(lc -> lc.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED
						&& lc.getWorkflowApproveStatus() != WorkflowApproveStatus.REVERSAL);

		if (hasActiveExtraBill) {
			throw new InternalSaleCustomException.ValidationException(EXTRABILL_ALREADY_EXISTS);
		}


		List<GaamModel> gaamModels = gaamRepository.findAllByProformaMasterId(masterId);
		for (GaamModel item : gaamModels) {
			if (item.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED) {
				throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
			}
			if (item.getProcessId() != null && !item.getProcessId().equalsIgnoreCase(PROCESS_ID_PLACEHOLDER) && !processVariableProvider.isProcessFinished(item.getProcessId())) {
				throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
			}
		}
	}


	//TODO: check all values set
	private List<GaamModel> buildGaamModels(ProformaMasterModel proformaMaster, ProcessInstance processInstance) {

		List<GaamModel> gaamModels = new ArrayList<>();
		for (ProformaDetailModel detailModel : proformaMaster.getProformaDetailModelLists()) {
			GaamModel gaamModel = GaamModel.builder()
					.processId(processInstance.getId())
					.workflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS)
					.reversalProcessId(PROCESS_ID_PLACEHOLDER)
					.isReckoningSend(false)
					.proformaMasterId(proformaMaster.getId())
					.proformaDetailId(detailModel.getId())
					.acknowledgment(Acknowledgment.RECKONING)
					.tradeId(proformaMaster.getTradeId())
					.contractNo(proformaMaster.getContractNo())
					// Bank and branch information
					.issuerBankName(null)
					.issuerBankId(null)
					.branchCode(null)
					.branchName(null)
					.paymentCity(null)
					.agentBankName(null)
					.agentBankId(null)
					// File IDs - to be filled when documents are uploaded
					.extraBillFileId(null)
					.dispatchAttachmentId(null)
					// Codes -
					.nosaCode(null)
					.sepamCode(null)
					.treasuryId(null)
					// Dates - to be filled when GAAM issue
					.issueDate(null)
					.dueDate(null)
					// PMS Bill ID - to be filled from logestic
					.pmsBillId(null)
					// Cancellation fields - to be filled if GAAM is canceled
					.cancelDate(null)
					.cancellationReason(null)
					// Certificate count and extra bill amounts from detail model
					.gamCertificateCount(detailModel.getGamCertificateCount() != null ? detailModel.getGamCertificateCount() : 0)
					.extraBillOfExchangeAmount(detailModel.getExtraBillOfExchangeAmount() != null ? detailModel.getExtraBillOfExchangeAmount() : java.math.BigDecimal.ZERO)
					.extraBillOfPercent(detailModel.getExtraBillOfPercent() != null ? detailModel.getExtraBillOfPercent() : java.math.BigDecimal.ZERO)
					// Reckoning send date - null initially
					.reckoningSendDate(null)
					.build();
			gaamModels.add(gaamModel);
		}

		return gaamModels;
	}


	private StartProcessWithDataDTO buildStartProcessDto(ProformaMasterModel proformaMaster) {
		StartProcessWithDataDTO dto = new StartProcessWithDataDTO();
		dto.setProcessDefinitionKey(processVariableProvider.getGaamWorkflowByTitle().getDefinitionKey());
		dto.setVariables(processVariableProvider.createGaamRequestVariables(buildGaamVariablesInput(proformaMaster)));

		return dto;
	}


	private ProformaVariablesInput buildGaamVariablesInput(ProformaMasterModel proformaMaster) {
		ProformaVariablesInput input = new ProformaVariablesInput();
		input.setProformaMasterId(proformaMaster.getId());
		input.setContractDate(proformaMaster.getContractDate());
		input.setGoodId(proformaMaster.getGoodId());
		input.setGoodName(proformaMaster.getGoodName());
		input.setCustomerName(proformaMaster.getCustomerName());
		input.setContractNo(String.valueOf(proformaMaster.getContractNo()));
		input.setCommission(proformaMaster.getCommissionPercentage());

		return input;
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

		if (Boolean.FALSE.equals(reviewTaskRequest.getApprove())) {
			gaamRepository.findAllByProcessId(reviewTaskRequest.getProcessInstanceId()).forEach(gaam -> {
				gaam.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
				gaam.setAcknowledgment(Acknowledgment.CANCELED);
				gaamRepository.saveAndFlush(gaam);
			});
		} else {
			gaamRepository.findAllByProcessId(reviewTaskRequest.getProcessInstanceId()).forEach(gaam -> {
				if (gaam.getAcknowledgment() == Acknowledgment.RECKONING)
					gaam.setAcknowledgment(Acknowledgment.REMITTANCE);

				else {
					gaam.setAcknowledgment(acknowledgmentDeterminer.determine(gaam));
				}
				if (gaam.getAcknowledgment() == Acknowledgment.FINISHED) {
					gaam.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
				}

				gaamRepository.saveAndFlush(gaam);
			});
		}

	}

	@Override
	public boolean canStartProcess() {
//		return hasAccessForVariable(ExtraBillProcessVariable.BillDraftRegistration);
		return true;
	}


	private void validateAccess() {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ACCESS_DENIED_MESSAGE);
		}
	}


	@Override
	public void refreshStatus() {
		var masterIds = gaamRepository
				.findAllByWorkflowApproveStatusIn(List.of(WorkflowApproveStatus.IN_PROGRESS))
				.stream()
				.map(GaamModel::getId)
				.toList();

		for (Long masterId : masterIds) {
			try {
				refreshOne(masterId);
			} catch (Exception ex) {
				log.error("Error while refreshing status for gaam master id={}", masterId, ex);
			}
		}
	}


	public void refreshOne(Long masterId) {
		GaamModel master = gaamRepository.findById(masterId)
				.orElseThrow(() -> new EntityNotFoundException("GaamModel not found: " + masterId));

		Acknowledgment determined = acknowledgmentDeterminer.determine(master);
		if (master.getAcknowledgment() != determined) {
			master.setAcknowledgment(determined);
		}

		if (master.getPmsBillId() != null) {
			master.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			master.setAcknowledgment(Acknowledgment.FINISHED);
			gaamRepository.save(master);
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

		gaamRepository.save(master);
	}

}
