package com.nicico.internal.sales.lc.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LcValidationServiceImpl implements LcValidationService {
	private static final String WORKFLOW_TITLE = "LC";
	private static final String MSG_INVALID_DATA = "اطلاعات اعتبار اسنادی نادرست است";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_PROFORMA_FROM_CREDIT_FACILITIES = "پیش فاکتور از محل مطالبات است و امکان صدور اعتبار اسنادی ندارد";
	private static final String MSG_PROFORMA_NOT_ACCEPTED = "پیش فاکتور در وضعیت تایید شده نیست";
	private static final String MSG_LC_IN_PROGRESS = "برای این پیش فاکتور اعتبار اسنادی در حال انجام وجود دارد";
	private static final String MSG_LC_ALREADY_ACCEPTED = "برای این پیش فاکتور اعتبار اسنادی تایید شده وجود دارد";
	private static final String MSG_SYSTEM_VARIABLE_NOT_FOUND = "متغیر {0} در سیستم تعریف نشده است";
	private static final String MSG_WORKFLOW_NOT_FOUND = "فرایند مربوطه پیدا نشد";
	private static final String MSG_LC_ALREADY_CANCELED = "این اعتبار اسنادی قبلا ابطال شده است";
	private static final String MSG_LC_MUST_BE_HANDLED_IN_WORKFLOW = "این اعتبار اسنادی باید از طریق کارتابل تعیین تکلیف شود";
	private static final String MSG_REMITTANCE_ALREADY_ISSUED = "برای این اعتبار اسنادی حواله صادر شده است";


	private final ProformaMasterRepository proformaMasterRepository;
	private final LcRepository lcRepository;
	private final ProcessUserAccessRepository userAccessRepository;
	private final WorkflowRepository workflowRepository;
	private final RemittanceMasterRepository remittanceRepository;

	@Override
	public void validateStart(long masterId) {
		List<String> errors = new ArrayList<>();
		ProformaMasterModel proforma = proformaMasterRepository.findById(masterId)
				.orElseThrow(() ->
						new InternalSaleCustomException.ValidationException(
								MSG_INVALID_DATA, List.of(MSG_PROFORMA_NOT_FOUND))
				);
		validateProformaIssueType(proforma, errors);
		validateProformaStatus(proforma, errors);
		validateExistingLcs(masterId);
		WorkflowModel workflow = getWorkflowByTitle();
		validateSystemVariables(workflow, errors);
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
		}
	}


	private void validateProformaIssueType(ProformaMasterModel proforma, List<String> errors) {
		if (proforma.getProformaIssueType() == ProformaIssueType.FROM_CREDIT_FACILITIES
				|| proforma.getProformaIssueType() == ProformaIssueType.CASH) {
			errors.add(MSG_PROFORMA_FROM_CREDIT_FACILITIES);
		}
	}

	private void validateProformaStatus(ProformaMasterModel proforma, List<String> errors) {
		if (proforma.getWorkflowApproveStatus() != WorkflowApproveStatus.ACCEPTED) {
			errors.add(MSG_PROFORMA_NOT_ACCEPTED);
			return;
		}
		boolean allCanceled = proforma.getProformaDetailModelLists().stream()
				.allMatch(d ->
						d.getProformaReversalStatus() == ProformaReversalStatus.CANCELED);
		if (allCanceled) {
			errors.add(MSG_PROFORMA_NOT_ACCEPTED);
		}
	}

	private void validateExistingLcs(long masterId) {
		lcRepository.findByMasterId(masterId).forEach(lc -> {
			if (lc.getWorkflowApproveStatus() == WorkflowApproveStatus.IN_PROGRESS || lc.getWorkflowApproveStatus() == WorkflowApproveStatus.DRAFT) {
				throw new InternalSaleCustomException.ValidationException(MSG_LC_IN_PROGRESS);
			}
			if (lc.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
				throw new InternalSaleCustomException.ValidationException(MSG_LC_ALREADY_ACCEPTED);
			}
		});
	}

	private void validateSystemVariables(WorkflowModel workflow, List<String> errors) {
		var accessList = userAccessRepository.findAllByProcessTitle(workflow.getProcessTitle());
		List<LcProcessVariable> requiredVariables = List.of(
				LcProcessVariable.CreditBridge,
				LcProcessVariable.SettleSure,
				LcProcessVariable.RemitSure,
				LcProcessVariable.FinalCheck
		);
		for (LcProcessVariable variable : requiredVariables) {
			boolean exists = accessList.stream()
					.anyMatch(a -> a.getProcessVariable().equals(variable.name()));
			if (!exists) {
				errors.add(MessageFormat.format(
						MSG_SYSTEM_VARIABLE_NOT_FOUND, variable.name()));
			}
		}
	}


	@Override
	public WorkflowModel getWorkflowByTitle() {
		return workflowRepository.findByProcessTitleIgnoreCase(WORKFLOW_TITLE)
				.orElseThrow(() ->
						new InternalSaleCustomException.ValidationException(
								MSG_WORKFLOW_NOT_FOUND));
	}

	@Override
	public List<String> validateCancel(long lcId) {
		List<String> errors = new ArrayList<>();
		var lcModel = lcRepository.findById(lcId)
				.orElseThrow(() ->
						new InternalSaleCustomException.ValidationException(
								MSG_INVALID_DATA, List.of(MSG_INVALID_DATA)));
		if (lcModel.getWorkflowApproveStatus() == WorkflowApproveStatus.REVERSAL
				|| lcModel.getWorkflowApproveStatus() == WorkflowApproveStatus.CANCELED) {
			errors.add(MSG_LC_ALREADY_CANCELED);
		}
		if (lcModel.getWorkflowApproveStatus() == WorkflowApproveStatus.IN_PROGRESS) {
			errors.add(MSG_LC_MUST_BE_HANDLED_IN_WORKFLOW);
		}
		remittanceRepository
				.findFirstByPaymentCodeOrderByIdDesc(lcModel.getPaymentCode())
				.filter(r ->
						r.getWorkflowApproveStatus() == WorkflowApproveStatus.IN_PROGRESS
								|| r.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED)
				.ifPresent(r -> errors.add(MSG_REMITTANCE_ALREADY_ISSUED));
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
		}
		return errors;
	}


}
