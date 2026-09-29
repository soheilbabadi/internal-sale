package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
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

	private static final String ACCESS_DENIED_MESSAGE = "شما اجازه شروع فرایند اوراق گام را ندارید";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور پیدا نشد";
	private static final String PROFORMA_DUPLICATE_START = "برای این پیش فاکتور قبلا اوراق گام صادر شده است";
	private static final String LC_ALREADY_EXISTS = "برای این پیش فاکتور اعتبار اسنادی فعال وجود دارد";
	private static final String PROCESS_ID_PLACEHOLDER = "-";
	private static final String APPROVED_KEY = "approved";

	private static final String MSG_PROFORMA_NOT_ACCEPTED = "پیش فاکتور در وضعیت تایید شده نیست";
	private static final String MSG_PROFORMA_MASTER_NOT_FOUND = "قرارداد فروش وجود ندارد";
	private static final String MSG_PROFORMA_DETAIL_CANCELED = " پیش فاکتور ابطال شده است";

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final GaamRepository gaamRepository;
	private final ExtraBillRepository extraBillRepository;
	private final LcRepository lcRepository;
		private final ProcessService processService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final GaamProcessVariableDetector  gaamProcessVariableDetector;

	@Override
	@Transactional
	public ProcessInstance startProcess(Long masterId) {

		validateNoActiveGaam(masterId);
		refreshStatus();
		validateAccess();
		ProformaMasterModel proformaMaster = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(PROFORMA_NOT_FOUND_MESSAGE));

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
					.gamCertificateCount(detailModel.getGamCertificateCount() != null ? detailModel.getGamCertificateCount() : 0L)
					.extraBillOfExchangeAmount(detailModel.getExtraBillOfExchangeAmount() != null ? detailModel.getExtraBillOfExchangeAmount() : java.math.BigDecimal.ZERO)
					.extraBillOfPercent(detailModel.getExtraBillOfPercent() != null ? detailModel.getExtraBillOfPercent() : java.math.BigDecimal.ZERO)
					// Reckoning send date - null initially
					.reckoningSendDate(null)

					.build();
			gaamModels.add(gaamModel);
		}

		return gaamModels;
	}

	private void validateNoActiveGaam(Long masterId) {

		ProformaMasterModel master = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_MASTER_NOT_FOUND));

		if (master.getWorkflowApproveStatus() != WorkflowApproveStatus.ACCEPTED) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_NOT_ACCEPTED);
		}

		ProformaDetailModel detail = proformaDetailRepository.findById(master.getProformaDetailModelLists().get(0).getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));

		if (detail.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_CANCELED);
		}


		List<LcModel> lcs = lcRepository.findAllByProformaMasterId(masterId);

		long activeLcCount = lcs.stream()
				.filter(lc -> lc.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED)
				.count();

		if (activeLcCount > 0) {
			throw new InternalSaleCustomException.ValidationException(LC_ALREADY_EXISTS);
		}

		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProformaMasterId(masterId);

		long nonCanceledCount = bills.stream()
				.filter(bill -> bill.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED)
				.count();

		if (nonCanceledCount > 0) {
			throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
		}

	}
	private StartProcessWithDataDTO buildStartProcessDto(ProformaMasterModel proformaMaster) {
		StartProcessWithDataDTO dto = new StartProcessWithDataDTO();
		dto.setProcessDefinitionKey(processVariableProvider.getGaamWorkflowByTitle().getDefinitionKey());
		dto.setVariables(processVariableProvider.createGaamRequestVariables(processVariableProvider.buildGaamVariablesInput(proformaMaster)));

		return dto;
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
					gaam.setAcknowledgment(gaamProcessVariableDetector.detectStep(gaam));
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

	@Override
	public void refreshOne(Long masterId) {
		GaamModel gaamModel = gaamRepository.findById(masterId)
				.orElseThrow(() -> new EntityNotFoundException("GaamModel not found: " + masterId));

		Acknowledgment determined = gaamProcessVariableDetector.detectStep(gaamModel);
		if (gaamModel.getAcknowledgment() != determined) {
			gaamModel.setAcknowledgment(determined);
		}

		if (gaamModel.getPmsBillId() != null) {
			gaamModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			gaamModel.setAcknowledgment(Acknowledgment.FINISHED);
			gaamRepository.save(gaamModel);
			return;
		}
		var processHistory = bpmsClientService.getProcessInstanceHistoryById(gaamModel.getProcessId());
		switch (processHistory.getStatus()) {
			case ACTIVE -> {

				gaamModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
				gaamModel.setAcknowledgment(determined);
				gaamRepository.save(gaamModel);
				return;
			}
			case CANCELED -> {
				gaamModel.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
				gaamModel.setAcknowledgment(Acknowledgment.CANCELED);
				gaamRepository.save(gaamModel);
				return;
			}
			case FINISHED -> {
				boolean acceptedFinally = processVariableProvider.isProcessAcceptedFinally(gaamModel.getProcessId());
				if (acceptedFinally) {
					gaamModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
					gaamModel.setAcknowledgment(Acknowledgment.FINISHED);
				} else {
					gaamModel.setWorkflowApproveStatus(WorkflowApproveStatus.CANCELED);
					gaamModel.setAcknowledgment(Acknowledgment.CANCELED);
				}
				gaamRepository.save(gaamModel);
				return;
			}
			default -> {
				gaamModel.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
				gaamModel.setAcknowledgment(Acknowledgment.RECKONING);
				gaamRepository.save(gaamModel);
				return;
			}
		}

	}





}
