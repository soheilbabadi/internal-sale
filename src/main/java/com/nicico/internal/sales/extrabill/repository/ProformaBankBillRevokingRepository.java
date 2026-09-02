package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.model.ProformaBankBillReadyRevoking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProformaBankBillRevokingRepository extends JpaRepository<ProformaBankBillReadyRevoking, Long>, JpaSpecificationExecutor<ProformaBankBillReadyRevoking> {


}
