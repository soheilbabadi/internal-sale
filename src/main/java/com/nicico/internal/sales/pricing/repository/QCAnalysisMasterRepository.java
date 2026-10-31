package com.nicico.internal.sales.pricing.repository;

import com.nicico.internal.sales.pricing.enums.QCAnalysisStage;
import com.nicico.internal.sales.pricing.model.QCAnalysisMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QCAnalysisMasterRepository extends JpaRepository<QCAnalysisMasterModel, Long>, JpaSpecificationExecutor<QCAnalysisMasterModel> {

	boolean existsByCommodityIdAndQcIndicatorNo(Long commodityId, String qcIndicatorNo);

	boolean existsByCommodityIdAndQcIndicatorNoAndIdNot(Long commodityId, String qcIndicatorNo, Long id);

	boolean existsByParentAnalysisId(Long parentAnalysisId);

	List<QCAnalysisMasterModel> findByCommodityIdAndAnalysisStageOrderByIdDesc(Long commodityId, QCAnalysisStage analysisStage);

	Optional<QCAnalysisMasterModel> findByQcIndicatorNo(String qcIndicatorNo);
}
