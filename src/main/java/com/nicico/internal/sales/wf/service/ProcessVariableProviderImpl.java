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
import com.nicico.internal.sales.wf.enums.*;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessVariableProviderImpl implements ProcessVariableProvider {
	public static final String PROCESS_TITLE_PREINVOCE = "PREINVOICE";
	public static final String PROCESS_TITLE_REVERSAL = "REVERSAL";
	public static final String PROCESS_TITLE_LC = "LC";
	public static final String PROCESS_TITLE_EXTRA_BILL = "EXTRA_BILL";
	public static final String PROCESS_TITLE_REMITTANCE = "REMITTANCE";
	public static final String PROCESS_TITLE_GAAM = "GAAM_BOUND";

	private final WorkflowRepository workflowRepository;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final BpmsClientService bpmsClientService;

	public Map<String, String> resolveUserAccess(List<ProcessUserAccessModel> accessList, List<String> requiredVariables) {
		return requiredVariables.stream()
				.collect(Collectors.toMap(variable -> variable, variable -> {
					List<ProcessUserAccessModel> matches = accessList.stream()
							.filter(a -> a.getProcessVariable().equals(variable))
							.toList();

					String title = matches.stream()
							.findFirst()
							.map(ProcessUserAccessModel::getProcessVariableTitle)
							.orElse(variable);

					return matches.stream()
							.min(Comparator.comparing(a -> !a.getUsername().equals(SecurityUtil.getUsername())))
							.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
									"متغیر " + title + " به هیچ کاربری تخصیص نیافته است"))
							.getUserId().toString();
				}));
	}


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
	public WorkflowModel getGaamWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(PROCESS_TITLE_GAAM)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("فرایند بثبت اوراق گام وجود ندارد"));
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
		return createRequestVariables(input, getExtraBillUserAccess());
	}

	@Override
	public Map<String, Object> createLCRequestVariables(ProformaVariablesInput input) {
		return createRequestVariables(input, getLcUserAccess());
	}

	@Override
	public Map<String, Object> createRemittanceRequestVariable(RemittanceVariablesInput input) {
		return createRequestVariables(input, getRemittanceUserAccess());
	}


	@Override
	public Map<String, Object> createGaamRequestVariables(ProformaVariablesInput input) {
		return createRequestVariables(input, getGaamUserAccess());
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
		variables.put("issuerName", SecurityUtil.getFirstName() + " " + SecurityUtil.getLastName());
		variables.put("issuerId", SecurityUtil.getUserId());

		variables.putAll(userAccess);
		Map<String, Object> wrapped = new HashMap<>(variables);
		wrapped.put("INSTANCE_DETAILS", variables);
		return wrapped;
	}


	private <E extends Enum<E>> Map<String, String> buildUserAccess(WorkflowModel workflow, E[] variables) {
		List<ProcessUserAccessModel> accessList = processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		Map<String, String> userAccess = new HashMap<>(resolveUserAccess(
				accessList,
				Arrays.stream(variables).map(Enum::name).toList()));
		userAccess.put("starter", SecurityUtil.getUserId().toString());
		userAccess.put("processName", workflow.getProcessTitle());
		userAccess.put("processLocalName", workflow.getProcessLocalTitle());
		return userAccess;
	}

	@Override
	public Map<String, String> getGaamUserAccess() {
		return buildUserAccess(getGaamWorkflowByTitle(), GaamProcessVariable.values());
	}

	@Override
	public Map<String, String> getProformaUserAccess() {
		return buildUserAccess(getProformaWorkflowByTitle(), ProformaProcessVariable.values());
	}

	@Override
	public Map<String, String> getRemittanceUserAccess() {
		return buildUserAccess(getRemittanceWorkflowByTitle(), RemittanceProcessVariable.values());
	}

	@Override
	public Map<String, String> getReversalUserAccess() {
		return buildUserAccess(getReversalWorkflowByTitle(), ReversalProcessVariable.values());
	}

	@Override
	public Map<String, String> getLcUserAccess() {
		return buildUserAccess(getLcWorkflowByTitle(), LcProcessVariable.values());
	}

	@Override
	public Map<String, String> getExtraBillUserAccess() {
		return buildUserAccess(getExtraBillWorkflowByTitle(), ExtraBillProcessVariable.values());
	}

	@Override
	public ProformaVariablesInput buildProformaVariablesInput(ProformaMasterModel masterModel) {
		ProformaDetailModel detail = masterModel.getProformaDetailModelLists().get(0);
		ProformaVariablesInput input = new ProformaVariablesInput();
		input.setProformaMasterId(masterModel.getId());
		input.setContractDate(detail.getContractDate());
		input.setGoodId(masterModel.getGoodId());
		input.setGoodName(masterModel.getGoodName());
		input.setCustomerName(masterModel.getCustomerName());
		input.setContractNo(String.valueOf(masterModel.getContractNo()));
		input.setCommission(masterModel.getCommissionPercentage());
		return input;
	}

	@Override
	public ProformaVariablesInput buildExtraBillVariablesInput(ProformaMasterModel proformaMaster) {
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

	@Override
	public ProformaVariablesInput buildGaamVariablesInput(ProformaMasterModel proformaMaster) {
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
		try {

			var processInstance = bpmsClientService.getProcessInstanceHistoryById(processId);
			return processInstance.getStatus() != ProcessInstanceStatus.ACTIVE;
		} catch (Exception exception) {
			return false;
		}
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
