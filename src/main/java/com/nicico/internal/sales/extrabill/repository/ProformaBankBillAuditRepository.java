package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.dto.ProformaBankBillAuditDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProformaBankBillAuditRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional(readOnly = true)
	public List<ProformaBankBillAuditDto> getAuditHistory(Long extraBillId) {
		String sql = """
				SELECT TO_CHAR(REV) as REV,
				       ID,
				       TO_CHAR(D_CREATED_DATE, 'YYYY/MM/DD', 'nls_calendar=persian') as D_CREATED_DATE,
				       TO_CHAR(D_LAST_MODIFIED_DATE, 'YYYY/MM/DD', 'nls_calendar=persian') as D_LAST_MODIFIED_DATE,
				       COALESCE(C_CREATED_BY, '-') as C_CREATED_BY,
				       COALESCE(C_LAST_MODIFIED_BY, '-') as C_LAST_MODIFIED_BY,
				       COALESCE(C_COMMENT, '-') as C_COMMENT,
				       COALESCE(C_DESCRIPTION, '-') as C_DESCRIPTION,
				       REVTYPE,
				       TO_CHAR(COALESCE(N_ISSUER_BANK_ID, 0)) as N_ISSUER_BANK_ID,
				       COALESCE(C_ISSUER_BANK_NAME, '-') as C_ISSUER_BANK_NAME,
				       COALESCE(C_ISSUER_BANK_BRANCH_NAME, '-') as C_ISSUER_BANK_BRANCH_NAME,
				       COALESCE(C_ISSUER_BANK_CODE, '-') as C_ISSUER_BANK_CODE,
				       TO_CHAR(COALESCE(N_AGENT_BANK_ID, 0)) as N_AGENT_BANK_ID,
				       COALESCE(C_AGENT_BANK_NAME, '-') as C_AGENT_BANK_NAME,
				       COALESCE(C_NOSA_CODE, '-') as C_NOSA_CODE,
				       COALESCE(C_SEPAM_CODE, '-') as C_SEPAM_CODE,
				       COALESCE(C_TREASURY_ID, '-') as C_TREASURY_ID,
				       COALESCE(TO_CHAR(D_ISSUE_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_ISSUE_DATE,
				       COALESCE(TO_CHAR(D_DUE_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_DUE_DATE,
				       COALESCE(C_EXTRA_BILL_FILE_ID, '-') as C_EXTRA_BILL_FILE_ID,
				       COALESCE(C_DISPATCH_FILE_ID, '-') as C_DISPATCH_FILE_ID,
				       COALESCE(C_WORKFLOW_APPROVE_STATUS, '-') as C_WORKFLOW_APPROVE_STATUS,
				       COALESCE(C_PROCESS_ID, '-') as C_PROCESS_ID,
				       COALESCE(C_REVERSAL_PROCESS_ID, '-') as C_REVERSAL_PROCESS_ID,
				       TO_CHAR(COALESCE(N_CONTRACT_NO, 0)) as N_CONTRACT_NO,
				       TO_CHAR(COALESCE(F_TRADE_ID, 0)) as F_TRADE_ID,
				       TO_CHAR(COALESCE(N_PROFORMA_MASTER_ID, 0)) as N_PROFORMA_MASTER_ID,
				       COALESCE(C_PROFORMA_INSTANCE_ID, '-') as C_PROFORMA_INSTANCE_ID,
				       COALESCE(C_ACKNOWLEDGMENT, '-') as C_ACKNOWLEDGMENT,
				       TO_CHAR(COALESCE(IS_RECKONING_SEND, 0)) as IS_RECKONING_SEND,
				       COALESCE(TO_CHAR(D_RECKONING_SEND_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_RECKONING_SEND_DATE,
				       COALESCE(C_CANCELLATION_REASON, '-') as C_CANCELLATION_REASON,
				       COALESCE(TO_CHAR(D_CANCEL_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_CANCEL_DATE,
				       COALESCE(C_PMS_BILL_ID, '-') as C_PMS_BILL_ID
				FROM T_INS_EXTRA_BANK_BILL_AUD
				WHERE ID = :extraBillId
				ORDER BY REV DESC
				""";

		var query = entityManager.createNativeQuery(sql);
		query.setParameter("extraBillId", extraBillId);

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		return results.stream()
				.map(this::mapAuditResultToDto)
				.toList();
	}

	private ProformaBankBillAuditDto mapAuditResultToDto(Object[] row) {
		int idx = 0;
		return ProformaBankBillAuditDto.builder()
				.rev((String) row[idx++])
				.id(((Number) row[idx++]).longValue())
				.dCreatedDate((String) row[idx++])
				.dLastModifiedDate((String) row[idx++])
				.cCreatedBy((String) row[idx++])
				.cLastModifiedBy((String) row[idx++])
				.cComment((String) row[idx++])
				.cDescription((String) row[idx++])
				.revtype(((Number) row[idx++]).intValue())
				.nIssuerBankId((String) row[idx++])
				.cIssuerBankName((String) row[idx++])
				.cIssuerBankBranchName((String) row[idx++])
				.cIssuerBankCode((String) row[idx++])
				.nAgentBankId((String) row[idx++])
				.cAgentBankName((String) row[idx++])
				.cNosaCode((String) row[idx++])
				.cSepamCode((String) row[idx++])
				.cTreasuryId((String) row[idx++])
				.dIssueDate((String) row[idx++])
				.dDueDate((String) row[idx++])
				.cExtraBillFileId((String) row[idx++])
				.cDispatchFileId((String) row[idx++])
				.cWorkflowApproveStatus((String) row[idx++])
				.cProcessId((String) row[idx++])
				.cReversalProcessId((String) row[idx++])
				.nContractNo((String) row[idx++])
				.fTradeId((String) row[idx++])
				.nProformaMasterId((String) row[idx++])
				.cProformaInstanceId((String) row[idx++])
				.cAcknowledgment((String) row[idx++])
				.isReckoningSend((String) row[idx++])
				.dReckoningSendDate((String) row[idx++])
				.cCancellationReason((String) row[idx++])
				.dCancelDate((String) row[idx++])
				.cPmsBillId((String) row[idx++])
				.build();
	}
}
