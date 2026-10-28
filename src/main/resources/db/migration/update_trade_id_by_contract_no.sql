-- =====================================================================
-- Fix T_INS_PERFORMA_MASTER.F_TRADE_ID for a given combined contract no.
--
--   :contract_no  (VARCHAR2, bind variable) - combined contract number,
--   e.g. '123456789' where:
--        CONTRACT_NO         = all characters except the last 3  -> 123456
--        CONTRACT_DETAIL_NO  = the last 3 characters             -> 789
--
--   result1 = current F_TRADE_ID of the proforma master(s) found by
--             N_CONTRACT_NO = :contract_no
--   result2 = TBL_IME_TRADE.ID found by the split contract no / detail no
--
--   then: UPDATE t_ins_performa_master SET F_TRADE_ID = result2
--          WHERE F_TRADE_ID = result1
-- =====================================================================

-- SQL*Plus / SQL Developer bind variable declaration:
VARIABLE contract_no VARCHAR2(50)
EXEC :contract_no := '123456789';   -- TODO: set the real combined contract no

DECLARE
    p_contract_no VARCHAR2(50)  := TRIM(:contract_no);

    v_result1     NUMBER;           -- old F_TRADE_ID (from proforma master)
    v_result2     NUMBER;           -- new trade id (from TBL_IME_TRADE)
    v_main_no     NUMBER;           -- CONTRACT_NO        (input minus last 3 chars)
    v_detail_no   NUMBER;           -- CONTRACT_DETAIL_NO (last 3 chars of input)
    v_updated     NUMBER;
BEGIN
    ------------------------------------------------------------------
    -- 0. Validate / split the combined contract number
    ------------------------------------------------------------------
    IF p_contract_no IS NULL OR LENGTH(p_contract_no) <= 3 THEN
        RAISE_APPLICATION_ERROR(-20001,
            'contract_no must be longer than 3 characters: [' || p_contract_no || ']');
    END IF;

    v_main_no   := TO_NUMBER(SUBSTR(p_contract_no, 1, LENGTH(p_contract_no) - 3));
    v_detail_no := TO_NUMBER(SUBSTR(p_contract_no, -3));

    ------------------------------------------------------------------
    -- 1. result1: current F_TRADE_ID of the proforma master
    --    (MAX() keeps it safe if N_CONTRACT_NO is not unique)
    ------------------------------------------------------------------
    SELECT MAX(tipm.F_TRADE_ID)
      INTO v_result1
      FROM T_INS_PERFORMA_MASTER tipm
     WHERE tipm.N_CONTRACT_NO = TO_NUMBER(p_contract_no);

    IF v_result1 IS NULL THEN
        DBMS_OUTPUT.PUT_LINE('No T_INS_PERFORMA_MASTER row (or NULL F_TRADE_ID) for N_CONTRACT_NO = '
                             || p_contract_no || ' - nothing to update.');
        RETURN;
    END IF;

    ------------------------------------------------------------------
    -- 2. result2: trade id from TBL_IME_TRADE by split contract no.
    --    If several trades match, the most recently created one wins
    --    (same tie-breaker as update_tradeid_proforma.sql).
    ------------------------------------------------------------------
    BEGIN
        SELECT id
          INTO v_result2
          FROM (SELECT tit.ID
                  FROM TBL_IME_TRADE tit
                 WHERE tit.CONTRACT_NO         = v_main_no
                   AND tit.CONTRACT_DETAIL_NO  = v_detail_no
                 ORDER BY tit.D_CREATED_DATE DESC, tit.ID DESC)
         WHERE ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20002,
                'No TBL_IME_TRADE found for CONTRACT_NO = ' || v_main_no ||
                ' / CONTRACT_DETAIL_NO = ' || v_detail_no);
    END;

    ------------------------------------------------------------------
    -- 3. Update every proforma master whose F_TRADE_ID = result1
    ------------------------------------------------------------------
    UPDATE t_ins_performa_master
       SET F_TRADE_ID = v_result2
     WHERE F_TRADE_ID = v_result1;

    v_updated := SQL%ROWCOUNT;

    DBMS_OUTPUT.PUT_LINE('result1 (old F_TRADE_ID)  = ' || v_result1);
    DBMS_OUTPUT.PUT_LINE('result2 (new F_TRADE_ID)  = ' || v_result2);
    DBMS_OUTPUT.PUT_LINE('rows updated              = ' || v_updated);

    -- COMMIT;   -- uncomment to commit directly; otherwise commit manually
END;
/

-- ---------------------------------------------------------------------
-- Verification (run after the block):
-- SELECT ID, N_CONTRACT_NO, F_TRADE_ID
--   FROM T_INS_PERFORMA_MASTER
--  WHERE N_CONTRACT_NO = TO_NUMBER(:contract_no);
--
-- Alternative: pure set-based single statement (no PL/SQL), updates only
-- the masters matched by contract no, guarded by trade existence:
--
-- UPDATE T_INS_PERFORMA_MASTER tipm
--    SET tipm.F_TRADE_ID = (SELECT tit.ID
--                             FROM (SELECT t.ID
--                                     FROM TBL_IME_TRADE t
--                                    WHERE t.CONTRACT_NO        = TO_NUMBER(SUBSTR(:contract_no, 1, LENGTH(:contract_no) - 3))
--                                      AND t.CONTRACT_DETAIL_NO = TO_NUMBER(SUBSTR(:contract_no, -3))
--                                    ORDER BY t.D_CREATED_DATE DESC, t.ID DESC)
--                            WHERE ROWNUM = 1)
--  WHERE tipm.N_CONTRACT_NO = TO_NUMBER(:contract_no)
--    AND tipm.F_TRADE_ID = (SELECT MAX(p2.F_TRADE_ID)
--                             FROM T_INS_PERFORMA_MASTER p2
--                            WHERE p2.N_CONTRACT_NO = TO_NUMBER(:contract_no));
-- ---------------------------------------------------------------------
