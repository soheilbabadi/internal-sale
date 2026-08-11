package com.nicico.internal.sales.salecondition.repository;

import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SaleConditionRepository extends JpaRepository<SaleConditionModel, Long>, JpaSpecificationExecutor<SaleConditionModel> {
	List<SaleConditionModel> findAllByGoodId(Long goodId);

	@Query("SELECT s FROM SaleConditionModel s " +
			"WHERE s.goodId = :goodId " +
			"AND TRUNC(s.startDate) <= TRUNC(:date) " +
			"AND (s.expireDate IS NULL OR TRUNC(s.expireDate) >= TRUNC(:date)) " +
			"ORDER BY s.id DESC")
	List<SaleConditionModel> findActiveByGoodIdAndDate(@Param("goodId") Long goodId, @Param("date") Date date);
}