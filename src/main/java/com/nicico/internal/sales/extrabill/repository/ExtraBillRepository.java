package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraBillRepository extends JpaRepository<ExtraBankBillModel, Long>, JpaSpecificationExecutor<ExtraBankBillModel> {
	List<ExtraBankBillModel> findAllByProformaMasterId(Long masterId);


	List<ExtraBankBillModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);

	ExtraBankBillModel findByProcessId(String processId);

	@Query(value = "SELECT * FROM T_INS_PERFORMA_DETAIL WHERE F_PERFORMA_MASTER_ID IN (SELECT F_PERFORMA_MASTER_ID FROM T_INS_EXTRA_BANK_BILL WHERE id = :billId)", nativeQuery = true)
	Optional<ProformaDetailModel> getDetailByBillId(@Param("billId") Long billId);


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


}
