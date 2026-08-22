package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraBillRepository extends JpaRepository<ProformaBankBillModel, Long>, JpaSpecificationExecutor<ProformaBankBillModel> {
	List<ProformaBankBillModel> findAllByProformaMasterId(Long masterId);

	List<ProformaBankBillModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);

	ProformaBankBillModel findByProcessId(String processId);

	@Query(value = "SELECT * FROM t_ins_proforma_detail WHERE C_PROFORMA_MASTER_ID IN (SELECT C_PROFORMA_MASTER_ID FROM t_ins_proforma_bank_bill WHERE id = :billId)", nativeQuery = true)
	Optional<ProformaDetailModel> getDetailByBillId(@Param("billId") Long billId);

	@Query(value = "SELECT C_NOSA_CODE FROM T_INS_PROFORMA_BANK_BILL WHERE N_ISSUER_BANK_ID = :bankId AND C_NOSA_CODE LIKE :prefix% ORDER BY C_NOSA_CODE DESC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	String findLastNosaCodeByBankIdAndPrefix(Long bankId, String prefix);

	boolean existsByProformaDetailIdAndWorkflowApproveStatusNot(Long proformaDetailId, WorkflowApproveStatus status);
}
