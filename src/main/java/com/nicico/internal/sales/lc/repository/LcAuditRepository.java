package com.nicico.internal.sales.lc.repository;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.dto.LcAuditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LcAuditRepository {
	private static final String MSG_LC_NOT_FOUND = "اعتبار اسنادی وجود ندارد";
	private final LcRepository lcRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional(readOnly = true)
	public List<LcAuditDto> getAuditHistory(Long lcId) {
		boolean existLc = lcRepository.existsById(lcId);
		if (!existLc) {
			throw new InternalSaleCustomException.ValidationException(
					MSG_LC_NOT_FOUND);
		}
		String sql = """
				SELECT TO_CHAR(REV) as REV,
				ID,
				       TO_CHAR(D_CREATED_DATE, 'YYYY/MM/DD', 'nls_calendar=persian') as D_CREATED_DATE,
				       TO_CHAR(D_LAST_MODIFIED_DATE, 'YYYY/MM/DD', 'nls_calendar=persian') as D_LAST_MODIFIED_DATE,
				       C_CREATED_BY,
				       COALESCE(C_LAST_MODIFIED_BY, '-') as C_LAST_MODIFIED_BY,
				       COALESCE(C_COMMENT, '-') as C_COMMENT,
				       COALESCE(C_DESCRIPTION, '-') as C_DESCRIPTION,
				       REVTYPE,
				       C_PERFORMA_NO,
				       C_PERFORMA_DATE,
				       TO_CHAR(N_CONTRACT_NO) as N_CONTRACT_NO,
				       COALESCE(C_LC_NO, '-') as C_LC_NO,
				       COALESCE(TO_CHAR(D_LC_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_LC_DATE,
				       TO_CHAR(COALESCE(N_TRADING_BANK_ID, 0)) as N_TRADING_BANK_ID,
				       COALESCE(C_TRADING_BANK_TITLE, '-') as C_TRADING_BANK_TITLE,
				       COALESCE(C_TRADING_BRANCH_TITLE, '-') as C_TRADING_BRANCH_TITLE,
				       TO_CHAR(COALESCE(N_CREDIT_EXPIRE_PERIOD, 0)) as N_CREDIT_EXPIRE_PERIOD,
				       TO_CHAR(COALESCE(N_PAYMENT_DEFERRAL, 0)) as N_PAYMENT_DEFERRAL,
				       TO_CHAR(COALESCE(N_DEADLINE_DAYS, 0)) as N_DEADLINE_DAYS,
				       COALESCE(C_WORKFLOW_APPROVE_STATUS, '-') as C_WORKFLOW_APPROVE_STATUS,
				       COALESCE(C_PROCESS_ID, '-') as C_PROCESS_ID,
				       COALESCE(TO_CHAR(D_LC_EXPIRY_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_LC_EXPIRY_DATE,
				       COALESCE(TO_CHAR(D_SETTLEMENT_DUE_DATE, 'YYYY/MM/DD', 'nls_calendar=persian'), '-') as D_SETTLEMENT_DUE_DATE,
				       TO_CHAR(COALESCE(N_ISSUER_BANK_ID, 0)) as N_ISSUER_BANK_ID,
				       COALESCE(C_ISSUER_BANK_NAME, '-') as C_ISSUER_BANK_NAME,
				       COALESCE(C_ISSUER_BANK_BRANCH_NAME, '-') as C_ISSUER_BANK_BRANCH_NAME,
				       COALESCE(C_ISSUER_BANK_CODE, '-') as C_ISSUER_BANK_CODE,
				       COALESCE(C_LC_FILE_ID, '-') as C_LC_FILE_ID,
				       COALESCE(C_DISPATCH_FILE_ID, '-') as C_DISPATCH_FILE_ID,
				       COALESCE(C_PROFORMA_FILE_ID, '-') as C_PROFORMA_FILE_ID,
				       TO_CHAR(COALESCE(N_PROFORMA_MASTER_ID, 0)) as N_PROFORMA_MASTER_ID,
				       TO_CHAR(COALESCE(N_PROFORMA_DETAIL_ID, 0)) as N_PROFORMA_DETAIL_ID,
				       COALESCE(C_PROFORMA_INSTANCE_ID, '-') as C_PROFORMA_INSTANCE_ID,
				       C_LC_INSTANCE_ID,
				       TO_CHAR(COALESCE(IS_REQUIRE_DISPATCH_FILE, 0)) as IS_REQUIRE_DISPATCH_FILE,
				       COALESCE(C_NOTIFICATION_DOCUMENT_ID, '-') as C_NOTIFICATION_DOCUMENT_ID,
				       COALESCE(C_NOSA_CODE, '-') as C_NOSA_CODE,
				       C_PAYMENT_CODE,
				       COALESCE(C_PMS_LC_ID, '-') as C_PMS_LC_ID
				
				FROM T_INS_LC_AUD
				WHERE ID = :lcId
				ORDER BY REV DESC
				""";
		var query = entityManager.createNativeQuery(sql);
		query.setParameter("lcId", lcId);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		log.info("Found {} audit records for LC ID: {}", results.size(), lcId);
		return results.stream()
				.map(this::mapAuditResultToDto)
				.toList();
	}

	private LcAuditDto mapAuditResultToDto(Object[] result) {
		return LcAuditDto.builder()
				.rev((String) result[0])
				.id(safeToNumber(result[1], Number::longValue))
				.createdDate((String) result[2])
				.lastModifiedDate((String) result[3])
				.createdBy((String) result[4])
				.lastModifiedBy((String) result[5])
				.comment((String) result[6])
				.description((String) result[7])
				.revisionType(getRevisionTypeDescription(safeToNumber(result[8], Number::intValue)))
				.performaNo((String) result[9])
				.performaDate((String) result[10])
				.contractNo((String) result[11])
				.lcNo((String) result[12])
				.lcDate((String) result[13])
				.tradingBankId((String) result[14])
				.tradingBankTitle((String) result[15])
				.tradingBankBranchTitle((String) result[16])
				.creditExpirePeriod((String) result[17])
				.paymentDeferral((String) result[18])
				.deadlineDays((String) result[19])
				.workflowApproveStatus((String) result[20])
				.processId((String) result[21])
				.lcExpiryDate((String) result[22])
				.settlementDueDate((String) result[23])
				.issuerBankId((String) result[24])
				.issuerBankName((String) result[25])
				.issuerBankBranchName((String) result[26])
				.issuerBankCode((String) result[27])
				.lcFileId((String) result[28])
				.dispatchFileId((String) result[29])
				.proformaFileId((String) result[30])
				.proformaMasterId((String) result[31])
				.proformaDetailId((String) result[32])
				.proformaInstanceId((String) result[33])
				.lcInstanceId((String) result[34])
				.requireDispatchFile((String) result[35])
				.notificationDocumentId((String) result[36])
				.nosaCode((String) result[37])
				.paymentCode((String) result[38])
				.pmsLcId((String) result[39])
				.build();
	}

	private String getRevisionTypeDescription(Integer revisionType) {
		return switch (revisionType) {
			case 0 -> "ایجاد";
			case 1 -> "ویرایش";
			case 2 -> "حذف";
			default -> "نامشخص";
		};
	}

	private <T> T safeToNumber(Object value, java.util.function.Function<Number, T> converter) {
		if (value == null) return null;
		if (value instanceof Number number) return converter.apply(number);
		return null;
	}
}
