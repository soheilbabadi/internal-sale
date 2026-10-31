-- =====================================================================
-- Fix proforma rows created/edited with EXTRA_BILL_OF_EXCHANGE but with
-- extraBillOfPercent left at 0 (e.g. created by the CASH flow and then
-- changed to EXTRA_BILL_OF_EXCHANGE without recalculation).
--
-- Formula extracted from code (ProformaModelHelper / ExportDocServiceImpl /
-- ExtraBillProformaIssueServiceImpl.calculateExtraBillContext):
--
--   extraAmount = ROUND(totalAmount * percent / 100, 0)
--   finalPrice  = totalAmount + extraAmount = totalAmount * (1 + p/100)
--   VAT (N_VAT_AMOUNT) is unchanged: it is always calculated on totalAmount only
--   master.totalFinalAmount = SUM(detail.finalPrice)
--
-- :p_performa_no  -> C_PERFORMA_NO of the affected detail (e.g. '140502131')
-- :p_percent      -> extra bill percent from sale condition (e.g. 5)
-- =====================================================================

-- 1) Detail: recalculate extra amount and final price
MERGE INTO T_INS_PERFORMA_DETAIL d
USING (
    SELECT
        d2.ID AS DETAIL_ID,
        NVL(SUM(NVL(g.N_TOTAL_AMOUNT, 0)), 0) AS BASE_AMOUNT
    FROM T_INS_PERFORMA_DETAIL d2
             LEFT JOIN T_INS_PERFORMA_GOOD_ITEM g
                       ON g.F_PERFORMA_DETAIL_ID = d2.ID
    WHERE d2.C_PERFORMA_NO = :p_performa_no
    GROUP BY d2.ID
) x
ON (d.ID = x.DETAIL_ID)
WHEN MATCHED THEN UPDATE SET
                         d.N_EXTRA_BILL_OF_PERCENT = :p_percent,
                         d.N_EXTRA_BILL_OF_AMOUNT  = ROUND(x.BASE_AMOUNT * :p_percent / 100, 0),
                         d.N_FINAL_PRICE           = x.BASE_AMOUNT + ROUND(x.BASE_AMOUNT * :p_percent / 100, 0);

-- 2) Master: roll up recalculated detail final prices
MERGE INTO T_INS_PERFORMA_MASTER m
USING (
    SELECT
        d.F_PERFORMA_MASTER_ID AS MASTER_ID,
        NVL(SUM(NVL(d.N_FINAL_PRICE, 0)), 0) AS SUM_FINAL_AMOUNT
    FROM T_INS_PERFORMA_DETAIL d
    WHERE d.C_PERFORMA_NO = :p_performa_no
    GROUP BY d.F_PERFORMA_MASTER_ID
) x
ON (m.ID = x.MASTER_ID)
WHEN MATCHED THEN UPDATE SET
                         m.N_TOTAL_FINAL_AMOUNT = ROUND(x.SUM_FINAL_AMOUNT, 0);

COMMIT;
