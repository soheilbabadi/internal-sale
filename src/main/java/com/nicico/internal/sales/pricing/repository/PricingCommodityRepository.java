package com.nicico.internal.sales.pricing.repository;

import com.nicico.internal.sales.pricing.model.PricingCommodityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricingCommodityRepository extends JpaRepository<PricingCommodityModel, Long>, JpaSpecificationExecutor<PricingCommodityModel> {


	Optional<PricingCommodityModel> findByShortDate(Date shortDate);

	Optional<PricingCommodityModel> findByPersianShortDate(String persianShortDate);

	List<PricingCommodityModel> findByShortDateBetween(Date startDate, Date endDate);

	Optional<PricingCommodityModel> findTopByOrderByShortDateDesc();

	List<PricingCommodityModel> findTop5ByOrderByShortDateDesc();

	boolean existsByShortDate(Date shortDate);

	boolean existsByPersianShortDate(String persianShortDate);

	void deleteByShortDate(Date shortDate);


}