package com.nicico.internal.sales.loading.repository;

import com.nicico.internal.sales.loading.model.LoadingExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingExtractRepository extends JpaRepository<LoadingExtractModel, Long>, JpaSpecificationExecutor<LoadingExtractModel> {

	boolean existsByGoodIdAndLoadingPlaceId(Long goodsId, Long loadingPlaceId);
}
