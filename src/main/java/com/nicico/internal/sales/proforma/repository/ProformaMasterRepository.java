package com.nicico.internal.sales.proforma.repository;

import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProformaMasterRepository extends JpaRepository<ProformaMasterModel, Long>, JpaSpecificationExecutor<ProformaMasterModel> {

	List<ProformaMasterModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);

	List<ProformaMasterModel> findAllByNationalCodeInOrderByIdDesc(List<String> nationalCodes);

	Optional<ProformaMasterModel> findByProcessId(String processId);

	Optional<ProformaMasterModel> findByReversalProcessId(String processId);

	List<ProformaMasterModel> findAllByContractNoOrderByIdDesc(Long contractNo);

	boolean existsByContractNoAndWorkflowApproveStatusIn(Long contractNo, List<WorkflowApproveStatus> statuses);

	Optional<ProformaMasterModel> findFirstByPaymentCodeOrderByIdDesc(String paymentCode);

	@Query(value = "SELECT tipm.* FROM T_INS_PERFORMA_MASTER tipm " +
			"WHERE tipm.F_TRADE_ID IN ( " +
			"  SELECT tit.ID FROM TBL_IME_TRADE tit " +
			"  WHERE tit.CONTRACT_DATE > '1405/01/01' " +
			"    AND tit.CURRENCY_CODE = 1 " +
			"    AND (tit.PAYMENT_CODE, tit.CONTRACT_NO, tit.CONTRACT_DETAIL_NO) IN ( " +
			"      SELECT PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO FROM TBL_IME_SETTLEMENT " +
			"    )) AND (tipm.C_WORKFLOW_APPROVE_STATUS NOT IN ('REVERSAL','CANCELED')) AND tipm.C_PROFORMA_ISSUE_TYPE ='LETTER_OF_CREDIT_OPENING'", nativeQuery = true)
	List<ProformaMasterModel> findCancellable();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query(value = "UPDATE T_INS_PERFORMA_MASTER m SET m.C_SETTLEMENT_TYPE = (SELECT CASE s.SETTLEMENT_TYPE WHEN 'نقدی' THEN 'CASH' WHEN 'اعتباری' THEN 'CREDIT'WHEN 'انفساخ' THEN 'EXHALATION' WHEN 'نامشخص' THEN 'UNKNOWN' ELSE 'UNKNOWN' END FROM TBL_IME_SETTLEMENT s WHERE s.PAYMENT_CODE = m.C_PAYMENT_CODE AND ROWNUM = 1) WHERE m.C_SETTLEMENT_TYPE = 'UNKNOWN' AND EXISTS (SELECT 1 FROM TBL_IME_SETTLEMENT s WHERE s.PAYMENT_CODE = m.C_PAYMENT_CODE)", nativeQuery = true)
	void syncSettlementTypeFromDetails();


}
