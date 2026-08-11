package com.nicico.internal.sales.remittance.repository;

import com.nicico.internal.sales.remittance.model.RemittanceDataProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceDataProviderRepository extends JpaRepository<RemittanceDataProviderModel, Long>, JpaSpecificationExecutor<RemittanceDataProviderModel> {
	Optional<RemittanceDataProviderModel> findByPaymentCode(String paymentCode);

	List<RemittanceDataProviderModel> findAllByPaymentCode(String paymentCode);


}
