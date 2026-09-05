-- SQL Script to add file ID columns to Oracle database tables
-- Generated for manual execution or migration scripts

-- Add C_PROFORMA_FILE_ID column to T_INS_PERFORMA_DETAIL table
ALTER TABLE T_INS_PERFORMA_DETAIL 
ADD (C_PROFORMA_FILE_ID VARCHAR2(100));

-- Add comment to the new column
COMMENT ON COLUMN T_INS_PERFORMA_DETAIL.C_PROFORMA_FILE_ID IS 'شناسه فایل پروفرما';

-- Add C_REMITTANCE_FILE_ID column to T_INS_REMITTANCE_MASTER table
ALTER TABLE T_INS_REMITTANCE_MASTER 
ADD (C_REMITTANCE_FILE_ID VARCHAR2(100));

-- Add comment to the new column
COMMENT ON COLUMN T_INS_REMITTANCE_MASTER.C_REMITTANCE_FILE_ID IS 'شناسه فایل حواله';

COMMIT;
