package com.nicico.internal.sales.pricing.repository;

import com.nicico.internal.sales.pricing.model.PricingCurrencyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricingCurrencyRepository extends JpaRepository<PricingCurrencyModel, Long>, JpaSpecificationExecutor<PricingCurrencyModel> {

	Optional<PricingCurrencyModel> findByPersianShortDate(String persianShortDate);

	boolean existsByPersianShortDate(String persianShortDate);

	Optional<PricingCurrencyModel> findByShortDate(Date shortDate);

	List<PricingCurrencyModel> findByShortDateBetween(Date startDate, Date endDate);

	List<PricingCurrencyModel> findByShortDateBetweenOrderByShortDateDesc(Date shortDateAfter, Date shortDateBefore);

	Optional<PricingCurrencyModel> findTopByOrderByShortDateDesc();


}