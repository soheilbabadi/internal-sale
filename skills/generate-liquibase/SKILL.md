---
name: generate-liquibase
description: Given an entity (new or changed), generate a Liquibase changeset XML in this project and register it in master.xml. Trigger phrases: "generate liquibase", "create changeset", "db migration for", "schema change".
---

# Generate Liquibase Changeset (internal-sales)

**Constraint: only write XML files. Never run Liquibase / mvn / the app.**

## Locations
- Master changelog: `src/main/resources/config/liquibase/master.xml` (configured via `spring.liquibase.change-log: classpath:config/liquibase/master.xml`, plus `database-change-log-table: Z_LIQ_CHANGELOG`, contexts = active profile — see `src/main/resources/config/application.yml`).
- New changesets go in `src/main/resources/config/liquibase/changelog/ins/` (this project's prefix dir; other dirs `ime/`, `pms/`, `data/` are legacy/integration-specific).
- File naming: descriptive snake_case, either `create_t_ins_<name>.xml`, `create_<name>_table.xml`, `<Name>_create_table.xml`, or `add_<cols>_to_<table>.xml` (see existing files in `changelog/ins/`: `create_t_ins_broker.xml`, `add_deposit_slip_columns_to_performa_master.xml`, `T_INS_PRICE_COMMODITY_create_table.xml`). Prefer `create_t_ins_<name>.xml` / `add_..._to_...xml`.

## CREATE vs ALTER rule
First grep all of `changelog/` and the entity's `@Table(name=...)`: if a `createTable` for that table already exists → emit `<addColumn>` changesets (one per column, pattern of `add_deposit_slip_columns_to_performa_master.xml`). If not → emit sequence + createTable + _AUD + audit-FK changesets (pattern of `create_t_ins_broker.xml`).

## changeSet id / author
- id: `yyyyMMdd-NN-kebab-description` (e.g. `20250803-01-create-t-ins-broker`, `20261028-02-add-deposit-slip-without-tax-to-performa-master`; date = today, NN increments within the file). Older files use bare `create-t-ins-cash-deposit` ids — do not copy that.
- author: `"soheil babadi"` (sole author in every changeset under `changelog/ins/`).
- Every changeSet MUST have a `<preConditions onFail="MARK_RAN">` guard: `<not><tableExists .../></not>`, `<not><columnExists .../></not>`, `<not><sequenceExists .../></not>`, `<not><foreignKeyConstraintExists foreignKeyName="..."/></not>` (all four seen in `create_t_ins_broker.xml`, `create_t_ins_cash_deposit.xml`, `add_deposit_slip_columns_to_performa_master.xml`).

## Table & column naming (Oracle dialect)
- Tables: `T_INS_<SNAKE_UPPER>` matching entity `@Table` (e.g. `T_INS_BROKER`, `T_INS_CASH_DEPOSIT`).
- Column prefixes mirror Java types: `C_` string, `N_` number, `D_` date, `F_` FK id, `IS_` boolean (`create_t_ins_cash_deposit.xml`).
- Types: String→`VARCHAR2(len)` (4000 for address/comment/description), Long/id/FK→`NUMBER(38,0)`, BigDecimal→`DECIMAL` (defaultValue `0` when entity defaults to zero), Date→`DATETIME`, boolean→`NUMBER(1)` defaultValue `0`, enum (@Enumerated STRING)→`VARCHAR2(20..50)`, version→`INT` nullable=false.
- Sequences: entities with `@GeneratedValue(strategy=SEQUENCE)` need a `createSequence` changeSet first: name `SEQ_INS_<X>` exactly matching `@SequenceGenerator(sequenceName=...)`, `startValue="1" incrementBy="1"` (see `CashDepositModel.java` generator `SEQ_INS_CASHDEPOSIT` ↔ `create_t_ins_cash_deposit.xml` id `create-seq-ins-cashdeposit`). Broker has no sequence (manual id) — check the entity.
- BaseClassModel columns must be included on every new table: `d_created_date DATETIME`, `d_last_modified_date DATETIME`, `c_created_by VARCHAR2(250)`, `c_last_modified_by VARCHAR2(250)`, `c_comment VARCHAR2(4000)`, `c_description VARCHAR2(4000)`, `n_version INT NOT NULL` (see `config/BaseClassModel.java` and `create_t_ins_broker.xml`).

## Envers audit tables — generated via Liquibase, NOT Hibernate
Every `@Audited` entity gets an explicit `<TABLE>_AUD` createTable changeSet in the same file (entities extend `BaseClassModel` which is itself `@Audited`; e.g. `BrokerModel.java` @Audited ↔ `create_t_ins_broker.xml` changeSet `20250803-02-create-t-ins-broker-aud`). `_AUD` structure: same columns as base table PLUS leading `REV NUMBER(38,0) PK (nullable=false)` + `ID` (also non-null) + `REVTYPE SMALLINT`; audit columns here are nullable (no NOT NULL copies). Then a third changeSet adds the FK to the revision table:
```xml
<addForeignKeyConstraint baseColumnNames="rev" baseTableName="t_ins_broker_aud"
    constraintName="FK_T_INS_BROKER_AUD_ON_REV" referencedColumnNames="rev"
    referencedTableName="revinfo"/>
```
`REVINFO` is the Envers revision entity `config/envers/RevInfo.java` (`@Table(name="REVINFO")`, columns REV/REVTSTMP/USERNAME); its own table comes from the legacy initdb chain, so never recreate it — only reference it.

## FK naming
`FK_<TABLE>_ON_<COLUMN>` style for audit links (`FK_T_INS_BROKER_AUD_ON_REV`, `FK_T_INS_CASH_DEPOSIT_AUD_ON_REV`); business FKs like `FK_T_INS_REMITTANCE_GOOD_ITEM_ON_F_REMIITANCE` (commented example in `create_remittance_table.xml`). Always paired with a `foreignKeyConstraintExists` precondition.

## Registering in master.xml
Append a new include at the END of `src/main/resources/config/liquibase/master.xml` (ordering = chronological append; latest `add_deposit_slip...` is last):
```xml
<include relativeToChangelogFile="true" file="changelog/ins/create_t_ins_<name>.xml"/>
```
Attribute order varies in the file; use the `relativeToChangelogFile` first form used by recent entries. Path is relative WITHOUT leading slash for new entries (older ones use `/changelog/...` — follow the recent style).

## Open questions
- Whether to also add indexes on FK columns: only `ins_gaam.xml`/`ins_proforma_bank_bill.xml` create index changesets (`idx-bank-bill-proforma-detail`) — default: skip indexes unless the user asks.
- Seed data files exist under `changelog/data/*.sql` but are not included in master.xml — default: do not touch them.
- Some files are fully commented out after execution (e.g. `create_remittance_table.xml`) — do NOT comment out new files; leave them active.
