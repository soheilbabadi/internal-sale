package com.nicico.internal.sales.deposit.repository;

import com.nicico.internal.sales.deposit.model.CashDepositReadyReckoningModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashDepositReadyRevokingRepository extends JpaRepository<CashDepositReadyReckoningModel, Long>, JpaSpecificationExecutor<CashDepositReadyReckoningModel> {

	List<CashDepositReadyReckoningModel> findByMasterId(Long masterId);
}
