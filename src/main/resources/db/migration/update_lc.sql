UPDATE T_INS_PERFORMA_MASTER pm
SET
    pm.N_BROKER_ID = (
        SELECT b.ID
        FROM TBL_IME_TRADE t
                 JOIN TBL_IME_PS_BROKERS b ON t.SELLER_BROKER_CODE = b.ID
        WHERE t.ID = pm.F_TRADE_ID
    ),
    pm.C_BROKER_NAME = (
        SELECT b.PERSIAN_NAME
        FROM TBL_IME_TRADE t
                 JOIN TBL_IME_PS_BROKERS b ON t.SELLER_BROKER_CODE = b.ID
        WHERE t.ID = pm.F_TRADE_ID
    )
WHERE EXISTS (
    SELECT 1
    FROM TBL_IME_TRADE t
             JOIN TBL_IME_PS_BROKERS b ON t.SELLER_BROKER_CODE = b.ID
    WHERE t.ID = pm.F_TRADE_ID
);
UPDATE T_INS_LC lc
SET
    (lc.N_CUSTOMER_ID,
        lc.C_CUSTOMER_NAME,
        lc.N_BROKER_ID,
        lc.C_BROKER_NAME) = (
        SELECT
            pm.N_CUSTOMER_ID,
            pm.C_CUSTOMER_NAME,
            pm.N_BROKER_ID,
            pm.C_BROKER_NAME
        FROM T_INS_PERFORMA_MASTER pm
        WHERE pm.ID = lc.N_PROFORMA_MASTER_ID
    )
WHERE EXISTS (
    SELECT 1
    FROM T_INS_PERFORMA_MASTER pm
    WHERE pm.ID = lc.N_PROFORMA_MASTER_ID
)