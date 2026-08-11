package com.nicico.internal.sales.bill.repository;

import com.nicico.internal.sales.bill.model.PmsWeighingDetailExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemittanceWeighingDetailRepository extends JpaRepository<PmsWeighingDetailExtractModel, Long>, JpaSpecificationExecutor<PmsWeighingDetailExtractModel> {

	List<PmsWeighingDetailExtractModel> findAllByRemittanceNo(String remittanceNo);

	List<PmsWeighingDetailExtractModel> findAllByContractNo(String remittanceNo);


}
