package com.nicico.internal.sales.remittance.repository;

import com.nicico.internal.sales.remittance.model.RemittanceProformaDataProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemittanceProformaDataProviderRepository extends JpaRepository<RemittanceProformaDataProviderModel, Long>, JpaSpecificationExecutor<RemittanceProformaDataProviderModel> {
	Optional<RemittanceProformaDataProviderModel> findByPaymentCode(String paymentCode);

	Optional<RemittanceProformaDataProviderModel> findFirstByIdOrderByIdDesc(Long id);

}
