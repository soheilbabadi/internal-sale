package com.nicico.internal.sales.pms.repository;

import com.nicico.internal.sales.pms.model.PMSBankLcModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PMSBankLcRepository extends JpaRepository<PMSBankLcModel, String>, JpaSpecificationExecutor<PMSBankLcModel> {
	Optional<PMSBankLcModel> findFirstByBankAndFixedBranchCode(String bank, String branch);

}