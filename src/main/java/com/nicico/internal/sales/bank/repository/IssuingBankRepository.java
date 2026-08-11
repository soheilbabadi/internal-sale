package com.nicico.internal.sales.bank.repository;

import com.nicico.internal.sales.bank.model.IssuingBankModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssuingBankRepository extends JpaRepository<IssuingBankModel, Long>, JpaSpecificationExecutor<IssuingBankModel> {


	//	Optional<IssuingBankModel> findFirstByFixedBranchCodeAndBaseBankModel_BankTitle(String branchCode, String bankName);
	@Query(value = """
			  SELECT ib.*
			           FROM T_INS_ISSUING_BANKS ib
			           INNER JOIN T_INS_BANK_BASE bb
			           ON ib.C_BANK_CODE = bb.C_BANK_CODE
			           WHERE LPAD(ib.C_BRANCH_CODE, 10, '0') = LPAD(:branchCode, 10, '0')
			           AND UPPER(bb.C_BANK_TITLE) LIKE '%' || UPPER(:bankName) || '%'
			           FETCH FIRST 1 ROW ONLY
			""", nativeQuery = true)
	Optional<IssuingBankModel> findFirstByBranchCodeAndBankNameLike(@Param("branchCode") String branchCode, @Param("bankName") String bankName);

}