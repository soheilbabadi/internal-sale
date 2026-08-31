package com.nicico.internal.sales.lc.repository;

import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LcRepository extends JpaRepository<LcModel, Long>, JpaSpecificationExecutor<LcModel> {
	Page<LcModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses, Pageable pageable);

	List<LcModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);


	@Query(value = "SELECT C_NOSA_CODE FROM T_INS_LC WHERE N_ISSUER_BANK_ID = :bankId AND C_NOSA_CODE LIKE :prefix% ORDER BY C_NOSA_CODE DESC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	String findLastNosaCodeByBankIdAndPrefix(Long bankId, String prefix);

	@Query(value = "SELECT C_NOSA_CODE FROM T_INS_LC WHERE N_ISSUER_BANK_ID = :bankId ORDER BY C_NOSA_CODE FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	String findLastNosaCodeByBankId(Long bankId);

	Optional<LcModel> findFirstByProformaDetailIdOrderByCreatedDateDesc(long proformaDetailId);

	Optional<LcModel> findFirstByPmsLcId(String pmsLcId);

	List<LcModel> findByProcessId(String processId);

	@Query(value = "SELECT * FROM t_ins_lc WHERE C_ACKNOWLEDGMENT = 'REMITTANCE' AND C_PMS_LC_ID IS NULL", nativeQuery = true)
	List<LcModel> findRemittanceLcWithoutPmsId();

	List<LcModel> findAllByProformaMasterId(Long proformaMasterId);

	List<LcModel> findAllByProcessId(String processId);

	@Query(value = "SELECT l.* FROM T_INS_LC l " +
			"INNER JOIN T_INS_PERFORMA_DETAIL pd ON l.N_PROFORMA_DETAIL_ID = pd.ID " +
			"WHERE pd.C_PROFORMA_REVERSAL_STATUS != 'CANCELED' and l.C_ACKNOWLEDGMENT != 'CANCELED' " +
			"AND l.N_PROFORMA_MASTER_ID = :proformaMasterId",
			nativeQuery = true)
	List<LcModel> findByMasterId(@Param("proformaMasterId") Long proformaMasterId);

	@Query(value = "select detail from LcModel lc inner join ProformaDetailModel  detail " +
			"on lc.proformaDetailId=detail.id and detail.proformaReversalStatus != 'CANCELED' where lc.id=:lcId")
	Optional<ProformaDetailModel> getDetailByLcId(@Param("lcId") Long lcId);

	@Query(value = "SELECT * FROM t_ins_lc tl WHERE tl.C_ACKNOWLEDGMENT = 'REMITTANCE' AND tl.IS_RECKONING_SEND =0 AND tl.C_WORKFLOW_APPROVE_STATUS='ACCEPTED'", nativeQuery = true)
	List<LcModel> findUnsentReckoning();

	@Query(value = "SELECT * FROM t_ins_lc tl WHERE tl.C_ACKNOWLEDGMENT != 'REMITTANCE'  AND tl.C_WORKFLOW_APPROVE_STATUS='IN_PROGRESS' AND tl.C_LC_NO IS NOT NULL", nativeQuery = true)
	List<LcModel> findReadyReckoning();

	@Query(value = "SELECT tl.* FROM t_ins_lc tl  INNER JOIN T_INS_PERFORMA_DETAIL tipd ON tl.N_PROFORMA_DETAIL_ID=tipd.ID INNER JOIN T_INS_PERFORMA_MASTER tipm ON tipd.F_PERFORMA_MASTER_ID=tipm.ID  WHERE tipm.C_WORKFLOW_APPROVE_STATUS='ACCEPTED' AND  tipd.C_PERFORMA_NO=:proformaNo", nativeQuery = true)
	Optional<LcModel> findByProformaNo(String proformaNo);

}
