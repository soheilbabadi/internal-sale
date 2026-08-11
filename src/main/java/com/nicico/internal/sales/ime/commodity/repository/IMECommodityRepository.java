package com.nicico.internal.sales.ime.commodity.repository;

import com.nicico.internal.sales.ime.commodity.model.IMECommodityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IMECommodityRepository extends JpaRepository<IMECommodityModel, Long>, JpaSpecificationExecutor<IMECommodityModel> {
	Optional<IMECommodityModel> findFirstByCommodityIdOrderByIdDesc(Long commodityId);
}
