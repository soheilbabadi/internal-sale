package com.nicico.internal.sales.bank.repository;

import com.nicico.internal.sales.bank.model.BaseBankModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BaseBankRepository extends JpaRepository<BaseBankModel, Long>, JpaSpecificationExecutor<BaseBankModel> {


}
