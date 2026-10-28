MERGE INTO T_INS_PERFORMA_GOOD_ITEM g
USING (
    SELECT
        g2.ID AS GOOD_ITEM_ID,
        :p_unit_price        AS UNIT_PRICE,
        :p_vat_percent       AS VAT_PERCENT,
        :p_quantity          AS QTY,
        :p_credit_percentage AS CREDIT_PERCENT,
        (100 - :p_credit_percentage) AS CASH_PERCENT,
        NVL(m.N_COMMISSION_PERCENTAGE, 0) AS COMMISSION_PERCENT
    FROM T_INS_PERFORMA_GOOD_ITEM g2
             JOIN T_INS_PERFORMA_DETAIL d ON d.ID = g2.F_PERFORMA_DETAIL_ID
             JOIN T_INS_PERFORMA_MASTER m ON m.ID = d.F_PERFORMA_MASTER_ID
    WHERE d.C_PERFORMA_NO = :p_performa_no
      AND g2.ID = :g_id
) x
ON (g.ID = x.GOOD_ITEM_ID)
WHEN MATCHED THEN UPDATE SET
                             g.N_UNIT_PRICE        = x.UNIT_PRICE,
                             g.N_UNIT_PRICE_CASH   = x.UNIT_PRICE,
                             g.N_VAT_PERCENT       = x.VAT_PERCENT,
                             g.N_QUANTITY          = x.QTY,
                             g.N_CREDIT_PERCENTAGE = x.CREDIT_PERCENT,

                             g.N_UNIT_PRICE_CREDIT = CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)),

                             g.N_CREDIT_QUANTITY = ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3),

                             g.N_CREDIT_AMOUNT = ROUND(
                                     ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3) *
                                     CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)),
                                     0
                                                 ),

                             g.N_CASH_AMOUNT = ROUND(
                                     (x.QTY - ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3)) * x.UNIT_PRICE,
                                     0
                                               ),

                             g.N_VAT_CREDIT_AMOUNT = ROUND(
                                     ROUND(
                                             ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3) *
                                             CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)),
                                             0
                                     ) * x.VAT_PERCENT / 100,
                                     0
                                                     ),

                             g.N_VAT_CASH_AMOUNT = ROUND(
                                     ROUND(
                                             (x.QTY - ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3)) * x.UNIT_PRICE,
                                             0
                                     ) * x.VAT_PERCENT / 100,
                                     0
                                                   ),

                             g.N_TOTAL_AMOUNT =
                                 ROUND((x.QTY - ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3)) * x.UNIT_PRICE, 0) +
                                 ROUND(ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3) * CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)), 0),

                             g.N_VAT_AMOUNT =
                                 ROUND(ROUND((x.QTY - ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3)) * x.UNIT_PRICE, 0) * x.VAT_PERCENT / 100, 0) +
                                 ROUND(ROUND(ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3) * CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)), 0) * x.VAT_PERCENT / 100, 0),

                             g.N_FINAL_AMOUNT =
                                 (
                                     ROUND((x.QTY - ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3)) * x.UNIT_PRICE, 0) +
                                     ROUND(ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3) * CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)), 0)
                                     ) +
                                 (
                                     ROUND(ROUND((x.QTY - ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3)) * x.UNIT_PRICE, 0) * x.VAT_PERCENT / 100, 0) +
                                     ROUND(ROUND(ROUND(x.QTY * x.CREDIT_PERCENT / 100, 3) * CEIL(x.UNIT_PRICE * ((x.COMMISSION_PERCENT + 100) / 100)), 0) * x.VAT_PERCENT / 100, 0)
                                     );
