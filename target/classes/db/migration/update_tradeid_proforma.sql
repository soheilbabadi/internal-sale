UPDATE T_INS_PERFORMA_MASTER p
SET p.F_TRADE_ID =
        (SELECT id
         FROM (SELECT t.ID,
                      ROW_NUMBER() OVER (
                          ORDER BY t.D_CREATED_DATE DESC
                          ) rn
               FROM TBL_IME_TRADE t
               WHERE t.PAYMENT_CODE = p.C_PAYMENT_CODE
                 AND t.CONTRACT_DATE = p.C_CONTRACT_DATE)
         WHERE rn = 1)
WHERE EXISTS (SELECT 1
              FROM TBL_IME_TRADE t
              WHERE t.PAYMENT_CODE = p.C_PAYMENT_CODE
                AND t.CONTRACT_DATE = p.C_CONTRACT_DATE);
