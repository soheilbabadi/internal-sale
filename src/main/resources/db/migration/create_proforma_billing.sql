CREATE SEQUENCE SEQ_INS_PROFORMA_BANK_BILL
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE t_ins_proforma_bank_bill
(
    id                        NUMBER(19, 0)   NOT NULL,
    d_created_date            TIMESTAMP       NOT NULL,
    d_last_modified_date      TIMESTAMP,
    c_created_by              VARCHAR2(250)   NOT NULL,
    c_last_modified_by        VARCHAR2(250),
    c_comment                 VARCHAR2(4000),
    c_description             VARCHAR2(4000),
    n_version                 NUMBER(10, 0)   NOT NULL,
    N_ISSUER_BANK_ID          NUMBER(19, 0),
    c_issuer_bank_name        VARCHAR2(200)   NOT NULL,

    c_branch_code             VARCHAR2(50),
    c_branch_name             VARCHAR2(200),
    c_payment_city            VARCHAR2(100),
    c_agent_bank_name         VARCHAR2(200),
    N_AGENT_BANK_ID           NUMBER(19, 0),
    C_EXTRA_BILL_FILE_ID      VARCHAR2(100),
    C_DISPATCH_FILE_ID        VARCHAR2(100),
    C_NOSA_CODE               VARCHAR2(100),
    c_sepam_code              VARCHAR2(100),
    c_treasury_id             VARCHAR2(100),
    d_issue_date              TIMESTAMP,
    d_due_date                TIMESTAMP,
    f_proforma_detail_id      NUMBER(19, 0),
    f_performa_master_id      NUMBER(19, 0),
    C_WORKFLOW_APPROVE_STATUS VARCHAR2(100),
    C_REVERSAL_PROCESS_ID     VARCHAR2(50),
    C_PROCESS_ID              VARCHAR2(50)    NOT NULL,
    N_CONTRACT_NO             NUMBER(19, 0)   NOT NULL,
    F_TRADE_ID                NUMBER(19, 0)   NOT NULL,
    C_ACKNOWLEDGMENT          VARCHAR2(50)    DEFAULT 'UNKNOWN' NOT NULL,
    IS_RECKONING_SEND         NUMBER(1)       DEFAULT 0 NOT NULL,
    D_RECKONING_SEND_DATE     TIMESTAMP,
    C_CANCELLATION_REASON     VARCHAR2(50),
    D_CANCEL_DATE             TIMESTAMP,
    C_PMS_BILL_ID             VARCHAR2(50),
    CONSTRAINT pk_t_ins_proforma_bank_bill PRIMARY KEY (id)
);

-- 3. Envers audit table
CREATE TABLE t_ins_proforma_bank_bill_aud
(
    rev                       NUMBER(19, 0)   NOT NULL,
    d_created_date            TIMESTAMP,
    d_last_modified_date      TIMESTAMP,
    c_created_by              VARCHAR2(250),
    c_last_modified_by        VARCHAR2(250),
    c_comment                 VARCHAR2(4000),
    c_description             VARCHAR2(4000),
    revtype                   NUMBER(3, 0),
    id                        NUMBER(19, 0)   NOT NULL,
    N_ISSUER_BANK_ID          NUMBER(19, 0),
    c_issuer_bank_name        VARCHAR2(200),

    c_branch_code             VARCHAR2(50),
    c_branch_name             VARCHAR2(200),
    c_payment_city            VARCHAR2(100),
    c_agent_bank_name         VARCHAR2(200),
    N_AGENT_BANK_ID           NUMBER(19, 0),
    C_EXTRA_BILL_FILE_ID      VARCHAR2(100),
    C_DISPATCH_FILE_ID        VARCHAR2(100),
    C_NOSA_CODE               VARCHAR2(100),
    c_sepam_code              VARCHAR2(100),
    c_treasury_id             VARCHAR2(100),
    d_issue_date              TIMESTAMP,
    d_due_date                TIMESTAMP,
    f_proforma_detail_id      NUMBER(19, 0),
    f_performa_master_id      NUMBER(19, 0),
    C_WORKFLOW_APPROVE_STATUS VARCHAR2(100),
    C_REVERSAL_PROCESS_ID     VARCHAR2(50),
    C_PROCESS_ID              VARCHAR2(50),
    N_CONTRACT_NO             NUMBER(19, 0),
    F_TRADE_ID                NUMBER(19, 0),
    C_ACKNOWLEDGMENT          VARCHAR2(50),
    IS_RECKONING_SEND         NUMBER(1),
    D_RECKONING_SEND_DATE     TIMESTAMP,
    C_CANCELLATION_REASON     VARCHAR2(50),
    D_CANCEL_DATE             TIMESTAMP,
    C_PMS_BILL_ID             VARCHAR2(50),
    CONSTRAINT pk_t_ins_proforma_bank_bill_aud PRIMARY KEY (id, rev)
);



