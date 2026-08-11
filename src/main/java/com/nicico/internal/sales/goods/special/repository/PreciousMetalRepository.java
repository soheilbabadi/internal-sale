package com.nicico.internal.sales.goods.special.repository;

import com.nicico.internal.sales.goods.special.model.PreciousMetalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreciousMetalRepository extends JpaRepository<PreciousMetalModel, Long>, JpaSpecificationExecutor<PreciousMetalModel> {
	Optional<PreciousMetalModel> findByImeCommodityId(Long imeCommodityId);

	Optional<PreciousMetalModel> findByImeCommoditySymbol(String imeCommoditySymbol);
}
