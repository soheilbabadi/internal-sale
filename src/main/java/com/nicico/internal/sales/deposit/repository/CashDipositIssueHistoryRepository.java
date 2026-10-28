package com.nicico.internal.sales.deposit.repository;

import com.nicico.internal.sales.deposit.model.CashDepositIssueHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashDipositIssueHistoryRepository extends JpaRepository<CashDepositIssueHistoryModel, Long>, JpaSpecificationExecutor<CashDepositIssueHistoryModel> {

	List<CashDepositIssueHistoryModel> findByMasterId(Long masterId);
}
