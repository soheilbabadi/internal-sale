package com.nicico.internal.sales.pms.repository;

import com.nicico.internal.sales.pms.model.PMSGoodsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PMSGoodsRepository extends JpaRepository<PMSGoodsModel, Long>, JpaSpecificationExecutor<PMSGoodsModel> {
}
