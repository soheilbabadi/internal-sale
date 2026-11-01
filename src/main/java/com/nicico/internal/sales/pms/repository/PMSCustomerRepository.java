package com.nicico.internal.sales.pms.repository;

import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface PMSCustomerRepository extends JpaRepository<PMSCustomerModel, Long>, JpaSpecificationExecutor<PMSCustomerModel> {
//    Optional<PMSCustomerModel> findFirstByRegisterNumberContainingOrderByIdDesc(String registerNumber);

	Optional<PMSCustomerModel> findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(String economicCode, String registerNumber);

	Optional<PMSCustomerModel> findFirstByEconomicCodeContainingOrderByIdDesc(String economicCode);

	// Exact-match variant for the duplicate-provisioning guard in PmsCustomerCreateRabbitServiceImpl.
	// The Containing variants above build LIKE '%value%', which can match a different customer whose
	// code merely contains ours as a substring; this one uses '=' so an identity lookup cannot produce
	// that false positive.
	Optional<PMSCustomerModel> findFirstByEconomicCodeEquals(String economicCode);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query(value = "BEGIN dbms_mview.refresh('MVW_DBLINK_CUSTOMER1TBL','C'); END;", nativeQuery = true)
	void updatePmsCustomerMaterializedView();
}
