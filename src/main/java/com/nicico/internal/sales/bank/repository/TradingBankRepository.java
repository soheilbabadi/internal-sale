package com.nicico.internal.sales.bank.repository;

import com.nicico.internal.sales.bank.model.TradingBankModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TradingBankRepository extends JpaRepository<TradingBankModel, Long>, JpaSpecificationExecutor<TradingBankModel> {
}
