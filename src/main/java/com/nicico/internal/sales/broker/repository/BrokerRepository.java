package com.nicico.internal.sales.broker.repository;

import com.nicico.internal.sales.broker.model.BrokerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BrokerRepository extends JpaRepository<BrokerModel, Long>, JpaSpecificationExecutor<BrokerModel> {

}
