package com.nicico.internal.sales.goods.repository;

import com.nicico.internal.sales.goods.model.GoodsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsRepository extends JpaRepository<GoodsModel, Long>, JpaSpecificationExecutor<GoodsModel> {
	Optional<GoodsModel> findByImeCommodityId(Long commodityId);
}
