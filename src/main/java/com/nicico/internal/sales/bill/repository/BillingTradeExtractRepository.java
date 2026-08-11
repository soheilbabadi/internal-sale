package com.nicico.internal.sales.bill.repository;

import com.nicico.internal.sales.bill.model.BillingTradeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingTradeExtractRepository extends JpaRepository<BillingTradeExtractModel, Long>, JpaSpecificationExecutor<BillingTradeExtractModel> {


}
