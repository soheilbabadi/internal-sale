package com.nicico.internal.sales.ime.broker.repository;

import com.nicico.internal.sales.ime.broker.model.IMEBrokerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IMEBrokerRepository extends JpaRepository<IMEBrokerModel, Long>, JpaSpecificationExecutor<IMEBrokerModel> {
	Optional<IMEBrokerModel> findFirstByBrokerId(Integer brokerId);
}
