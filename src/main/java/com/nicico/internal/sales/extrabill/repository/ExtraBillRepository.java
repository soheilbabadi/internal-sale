package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraBillRepository extends JpaRepository<ExtraBankBillModel, Long>, JpaSpecificationExecutor<ExtraBankBillModel> {
	List<ExtraBankBillModel> findAllByProformaMasterId(Long masterId);


	List<ExtraBankBillModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);

	ExtraBankBillModel findByProcessId(String processId);


	@Query(value = "SELECT C_NOSA_CODE FROM T_INS_EXTRA_BANK_BILL WHERE N_ISSUER_BANK_ID = :bankId AND C_NOSA_CODE LIKE :prefix% ORDER BY C_NOSA_CODE DESC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	String findLastNosaCodeByBankIdAndPrefix(Long bankId, String prefix);

	@Query(value = """
			SELECT *
			FROM T_INS_EXTRA_BANK_BILL
			WHERE F_PROFORMA_DETAIL_ID = :detailId
			  AND C_WORKFLOW_APPROVE_STATUS = 'IN_PROGRESS'
			ORDER BY ID DESC
			FETCH FIRST 1 ROW ONLY
			""", nativeQuery = true)
	Optional<ExtraBankBillModel> findLastInProgressByProformaDetailId(
			@Param("detailId") Long detailId
	);

	List<ExtraBankBillModel> findAllByProcessId(String processInstanceId);

	Optional<ExtraBankBillModel> findFirstByPmsBillId(String pmsBillId);

	@Query(value = "SELECT b.* FROM T_INS_EXTRA_BANK_BILL b " +
			"INNER JOIN T_INS_PERFORMA_DETAIL pd ON b.F_PROFORMA_DETAIL_ID = pd.ID " +
			"WHERE pd.C_PROFORMA_REVERSAL_STATUS != 'CANCELED' and b.C_ACKNOWLEDGMENT != 'CANCELED' " +
			"AND b.F_PERFORMA_MASTER_ID = :proformaMasterId",
			nativeQuery = true)
	List<ExtraBankBillModel> findByMasterId(@Param("proformaMasterId") Long proformaMasterId);

	@Query("""
			SELECT b FROM ExtraBankBillModel b WHERE b.nosaCode IS NULL 
			AND b.workflowApproveStatus in ( com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus.ACCEPTED) 
			AND (b.sepamCode IS NOT NULL OR b.treasuryId IS NOT NULL)
			ORDER BY b.id ASC
			""")
	List<ExtraBankBillModel> findExtraBillsReadyForNosaAssignment(Pageable pageable);

	@Query("""
			SELECT count(b) FROM ExtraBankBillModel b WHERE b.nosaCode IS NULL 
			AND b.workflowApproveStatus in ( com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus.ACCEPTED) 
			AND (b.sepamCode IS NOT NULL OR b.treasuryId IS NOT NULL)
			""")
	long countExtraBillsReadyForNosaAssignment();

}

