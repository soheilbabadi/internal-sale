package com.nicico.internal.sales.bill.repository;

import com.nicico.internal.sales.bill.model.PmsWeighingSummaryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceWeighingSummaryRepository extends JpaRepository<PmsWeighingSummaryModel, Long>, JpaSpecificationExecutor<PmsWeighingSummaryModel> {

	Optional<PmsWeighingSummaryModel> findFirstByContractNo(String contractNo);

	@Query("SELECT r FROM PmsWeighingSummaryModel r " +
			"WHERE r.isFinal = false " +
			"AND r.remittanceQuantity IS NOT NULL " +
			"AND r.weightDifference BETWEEN (r.remittanceQuantity * -0.05) AND (r.remittanceQuantity * 0.05)")
	List<PmsWeighingSummaryModel> findWithinPlusMinusFivePercent();
}
