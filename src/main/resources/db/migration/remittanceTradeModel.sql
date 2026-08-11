
SELECT
    id,
    C_PAYMENT_CODE,
    N_CUSTOMER_ID,
    C_CUSTOMER_NAME,
    C_ECONOMIC_CODE,
    C_NATIONAL_CODE,
    N_GOOD_ID,
    C_GOOD_NAME,
    C_IME_COMMODITY_SYMBOL,
    N_IME_COMMODITY_ID,
    C_CONTRACT_DATE,
    C_CONTRACT_NO,
    N_BUYER_BROKER_ID,
    C_BUYER_BROKER_NAME,
    N_SELLER_BROKER_ID,
    C_SELLER_BROKER_NAME,
    N_UNIT_COUNT,
    N_UNIT_PRICE,
    N_CASH_PERCENTAGE,
    N_CREDIT_PERCENTAGE,
    N_COMMISSION,
    N_CREDIT_UNIT_PRICE,
    N_CREDIT_AMOUNT,
    N_CASH_AMOUNT,
    N_FINAL_AMOUNT,
    C_SETTLEMENT_TYPE,
    C_SETTLEMENT_TYPE_DESC,
    C_CONTRACT_TYPE_CODE,
    C_CONTRACT_TYPE_DESCRIPTION,
    SETTLEMENT_DATE,
    DELIVERY_DATE,
    IS_DELAY_PENALTY
FROM (
         SELECT
             tit.id,
             tit.PAYMENT_CODE                                       AS C_PAYMENT_CODE,
             tic.ID                                                 AS N_CUSTOMER_ID,
             tit.BUYER_NAME                                         AS C_CUSTOMER_NAME,
             tic.C_ECONOMIC_CODE                                    AS C_ECONOMIC_CODE,
             tit.BUYER_NATIONAL_CODE                                AS C_NATIONAL_CODE,
             tig.ID                                                 AS N_GOOD_ID,
             tig.C_NAME                                             AS C_GOOD_NAME,
             tit.COMMODITY_CODE                                     AS N_IME_COMMODITY_ID,
             tig.C_IME_COMMODITY_SYMBOL,
             tit.CONTRACT_DATE                                      AS C_CONTRACT_DATE,
             tit.CONTRACT_NO || '00' || tit.CONTRACT_DETAIL_NO      AS C_CONTRACT_NO,
             tit.BUYER_BROKER_CODE                                  AS N_BUYER_BROKER_ID,
             buyerBroker.PERSIAN_NAME                               AS C_BUYER_BROKER_NAME,
             tit.SELLER_BROKER_CODE                                 AS N_SELLER_BROKER_ID,
             sellerBroker.PERSIAN_NAME                              AS C_SELLER_BROKER_NAME,
             tit.UNIT_COUNT * 1000                                  AS N_UNIT_COUNT,
             tit.UNIT_PRICE                                         AS N_UNIT_PRICE,
             COALESCE(tigb.N_CASH_PERCENTAGE, 0)                    AS N_CASH_PERCENTAGE,
             COALESCE(100 - tigb.N_CASH_PERCENTAGE, 0)              AS N_CREDIT_PERCENTAGE,
             COALESCE(tigb.N_COMMISSION, 0)                         AS N_COMMISSION,
             tit.UNIT_PRICE * (1 + tigb.N_COMMISSION)               AS N_CREDIT_UNIT_PRICE,
             COALESCE((100 - tigb.N_CASH_PERCENTAGE) / 100.0 *
                      (tit.UNIT_COUNT * 1000) *
                      ((tigb.N_COMMISSION + 100.0) / 100.0) *
                      tit.UNIT_PRICE, 0)                            AS N_CREDIT_AMOUNT,
             COALESCE(tigb.N_CASH_PERCENTAGE / 100.0 *
                      (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE, 0)  AS N_CASH_AMOUNT,
             COALESCE(
                     (100 - tigb.N_CASH_PERCENTAGE) / 100.0 *
                     (tit.UNIT_COUNT * 1000) *
                     ((tigb.N_COMMISSION + 100.0) / 100.0) *
                     tit.UNIT_PRICE +
                     (tigb.N_CASH_PERCENTAGE / 100.0 *
                      (tit.UNIT_COUNT * 1000) * tit.UNIT_PRICE), 0) AS N_FINAL_AMOUNT,
             tit.SETTLEMENT_TYPE_DESC                                    AS C_SETTLEMENT_TYPE,
             tit.SETTLEMENT_TYPE_DESC                               AS C_SETTLEMENT_TYPE_DESC,
             tit.CONTRACT_TYPE_CODE                                 AS C_CONTRACT_TYPE_CODE,
             tipct.DESCRIPTION                                      AS C_CONTRACT_TYPE_DESCRIPTION,
             tis.SETTLEMENT_DATE,
             tit.DELIVERY_DATE,
             tis.IS_DELAY_PENALTY
         FROM TBL_IME_TRADE tit
                  INNER JOIN T_INS_GOODS tig ON tit.COMMODITY_CODE = tig.N_IME_COMMODITY_ID
                  INNER JOIN TBL_IME_PS_CONTRACT_TYPES tipct ON tit.CONTRACT_TYPE_CODE = tipct.ID
                  INNER JOIN TBL_IME_SETTLEMENT tis ON tis.PAYMENT_CODE = tit.PAYMENT_CODE AND tis.CONTRACT_NO =tit.CONTRACT_NO  AND tis.CONTRACT_DETAIL_NO =tit.CONTRACT_DETAIL_NO
                  INNER JOIN T_INS_CUSTOMER tic ON tic.C_NATIONAL_CODE = tit.BUYER_NATIONAL_CODE
                  INNER JOIN T_INS_GOODS_BUCKET tigb ON tig.ID = tigb.N_GOOD_ID
                  LEFT JOIN TBL_IME_PS_BROKERS sellerBroker ON sellerBroker.ID = tit.SELLER_BROKER_CODE
                  LEFT JOIN TBL_IME_PS_BROKERS buyerBroker ON buyerBroker.ID = tit.BUYER_BROKER_CODE
         WHERE TIT.CONTRACT_DATE > '1405/01/01' AND TIT.CURRENCY_CODE = 1
           AND (tit.PAYMENT_CODE, tit.CONTRACT_NO, tit.CONTRACT_DETAIL_NO)  IN (SELECT PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO  FROM TBL_IME_SETTLEMENT)
     )
ORDER BY C_CONTRACT_DATE DESC