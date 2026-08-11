package com.nicico.internal.sales.loading.repository;

import com.nicico.internal.sales.loading.model.LoadingGoodsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingGoodsRepository extends JpaRepository<LoadingGoodsModel, Long>, JpaSpecificationExecutor<LoadingGoodsModel> {

	java.util.Optional<LoadingGoodsModel> findByGoodIdAndLoadingPlaceId(Long goodsId, Long loadingPlaceId);
}
