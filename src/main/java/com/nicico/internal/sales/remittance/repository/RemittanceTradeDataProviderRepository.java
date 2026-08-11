package com.nicico.internal.sales.remittance.repository;

import com.nicico.internal.sales.remittance.model.RemittanceTradeDataProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemittanceTradeDataProviderRepository extends JpaRepository<RemittanceTradeDataProviderModel, Long>, JpaSpecificationExecutor<RemittanceTradeDataProviderModel> {


	Optional<RemittanceTradeDataProviderModel> findFirstByIdOrderByContractDateDesc(Long id);


}
