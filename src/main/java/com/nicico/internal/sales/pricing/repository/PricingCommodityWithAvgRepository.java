package com.nicico.internal.sales.pricing.repository;

import com.nicico.internal.sales.pricing.model.PricingCommodityWithAvgModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricingCommodityWithAvgRepository extends JpaRepository<PricingCommodityWithAvgModel, Long>, JpaSpecificationExecutor<PricingCommodityWithAvgModel> {

	Optional<PricingCommodityWithAvgModel> findByShortDate(Date shortDate);

	Optional<PricingCommodityWithAvgModel> findByPersianShortDate(String persianShortDate);

	List<PricingCommodityWithAvgModel> findByShortDateBetween(Date startDate, Date endDate);

	Optional<PricingCommodityWithAvgModel> findTopByOrderByShortDateDesc();

	List<PricingCommodityWithAvgModel> findTop5ByOrderByShortDateDesc();

	boolean existsByShortDate(Date shortDate);

	boolean existsByPersianShortDate(String persianShortDate);

	// Note: delete methods are typically not used for read-only @Subselect models
	// void deleteByShortDate(Date shortDate); //不建议用于只读模型
}