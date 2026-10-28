package com.nicico.internal.sales.deposit.repository;

import com.nicico.internal.sales.deposit.model.CashDepositModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashDepositRepository extends JpaRepository<CashDepositModel, Long>, JpaSpecificationExecutor<CashDepositModel> {

	List<CashDepositModel> findAllByProformaMasterId(Long masterId);
	Optional<CashDepositModel> findByTradeId(Long tradeId);
	Optional<CashDepositModel> findByProformaDetailId(Long detailId);

}
