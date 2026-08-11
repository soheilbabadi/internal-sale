package com.nicico.internal.sales.lc.service;

import com.nicico.internal.sales.wf.model.WorkflowModel;

import java.util.List;

public interface LcValidationService {
	void validateStart(long proformaId);

	WorkflowModel getWorkflowByTitle();

	List<String> validateCancel(long lcId);
}
