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
    c_issuer_bank_name        VARCHAR2(200)   NOT NULL,

    c_branch_code             VARCHAR2(50),
    c_branch_name             VARCHAR2(200),
    c_payment_city            VARCHAR2(100),
    c_agent_bank_name         VARCHAR2(200),
    C_NOSA_CODE             VARCHAR2(100),
    c_sepam_code              VARCHAR2(100),
    c_treasury_id             VARCHAR2(100),
    d_issue_date              TIMESTAMP,
    d_due_date                TIMESTAMP,
    f_proforma_detail_id      NUMBER(19, 0),
    f_performa_master_id      NUMBER(19, 0),
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
    c_issuer_bank_name        VARCHAR2(200),

    c_branch_code             VARCHAR2(50),
    c_branch_name             VARCHAR2(200),
    c_payment_city            VARCHAR2(100),
    c_agent_bank_name         VARCHAR2(200),
    C_NOSA_CODE             VARCHAR2(100),
    c_sepam_code              VARCHAR2(100),
    c_treasury_id             VARCHAR2(100),
    d_issue_date              TIMESTAMP,
    d_due_date                TIMESTAMP,
    f_proforma_detail_id      NUMBER(19, 0),
    f_performa_master_id      NUMBER(19, 0),
    CONSTRAINT pk_t_ins_proforma_bank_bill_aud PRIMARY KEY (id, rev)
);



