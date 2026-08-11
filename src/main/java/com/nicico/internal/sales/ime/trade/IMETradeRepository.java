package com.nicico.internal.sales.ime.trade;

import com.nicico.internal.sales.goods.dto.CommodityProjection;
import com.nicico.internal.sales.ins.customer.dto.BuyerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMETradeRepository extends JpaRepository<IMETradeModel, Long>, JpaSpecificationExecutor<IMETradeModel> {
	Optional<IMETradeModel> findFirstByPaymentCodeOrderByIdDesc(String paymentCode);

	Optional<IMETradeModel> findFirstByContractNoAndPaymentCodeOrderByIdDesc(Integer contractNo, String paymentCode);

	@Query(value = "SELECT DISTINCT BUYER_NATIONAL_CODE as buyerNationalCode, BUYER_NAME as buyerName " + "FROM TBL_IME_TRADE tit " + "WHERE BUYER_NATIONAL_CODE IS NOT NULL " + "AND BUYER_NATIONAL_CODE NOT IN (SELECT tic.C_NATIONAL_CODE FROM T_INS_CUSTOMER tic)", nativeQuery = true)
	List<BuyerProjection> findDistinctBuyersNotInCustomers();

	Optional<IMETradeModel> findFirstByIdOrderByIdDesc(Long id);

	@Query(value = "SELECT DISTINCT tit.COMMODITY_CODE as commodityCode, " + "       c.PERSIAN_NAME as persianName, " + "       c.SYMBOL as symbol " + "FROM TBL_IME_TRADE tit " + "INNER JOIN TBL_IME_PS_COMMODITIES c ON c.ID = tit.COMMODITY_CODE " + "WHERE tit.COMMODITY_CODE NOT IN (SELECT DISTINCT tig.N_IME_COMMODITY_ID FROM T_INS_GOODS tig) " + "  AND tit.CONTRACT_DATE > '1405/01/01'", nativeQuery = true)
	List<CommodityProjection> findDistinctCommoditiesNotInGoods();


	@Query(value = """
			SELECT tit.SELLER_BROKER_CODE FROM tbl_ime_trade tit INNER JOIN TBL_IME_PS_BROKERS broker
			    ON tit.BUYER_BROKER_CODE = broker.ID  WHERE tit.ID = :tradeId FETCH FIRST 1 ROW ONLY
			""", nativeQuery = true)
	Optional<Long> findSellerBrokerCodeById(@Param("tradeId") Long tradeId);
}
