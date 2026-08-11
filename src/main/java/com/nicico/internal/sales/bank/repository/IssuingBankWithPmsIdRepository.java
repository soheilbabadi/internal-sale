package com.nicico.internal.sales.bank.repository;

import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IssuingBankWithPmsIdRepository extends JpaRepository<IssuingBankWithPmsIdView, Long>, JpaSpecificationExecutor<IssuingBankWithPmsIdView> {
	Optional<IssuingBankWithPmsIdView> findFirstByPmsBaseBankIdAndBranchCodeAndPmsLcBankIdNotNull(String pmsBaseBankId, String branchCode);
}
