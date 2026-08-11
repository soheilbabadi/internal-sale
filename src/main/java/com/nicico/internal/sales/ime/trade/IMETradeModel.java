package com.nicico.internal.sales.ime.trade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
		        SELECT *
		FROM (
		    SELECT q.*,
		           ROW_NUMBER() OVER (PARTITION BY q.ID ORDER BY q.ID) rn
		    FROM (
		        SELECT
		            trade.*,
		            commodity.persian_name       AS commodity_persian_name,
		            seller_broker.persian_name   AS seller_broker_persian_name,
		            commodity.SYMBOL  AS commodity_symbol,
		            buyer_broker.persian_name    AS buyer_broker_persian_name,
		            offer.DESCRIPTION            AS offer_description,
		            trade_settlement.SETTLEMENT_DATE AS real_settelment_date
		        FROM vw_ime_trade trade
		        LEFT JOIN TBL_IME_PS_COMMODITIES commodity
		               ON trade.commodity_code = commodity.id
		        LEFT JOIN TBL_IME_PS_BROKERS seller_broker
		               ON trade.seller_broker_code = seller_broker.id
		        LEFT JOIN TBL_IME_PS_BROKERS buyer_broker
		               ON trade.buyer_broker_code = buyer_broker.id
		        LEFT JOIN TBL_IME_PS_OFFERS offer
		               ON trade.offer_code = offer.id
		        LEFT JOIN (
		            SELECT CONTRACT_NO,
		                   CONTRACT_DETAIL_NO,
		                   MAX(SETTLEMENT_DATE) AS SETTLEMENT_DATE
		            FROM VW_IME_TRADE_SETTLEMENT
		            GROUP BY CONTRACT_NO, CONTRACT_DETAIL_NO
		        ) trade_settlement
		        ON trade.CONTRACT_NO = trade_settlement.CONTRACT_NO
		        AND trade.CONTRACT_DETAIL_NO = trade_settlement.CONTRACT_DETAIL_NO
		
		    ) q
		)
		WHERE rn = 1
		""")
public class IMETradeModel implements Serializable {
	@Serial
	private static final long serialVersionUID = -1276901316040237687L;
	@Id
	private Long id;
	@Column(name = "broker_buy_wage")
	private Float brokerBuyWage;
	@Column(name = "broker_sell_wage")
	private Float brokerSellWage;
	@Column(name = "buyer_broker_code")
	private Integer buyerBrokerCode;
	@Column(name = "buyer_name")
	private String buyerName;
	@Column(name = "commodity_persian_name")
	private String commodityPersianName;
	@Column(name = "buyer_national_code")
	private String buyerNationalCode;
	@Column(name = "commodity_code")
	private Integer commodityCode;
	@Column(name = "contract_date", length = 10)
	private String contractDate;
	@Column(name = "contract_detail_no")
	private Integer contractDetailNo;
	@Column(name = "contract_no")
	private Integer contractNo;
	@Column(name = "contract_type_code")
	private Integer contractTypeCode;
	@Column(name = "currency_code")
	private Integer currencyCode;
	@Column(name = "delivery_date", length = 10)
	private String deliveryDate;
	@Column(name = "delivery_place_desc")
	private String deliveryPlaceDesc;
	@Column(name = "ime_buy_wage")
	private Float imeBuyWage;
	@Column(name = "ime_sell_wage")
	private Float imeSellWage;
	@Column(name = "is_canceled")
	private Boolean isCanceled;
	@Column(name = "is_delay_penalty")
	private Boolean isDelayPenalty;
	@Column(name = "listing_fee")
	private Float listingFee;
	@Column(name = "manfacturer_name")
	private String manufacturerName;
	@Column(name = "measure_unit_code")
	private Integer measureUnitCode;
	@Column(name = "offer_code")
	private Integer offerCode;
	@Column(name = "payment_code", length = 100)
	private String paymentCode;
	@Column(name = "seller_broker_code")
	private Integer sellerBrokerCode;
	@Column(name = "seller_broker_persian_name")
	private String sellerBrokerPersianName;
	@Column(name = "buyer_broker_persian_name")
	private String buyerBrokerPersianName;
	@Column(name = "seo_buy_wage")
	private Float seoBuyWage;
	@Column(name = "seo_sell_wage")
	private Float seoSellWage;
	@Column(name = "settlement_date", length = 10)
	private String settlementDate;
	@Column(name = "real_settelment_date", length = 10)
	private String realSettlementDate;
	@Column(name = "settlement_deadline", length = 10)
	private String settlementDeadline;
	@Column(name = "settlement_type")
	private String settlementType;
	@Column(name = "settlement_type_desc", length = 1000)
	private String settlementTypeDesc;
	@Column(name = "supplier_code")
	private Integer supplierCode;
	@Column(name = "total_price")
	private Float totalPrice;
	@Column(name = "unit_count")
	private Integer unitCount;
	@Column(name = "unit_price")
	private Double unitPrice;
	@Column(name = "buyer_code")
	private Integer buyerCode;
	@Column(name = "RESPONSE_DATE")
	private Date responseDate;
	@Column(name = "MESSAGE_TEXT")
	private String messageText;
	@Column(name = "offer_description")
	private String offerDescription;

	@Column(name = "buyer_customer_type")
	private Integer buyerCustomerType;
	@Column(name = "buyer_id")
	private Integer buyerId;

	@Column(name = "commodity_symbol")
	private String commoditySymbol;

}
