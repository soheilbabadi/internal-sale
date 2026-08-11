package com.nicico.internal.sales.goods.repository;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GoodBucketRepository extends JpaRepository<GoodsBucketModel, Long>, JpaSpecificationExecutor<GoodsBucketModel> {
	List<GoodsBucketModel> findAllByGoodId(Long goodId);

	List<GoodsBucketModel> findAllByGoodIdOrderByIdDesc(Long goodId);

	@Query("SELECT g FROM GoodsBucketModel g " +
			"WHERE g.goodId = :goodId " +
			"AND TRUNC(g.startDate) <= TRUNC(:date) " +
			"AND (g.expireDate IS NULL OR TRUNC(g.expireDate) >= TRUNC(:date)) " +
			"ORDER BY g.id DESC")
	List<GoodsBucketModel> findActiveByGoodIdAndDate(@Param("goodId") Long goodId, @Param("date") Date date);
}
