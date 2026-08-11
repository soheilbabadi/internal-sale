package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.RemittanceVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.model.WorkflowModel;

import java.util.Map;

public interface ProcessVariableProvider {
	WorkflowModel getProformaWorkflowByTitle();

	WorkflowModel getReversalWorkflowByTitle();

	Map<String, Object> createProformaRequestVariables(ProformaVariablesInput input);

	Map<String, Object> createReversalRequestVariables(ProformaVariablesInput input);

	Map<String, Object> createExtraBillRequestVariables(ProformaVariablesInput input);

	Map<String, Object> createRemittanceRequestVariable(RemittanceVariablesInput input);

	Map<String, String> getProformaUserAccess();

	Map<String, String> getRemittanceUserAccess();

	Map<String, String> getReversalUserAccess();

	WorkflowModel getLcWorkflowByTitle();

	WorkflowModel getExtraBillWorkflowByTitle();

	WorkflowModel getRemittanceWorkflowByTitle();

	Map<String, Object> createLCRequestVariables(ProformaVariablesInput input);

	Map<String, String> getLcUserAccess();


	Map<String, String> getExtraBillUserAccess();

	ReviewTaskRequest prepareReviewTaskRequest(TaskActionDto taskActionDto);

	boolean isProcessFinished(String processId);

	boolean isProcessAcceptedFinally(String processId);
}
