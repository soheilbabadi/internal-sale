package com.nicico.internal.sales.bank.repository;

import com.nicico.internal.sales.bank.model.BaseBankModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseBankRepository extends JpaRepository<BaseBankModel, Long>, JpaSpecificationExecutor<BaseBankModel> {


}
