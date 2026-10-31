-- =====================================================================
-- Extra bill percent 0 -> 5 for contract 1405361893001
-- Values taken from the proforma JSON response:
--   master id          = 3902
--   detail id          = 4772  (performaNo '140502131',
--                               EXTRA_BILL_OF_EXCHANGE, EDITED)
--   good item id       = 3800
--   item totalAmount   = 2,133,861,096,000
--
--   extra (5%)         = 2,133,861,096,000 * 5 / 100 = 106,693,054,800
--   finalAmount        = 2,133,861,096,000 + 106,693,054,800
--                      = 2,240,554,150,800
--
-- Detail 4771 ('140502094', LETTER_OF_CREDIT_OPENING, CANCELED)
-- and its good item 3799 are intentionally NOT touched.
-- =====================================================================

-- 1) Good item: apply 5% to final amount
--    3800: N_FINAL_AMOUNT 2,347,247,205,600 -> 2,240,554,150,800
UPDATE T_INS_PERFORMA_GOOD_ITEM
SET N_FINAL_AMOUNT = N_TOTAL_AMOUNT + ROUND(N_TOTAL_AMOUNT * 5 / 100, 0)
WHERE ID = 3800;

-- 2) Detail 4772: percent, extra amount and final price
UPDATE T_INS_PERFORMA_DETAIL
SET N_EXTRA_BILL_OF_PERCENT = 5,
    N_EXTRA_BILL_OF_AMOUNT  = 106693054800,
    N_FINAL_PRICE           = 2240554150800
WHERE ID = 4772;

-- 3) Master 3902: total final amount = SUM(detail.finalPrice) of active details
--    (only 4772 is EXTRA_BILL_OF_EXCHANGE; 4771 is CANCELED)
UPDATE T_INS_PERFORMA_MASTER
SET N_TOTAL_FINAL_AMOUNT = 2240554150800
WHERE ID = 3902;

COMMIT;
