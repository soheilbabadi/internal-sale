package com.nicico.internal.sales.proforma.repository;

import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProformaDetailRepository extends JpaRepository<ProformaDetailModel, Long>, JpaSpecificationExecutor<ProformaDetailModel> {
	@Query(value = "SELECT MAX(p.C_PERFORMA_NO) FROM T_INS_PERFORMA_DETAIL p", nativeQuery = true)
	String findFirstProformaNumber();

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "UPDATE T_INS_PERFORMA_DETAIL D SET D.C_SETTLEMENT_TYPE = (SELECT M.C_SETTLEMENT_TYPE  FROM T_INS_PERFORMA_MASTER M WHERE M.ID = D.F_PERFORMA_MASTER_ID  AND d.C_SETTLEMENT_TYPE = 'UNKNOWN') WHERE EXISTS (SELECT 1 FROM T_INS_PERFORMA_MASTER M WHERE M.ID = D.F_PERFORMA_MASTER_ID AND m.C_SETTLEMENT_TYPE != 'UNKNOWN')", nativeQuery = true)
	void updateSettlementTypeFromTradeSettlement();


	List<ProformaDetailModel> findAllByProformaMasterId(long masterId);

}
