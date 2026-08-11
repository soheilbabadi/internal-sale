package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.enumuration.ProcessInstanceStatus;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.TaskDetail;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.RemittanceVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessVariableProviderImpl implements ProcessVariableProvider {
	public static final String PROCESS_TITLE_PREINVOCE = "PREINVOICE";
	public static final String PROCESS_TITLE_REVERSAL = "REVERSAL";
	public static final String PROCESS_TITLE_LC = "LC";
	public static final String PROCESS_TITLE_EXTRA_BILL = "EXTRA_BILL";
	public static final String PROCESS_TITLE_REMITTANCE = "REMITTANCE";
	private final WorkflowRepository workflowRepository;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final BpmsClientService bpmsClientService;


	@Override
	public WorkflowModel getProformaWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_PREINVOCE)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("فرایند پیش فاکتور وجود ندارد"));
	}

	@Override
	public WorkflowModel getReversalWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_REVERSAL)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("فرایند برگشت پیش فاکتور وجود ندارد"));
	}


	@Override
	public WorkflowModel getLcWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_LC)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("فرایند اعتبار اسنادی وجود ندارد"));
	}

	@Override
	public WorkflowModel getExtraBillWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_EXTRA_BILL)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("فرایند ثبت برات وجود ندارد"));
	}

	@Override
	public WorkflowModel getRemittanceWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_REMITTANCE)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("فرایند حواله وجود ندارد"));
	}

	@Override
	public Map<String, Object> createProformaRequestVariables(ProformaVariablesInput input) {
		return createRequestVariables(input, getProformaUserAccess());
	}

	@Override
	public Map<String, Object> createReversalRequestVariables(ProformaVariablesInput input) {
		return createRequestVariables(input, getReversalUserAccess());
	}

	@Override
	public Map<String, Object> createExtraBillRequestVariables(ProformaVariablesInput input) {
		return createRequestVariables(input, getLcUserAccess());
	}

	@Override
	public Map<String, Object> createLCRequestVariables(ProformaVariablesInput input) {
		return createRequestVariables(input, getLcUserAccess());
	}

	@Override
	public Map<String, Object> createRemittanceRequestVariable(RemittanceVariablesInput input) {
		return createRequestVariables(input, getRemittanceUserAccess());
	}

	private Map<String, Object> createRequestVariables(RemittanceVariablesInput input, Map<String, String> userAccess) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("remittanceMasterId", input.getRemittanceMasterId());
		variables.put("contractDate", input.getContractDate());
		variables.put("remittanceDate", input.getRemittanceDate());
		variables.put("remittanceNo", input.getRemittanceNumber());
		variables.put("goodId", input.getGoodId());
		variables.put("goodName", input.getGoodName());
		variables.put("customerName", input.getCustomerName());
		variables.put("contractNo", input.getContractNo());
		variables.put("issuerName", SecurityUtil.getFirstName() + " " + SecurityUtil.getLastName());
		variables.put("issuerId", SecurityUtil.getUserId());
		variables.put("settlementType", input.getSettlementType());
		variables.put("proformaType", input.getProformaType());
		variables.put("proformaIssueDate", input.getProformaIssueDate());
		variables.put("proformaNo", input.getProformaNo());
		variables.put("tradingBank", input.getTradingBank());
		variables.put("lcNo", input.getLcNo());
		variables.put("issuerBank", input.getIssuerBank());
		variables.put("lcDate", input.getLcDate());

		variables.putAll(userAccess);
		Map<String, Object> wrapped = new HashMap<>(variables);
		wrapped.put("INSTANCE_DETAILS", variables);
		return wrapped;
	}

	private Map<String, Object> createRequestVariables(ProformaVariablesInput input, Map<String, String> userAccess) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("proformaMasterId", input.getProformaMasterId());
		variables.put("contractDate", input.getContractDate());
		variables.put("goodId", input.getGoodId());
		variables.put("goodName", input.getGoodName());
		variables.put("customerName", input.getCustomerName());
		variables.put("contractNo", input.getContractNo());
		variables.put("commission", input.getCommission());
		variables.putAll(userAccess);
		Map<String, Object> wrapped = new HashMap<>(variables);
		wrapped.put("INSTANCE_DETAILS", variables);
		return wrapped;
	}


	@Override
	public Map<String, String> getProformaUserAccess() {
		var workflow = getProformaWorkflowByTitle();
		var accessList = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		var userAccess = ProcessUserAccessResolver.resolveUserAccess(accessList, List.of("bossProforma", "Proforma"));
		userAccess.put("starter", SecurityUtil.getUserId().toString());
		userAccess.put("processName", workflow.getProcessTitle());
		userAccess.put("processLocalName", workflow.getProcessLocalTitle());
		return userAccess;
	}

	@Override
	public Map<String, String> getRemittanceUserAccess() {
		var workflow = this.getRemittanceWorkflowByTitle();
		List<ProcessUserAccessModel> accessList = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		var userAccess = ProcessUserAccessResolver.resolveUserAccess(accessList, List.of("RemitForge", "RemitGuardboss", "RemitGuard"));
		userAccess.put("starter", SecurityUtil.getUserId().toString());
		userAccess.put("processName", workflow.getProcessTitle());
		userAccess.put("processLocalName", workflow.getProcessLocalTitle());
		return userAccess;
	}

	@Override
	public Map<String, String> getReversalUserAccess() {
		var workflow = getReversalWorkflowByTitle();
		List<ProcessUserAccessModel> accessList = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		var userAccess = ProcessUserAccessResolver.resolveUserAccess(accessList, List.of("salesExpert", "salesExpertReview", "salesManager"));
		userAccess.put("starter", SecurityUtil.getUserId().toString());
		userAccess.put("processName", workflow.getProcessTitle());
		userAccess.put("processLocalName", workflow.getProcessLocalTitle());
		return userAccess;
	}

	@Override
	public Map<String, String> getLcUserAccess() {
		var workflow = getLcWorkflowByTitle();
		var accessList = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		var userAccess = ProcessUserAccessResolver.resolveUserAccess(accessList, List.of("CreditBridge", "SettleSure", "RemitSure", "FinalCheck"));
		userAccess.put("starter", SecurityUtil.getUserId().toString());
		userAccess.put("processName", workflow.getProcessTitle());
		userAccess.put("processLocalName", workflow.getProcessLocalTitle());
		return userAccess;
	}


	@Override
	public Map<String, String> getExtraBillUserAccess() {
		var workflow = getExtraBillWorkflowByTitle();
		var accessList = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		var userAccess = ProcessUserAccessResolver.resolveUserAccess(accessList, List.of("DraftRegistration", "DraftReview", "BankApproval", "BeneficiaryConfirmation", "DraftIssuance", "FinalVerification"));
		userAccess.put("starter", SecurityUtil.getUserId().toString());
		userAccess.put("processName", workflow.getProcessTitle());
		userAccess.put("processLocalName", workflow.getProcessLocalTitle());
		return userAccess;
	}


	@Override
	public ReviewTaskRequest prepareReviewTaskRequest(TaskActionDto taskActionDto) {
		TaskDetail taskInfo = bpmsClientService.getTaskDetail(taskActionDto.getTaskId());
		String action = taskActionDto.getApprove() ? "تایید" : "رد";
		ReviewTaskRequest reviewTaskRequest = new ReviewTaskRequest();
		reviewTaskRequest.setProcessInstanceId(taskInfo.getProcessInstanceId());
		reviewTaskRequest.setUserId(SecurityUtil.getUserId().toString());
		reviewTaskRequest.setUserName(SecurityUtil.getUsername());
		reviewTaskRequest.setTaskId(taskActionDto.getTaskId());
		reviewTaskRequest.setApprove(taskActionDto.getApprove());
		reviewTaskRequest.setDescription(action + " پروسه ");
		return reviewTaskRequest;
	}

	@Override
	public boolean isProcessFinished(String processId) {
		if (!isValidUUID(processId)) {
			return true;
		}
		var processInstance = bpmsClientService.getProcessInstanceHistoryById(processId);
		return processInstance.getStatus() != ProcessInstanceStatus.ACTIVE;
	}

	private boolean isValidUUID(String processId) {
		try {
			UUID.fromString(processId);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public boolean isProcessAcceptedFinally(String processId) {
		try {
			ProcessInstanceHistory processInstance = bpmsClientService.getProcessInstanceHistoryById(processId);
			if (processInstance == null || processInstance.getEndDate() == null) return false;
			if (processInstance.getStatus() != ProcessInstanceStatus.FINISHED) return false;
			boolean hasNull = processInstance.getTaskHistoryDetailList().stream().anyMatch(task -> task.getApproved() == null);
			boolean hasFalse = processInstance.getTaskHistoryDetailList().stream().anyMatch(task -> !task.getApproved());
			return !hasNull && !hasFalse;
		} catch (Exception e) {
			return false;
		}


	}
}
