package com.nicico.internal.sales.pricing.repository;

import com.nicico.internal.sales.pricing.model.PricingCurrencyTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingCurrencyTypeRepository extends JpaRepository<PricingCurrencyTypeModel, Long>, JpaSpecificationExecutor<PricingCurrencyTypeModel> {

	Optional<PricingCurrencyTypeModel> findTopByOrderByIdDesc();

}