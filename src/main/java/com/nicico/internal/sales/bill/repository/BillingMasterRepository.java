package com.nicico.internal.sales.bill.repository;


import com.nicico.internal.sales.bill.model.BillingMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BillingMasterRepository extends JpaRepository<BillingMasterModel, UUID>, JpaSpecificationExecutor<BillingMasterModel> {

}
