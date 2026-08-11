package com.nicico.internal.sales.trade.repository;

import com.nicico.internal.sales.trade.model.TradeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TradeExtractProformaRepository extends JpaRepository<TradeExtractModel, Long>, JpaSpecificationExecutor<TradeExtractModel> {
	Optional<TradeExtractModel> findFirstByPaymentCodeOrderByIdDesc(String paymentCode);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "UPDATE TBL_IME_TRADE SET " +
			"SETTLEMENT_TYPE_DESC = " +
			"  CASE " +
			"    WHEN (PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO) NOT IN " +
			"      (SELECT PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO FROM TBL_IME_SETTLEMENT) " +
			"    THEN 'نامشخص' " +
			"    ELSE REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') " +
			"  END, " +
			"SETTLEMENT_TYPE = " +
			"  CASE " +
			"    WHEN (PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO) NOT IN " +
			"      (SELECT PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO FROM TBL_IME_SETTLEMENT) " +
			"    THEN '255' " +
			"    WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'نقدی' THEN '0' " +
			"    WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'اعتباری' THEN '1' " +
			"    WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'نقدی/اعتباری' THEN '2' " +
			"    WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'انفساخ' THEN '4' " +
			"    ELSE SETTLEMENT_TYPE " +
			"  END " +
			"WHERE D_CREATED_DATE >= TRUNC(SYSDATE - 1) ", nativeQuery = true)
	void updateSettlementType();
}
