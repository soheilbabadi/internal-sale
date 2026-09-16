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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GaamProcessServiceImpl implements GaamProcessService {

	private static final String PROCESS_TITLE_GAAM = "GAAM";
	private static final String BPMS_ERROR = "خطا در اتصال به کارتابل";
	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند برات الکترونیک را ندارید";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور پیدا نشد";
	private static final String PROFORMA_DUPLICATE_START = "برای این پیش فاکتور قبلا برات صادر شده است";
	private static final String LC_ALREADY_EXISTS = "برای این پیش فاکتور اعتبار اسنادی فعال وجود دارد";
	private static final String EXTRABILL_ALREADY_EXISTS = "برای این پیش فاکتور برات الکترونیک فعال وجود دارد";
	private static final String ERROR_REFRESHING_STATUS = "خطا در بروز رسانی وضعیت براتها";
	private static final String ERROR_REJECTING_GAAM = "خطا در رد کردن فرایند {}";
	private static final String ERROR_DETECTING_STEP = "خطا در تشخیص مرحله فرایند {}";
	private static final String ERROR_HANDLING_TASK_ACTION = "خطا در انجام عملیات تسک {}";
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
	public ProcessInstance startGaamProcess(Long masterId) {

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
					// Bank and branch information - to be filled from external source or left null for now
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
					// Codes - to be filled from external source or left null for now
					.nosaCode(null)
					.sepamCode(null)
					.treasuryId(null)
					// Dates - to be filled when GAAM is issued
					.issueDate(null)
					.dueDate(null)
					// PMS Bill ID - to be filled from external source
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


}
