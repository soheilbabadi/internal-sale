package com.nicico.internal.sales.trade.repository;

import com.nicico.internal.sales.trade.model.TradeExtractStartProformaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeExtractStartProformaRepository extends JpaRepository<TradeExtractStartProformaModel, Long>, JpaSpecificationExecutor<TradeExtractStartProformaModel> {

	Optional<TradeExtractStartProformaModel> findFirstByPaymentCodeOrderByIdDesc(String paymentCode);
}
