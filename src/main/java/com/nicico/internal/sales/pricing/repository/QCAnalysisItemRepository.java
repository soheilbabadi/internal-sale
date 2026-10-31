package com.nicico.internal.sales.pricing.repository;

import com.nicico.internal.sales.pricing.model.QCAnalysisItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QCAnalysisItemRepository extends JpaRepository<QCAnalysisItemModel, Long>, JpaSpecificationExecutor<QCAnalysisItemModel> {

	List<QCAnalysisItemModel> findByAnalysisMasterIdOrderByRowNoAsc(Long analysisMasterId);

	void deleteByAnalysisMasterId(Long analysisMasterId);
}
