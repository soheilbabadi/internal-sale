package com.nicico.internal.sales.goods.repository;

import com.nicico.internal.sales.goods.model.PmsMappingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PmsMappingRepository extends JpaRepository<PmsMappingModel, Long>, JpaSpecificationExecutor<PmsMappingModel> {
}
