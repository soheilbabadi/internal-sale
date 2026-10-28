package com.nicico.internal.sales.remittance.repository;

import com.nicico.internal.sales.remittance.model.RemittanceSulfuricAcidDataProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemittanceSulfuricAcidDataProviderRepository extends JpaRepository<RemittanceSulfuricAcidDataProviderModel, Long>, JpaSpecificationExecutor<RemittanceSulfuricAcidDataProviderModel> {
	Optional<RemittanceSulfuricAcidDataProviderModel> findFirstByIdOrderByContractDateDesc(Long id);
}
