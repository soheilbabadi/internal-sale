package com.nicico.internal.sales.bill.repository;


import com.nicico.internal.sales.bill.model.BillingWeightingDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BillingWeightingDetailRepository extends JpaRepository<BillingWeightingDetailModel, UUID>, JpaSpecificationExecutor<BillingWeightingDetailModel> {

}
