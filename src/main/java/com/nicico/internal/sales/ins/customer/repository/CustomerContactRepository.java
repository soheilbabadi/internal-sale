package com.nicico.internal.sales.ins.customer.repository;

import com.nicico.internal.sales.ins.customer.model.CustomerContactModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerContactRepository extends JpaRepository<CustomerContactModel, Long>, JpaSpecificationExecutor<CustomerContactModel> {
	List<CustomerContactModel> findAllByCustomerId(Long customerId);
}
