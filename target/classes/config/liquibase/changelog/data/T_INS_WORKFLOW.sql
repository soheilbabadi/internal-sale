INSERT INTO UAT_IME.T_INS_WORKFLOW (C_PROCESS_TITLE, ID, C_PROCESS_LOCAL_TITLE, N_PROCESS_VERSION, C_TENANT_ID,
                                    C_PROCESS_DEFINITION_KEY, D_CREATED_DATE, D_LAST_MODIFIED_DATE, C_CREATED_BY,
                                    C_LAST_MODIFIED_BY, C_COMMENT, C_DESCRIPTION, N_VERSION)
VALUES ('PREINVOCE', 'a240e-a6088-aeb6d:1:bdd6d90e-7c05-11f0-8e6e-0242ac110018', 'پیش فاکتور داخلی', 2,
        'internal-sales', 'a240e-a6088-aeb6d', TIMESTAMP'2025-05-10 12:12:59.925374', NULL, 'db-saeb', NULL, NULL, NULL,
        1),
       ('LC', 'a5db5-a19dc-a5b68:1:c7c5b9a2-7c05-11f0-8e6e-0242ac110018', 'اعتبارات اسنادی LC', 1, 'internal-sales',
        'a5db5-a19dc-a5b68', TIMESTAMP '2025-05-10 12:12:59.925', NULL, 'db-saeb', NULL, NULL, NULL, 1)
        ,
       ('REVERSAL', 'a9818-ac308-a112b:1:b17ec0aa-7c05-11f0-8e6e-0242ac110018', 'فرایند ابطال پیش فاکتور', 2,
        'internal-sales', 'a9818-ac308-a112b', TIMESTAMP '2025-08-06 11:34:24', NULL, 'db_internal_sales_test', NULL,
        NULL, NULL, 1);