MERGE INTO T_INS_PERFORMA_DETAIL d
USING (
    SELECT
        d2.ID AS DETAIL_ID,
        NVL(SUM(NVL(g.N_TOTAL_AMOUNT, 0)), 0) AS SUM_TOTAL_AMOUNT,
        NVL(SUM(NVL(g.N_VAT_AMOUNT, 0)), 0) AS SUM_VAT_AMOUNT,
        NVL(SUM(NVL(g.N_FINAL_AMOUNT, 0)), 0) AS SUM_FINAL_AMOUNT,
        NVL(SUM(NVL(g.N_DEPOSIT_SLIP_WITH_TAX, 0)), 0) AS SUM_DEPOSIT_WITH_TAX,
        NVL(SUM(NVL(g.N_DEPOSIT_SLIP_WITHOUT_TAX, 0)), 0) AS SUM_DEPOSIT_WITHOUT_TAX
    FROM T_INS_PERFORMA_DETAIL d2
             LEFT JOIN T_INS_PERFORMA_GOOD_ITEM g
                       ON g.F_PERFORMA_DETAIL_ID = d2.ID
    WHERE d2.ID = :p_detail_id
    GROUP BY d2.ID
) x
ON (d.ID = x.DETAIL_ID)
WHEN MATCHED THEN UPDATE SET
                             d.N_TOTAL_AMOUNT = ROUND(x.SUM_TOTAL_AMOUNT, 0),
                             d.N_VAT_AMOUNT = ROUND(x.SUM_VAT_AMOUNT, 0),
                             d.N_FINAL_PRICE = ROUND(x.SUM_FINAL_AMOUNT, 0),
                             d.N_DEPOSIT_SLIP_WITH_TAX = ROUND(x.SUM_DEPOSIT_WITH_TAX, 0),
                             d.N_DEPOSIT_SLIP_WITHOUT_TAX = ROUND(x.SUM_DEPOSIT_WITHOUT_TAX, 0);

MERGE INTO T_INS_PERFORMA_MASTER m
USING (
    SELECT
        d.F_PERFORMA_MASTER_ID AS MASTER_ID,
        NVL(SUM(NVL(d.N_TOTAL_AMOUNT, 0)), 0) AS SUM_TOTAL_AMOUNT,
        NVL(SUM(NVL(d.N_VAT_AMOUNT, 0)), 0) AS SUM_VAT_AMOUNT,
        NVL(SUM(NVL(d.N_FINAL_PRICE, 0)), 0) AS SUM_FINAL_AMOUNT,
        NVL(SUM(NVL(gs.SUM_CASH_AMOUNT, 0)), 0) AS SUM_CASH_AMOUNT,
        NVL(SUM(NVL(gs.SUM_CREDIT_AMOUNT, 0)), 0) AS SUM_CREDIT_AMOUNT,
        NVL(SUM(NVL(gs.SUM_QUANTITY, 0)), 0) AS SUM_QUANTITY
    FROM T_INS_PERFORMA_DETAIL d
             LEFT JOIN (
        SELECT
            g.F_PERFORMA_DETAIL_ID AS DETAIL_ID,
            NVL(SUM(NVL(g.N_CASH_AMOUNT, 0)), 0) AS SUM_CASH_AMOUNT,
            NVL(SUM(NVL(g.N_CREDIT_AMOUNT, 0)), 0) AS SUM_CREDIT_AMOUNT,
            NVL(SUM(NVL(g.N_QUANTITY, 0)), 0) AS SUM_QUANTITY
        FROM T_INS_PERFORMA_GOOD_ITEM g
        GROUP BY g.F_PERFORMA_DETAIL_ID
    ) gs ON gs.DETAIL_ID = d.ID
    WHERE d.C_PERFORMA_NO = :p_performa_no
    GROUP BY d.F_PERFORMA_MASTER_ID
) x
ON (m.ID = x.MASTER_ID)
WHEN MATCHED THEN UPDATE SET
                             m.N_TOTAL_VAT_AMOUNT   = ROUND(x.SUM_VAT_AMOUNT, 0),
                             m.N_TOTAL_FINAL_AMOUNT = ROUND(x.SUM_FINAL_AMOUNT, 0),
                             m.N_TOTAL_CASH_AMOUNT  = ROUND(x.SUM_CASH_AMOUNT, 0),
                             m.N_TOTAL_CREDIT_AMOUNT= ROUND(x.SUM_CREDIT_AMOUNT, 0),
                             m.N_TOTAL_QUANTITY     = ROUND(x.SUM_QUANTITY, 0);