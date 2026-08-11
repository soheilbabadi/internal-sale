UPDATE T_INS_PERFORMA_MASTER m
SET m.C_SETTLEMENT_TYPE = (SELECT CASE s.SETTLEMENT_TYPE
                                      WHEN 'نقدی' THEN 'CASH'
                                      WHEN 'اعتباری' THEN 'CREDIT'
                                      WHEN 'نقدی/اعتباری' THEN 'CASH_CREDIT'
                                      WHEN 'انفساخ' THEN 'EXHALATION'
                                      WHEN 'نامشخص' THEN 'UNKNOWN'
                                      ELSE 'UNKNOWN'
                                      END
                           FROM TBL_IME_SETTLEMENT s
                           WHERE s.PAYMENT_CODE = m.C_PAYMENT_CODE
                             AND ROWNUM = 1
)
WHERE m.C_SETTLEMENT_TYPE = 'UNKNOWN' AND EXISTS (SELECT 1 FROM TBL_IME_SETTLEMENT s WHERE s.PAYMENT_CODE = m.C_PAYMENT_CODE);


UPDATE T_INS_PERFORMA_DETAIL D
SET D.C_SETTLEMENT_TYPE = (SELECT M.C_SETTLEMENT_TYPE
                           FROM T_INS_PERFORMA_MASTER M
                           WHERE M.ID = D.F_PERFORMA_MASTER_ID
                             AND d.C_SETTLEMENT_TYPE = 'UNKNOWN')
WHERE EXISTS (SELECT 1
              FROM T_INS_PERFORMA_MASTER M
              WHERE M.ID = D.F_PERFORMA_MASTER_ID
                AND m.C_SETTLEMENT_TYPE != 'UNKNOWN');

UPDATE TBL_IME_TRADE
SET SETTLEMENT_TYPE_DESC =
        CASE
            WHEN (PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO) NOT IN
                 (SELECT PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO
                  FROM TBL_IME_SETTLEMENT)
                THEN 'نامشخص'
            ELSE REPLACE(SETTLEMENT_TYPE_DESC, ' ', '')
            END,

    SETTLEMENT_TYPE =
        CASE
            WHEN (PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO) NOT IN
                 (SELECT PAYMENT_CODE, CONTRACT_NO, CONTRACT_DETAIL_NO
                  FROM TBL_IME_SETTLEMENT)
                THEN '255'

            WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'نقدی' THEN '0'
            WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'اعتباری' THEN '1'
            WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'نقدی/اعتباری' THEN '2'
            WHEN REPLACE(SETTLEMENT_TYPE_DESC, ' ', '') = 'انفساخ' THEN '4'
            ELSE SETTLEMENT_TYPE
            END
WHERE SETTLEMENT_DATE >= '1405/01/01';

DELETE FROM TBL_IME_TRADE
WHERE ID NOT IN (
    SELECT MAX(ID)
    FROM TBL_IME_TRADE
    WHERE PAYMENT_CODE IS NOT NULL
    GROUP BY PAYMENT_CODE
)
  AND PAYMENT_CODE IS NOT NULL;

DELETE FROM TBL_IME_TRADE t
WHERE t.ID NOT IN (
    SELECT MAX(t2.ID)
    FROM TBL_IME_TRADE t2
    GROUP BY t2.CONTRACT_NO
);