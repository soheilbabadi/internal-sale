# Internal Sales — Service Layer Guide for Version 5.0.5

This document describes the **service layer** of version 5.0.5 in plain language. It covers every service class that was added, moved, or renamed in this release, lists each public function it exposes, and explains what that function actually does and when it is used.

The new/changed services are grouped by business area:

1. [Cash Deposit (deposit/service)](#1-cash-deposit-services)
2. [Cash Deposit Receipt Sale (proforma/service/cash)](#2-cash-deposit-receipt-sale-service)
3. [Credit Helper (lc/service)](#3-credit-helper)
4. [PMS GAAM Integration (pms/service)](#4-pms-gaam-services)
5. [Accounting Detail Service (accounting/service)](#5-accounting-detail-service)
6. [Workflow Step Detectors (wf/detection)](#6-workflow-step-detectors)
7. [Process History Service (wf/detection)](#7-process-history-service)

---

## 1. Cash Deposit Services

**Files:** `CashDepositService.java` (interface) / `CashDepositServiceImpl.java` (implementation)
**Package:** `com.nicico.internal.sales.deposit.service`

This is the heart of the new Cash Deposit feature. A *cash deposit* is the initial deposit a customer pays on a proforma contract before settlement. This service handles looking up deposits, listing those that are ready for settlement (reckoning), emailing the broker to confirm settlement, and exposing workflow history for the UI.

### Functions

#### `getById(Long id)`
Fetches a single cash-deposit issue-history record by its primary key and returns it as a DTO (`CashDepositIssueHistoryDto.Info`). If no record exists with that id, a validation exception is thrown with the message *"رکورد پیش فاکتور جهت صدور اعتبار پیدا نشد"* (the proforma/issue record could not be found). Used by the detail screen of an issued deposit.

#### `getByMasterId(Long masterId)`
Returns **all** issue-history records that belong to one proforma master (contract). Each entity is mapped to a DTO. This lets the front-end show the full issuance history of a single contract in chronological order.

#### `findReadyReckoning(SearchDTO.SearchRq request)`
A paged, filterable search over the "ready for reckoning" view (`CashDepositReadyRevokingRepository`). It returns deposits that have completed their workflow and are waiting to be settled with the broker. After fetching the page, the function post-processes every row: if a `reckoningSendDate` is already stored, the row's `reckoningSend` flag is set to `true`, so the UI can grey out the "send reckoning email" button for deposits that were already notified.

#### `searchIssueHistory(SearchDTO.SearchRq request)`
Generic grid search over the cash-deposit issue-history table using the common `SearchUtil` mechanism (criteria, sorting, paging). Powers the main list screen of the module.

#### `generateBrokerEmailContent(long id)`
Builds the **Persian text body** of the settlement confirmation email for a given cash deposit. It loads the `CashDepositModel`, its proforma detail, gathers contract data (broker name, contract number and date, quantity in kilograms, product name, customer name) and composes a sentence like:

> "کارگزاری محترم {broker}: قرارداد شماره {contractNo} مورخ {date} جهت خرید {quantity} کیلوگرم محصول {good} توسط شرکت: {customer} جهت تسویه مورد تایید می باشد"

It is called both directly (to preview the text in the UI) and internally by `sendReckoningEmail`.

#### `markAllAsReckoning(Long proformaMasterId)`
Bulk state update: finds **every** cash deposit belonging to a proforma master and marks them all as settled — sets `reckoningSend = true`, stamps `reckoningSendDate` with the current date, and moves the `acknowledgment` status to `RECKONING`. Each row is saved immediately (`saveAndFlush`). This guarantees that once one deposit slip of a contract is sent for settlement, the whole contract's slips are treated as settled together.

#### `buildGaamBrokerEmailRequest(ProformaDetailModel detail)`
Assembles the structured `BrokerEmailRequest` payload needed to send mail: it first calls `markAllAsReckoning` for the detail's master, then loads the proforma master, resolves the seller's broker through `CreditHelper.fetchBrokerForTrade(tradeId)`, and fills in contract number, contract date, total quantity, customer name, good name, broker name and broker e-mail. Returns the ready-to-send request object.

#### `sendReckoningEmail(Long id)` *(transactional)*
The main user action behind the "ارسال ایمیل تسویه" button. In one transaction it:
1. Loads the cash deposit by id,
2. Marks all deposits of its master as reckoning,
3. Builds the broker e-mail request from the proforma detail,
4. Generates the Persian e-mail content,
5. Hands both off to `sendBrokerReckoningEmail`.

#### `sendBrokerReckoningEmail(BrokerEmailRequest emailRequest, String emailContent)`
Thin delivery wrapper: logs the generated content and delegates the actual sending to `NotificationService.sendEmailForLcBroker(...)` — the same broker-mail channel already used by the LC module.

#### `getHistoryDetail(Long id)`
Returns the full BPM/Flowable process-instance history for the workflow that governs this cash deposit. The chain is: cash deposit → proforma master → `processId` → `BpmsClientService.getProcessInstanceHistoryById(...)`. Used by the "workflow history" dialog.

#### `getUserTasksReport(Long id)`
Returns a map of user-task reports (who did what, and when) for the deposit's process instance. Before calling BPMS it checks that the master's `processId` is a valid UUID (returns `null` otherwise). After receiving the report, it replaces every numeric assignee id with the human-readable full name looked up from the OAuth user table (`OAUserDAO`), so the UI shows names instead of ids.

---

## 2. Cash Deposit Receipt Sale Service

**Files:** `CashDepositReceiptSaleService.java` / `CashDepositReceiptSaleServiceImpl.java`
**Package:** `com.nicico.internal.sales.proforma.service.cash` (moved here from `proforma.service` in 5.0.5; the old interface at the previous location was deleted)

This service creates a cash-deposit sale end-to-end: it calculates what the customer owes as a deposit, then generates the underlying proforma contract and registers the cash-deposit record. It supports both **regular goods** and **precious metals** (which are weighed by net weight rather than sold by contract quantity).

### Public functions

#### `generateSuggestion(CashSaleCreateRequest request)`
A "what-if calculator" for the UI. Given a trade-offer id (and optionally a net weight for precious metals), it:
- refreshes acknowledgment states for all deposits (`CashDepositStepDetector.updateAllAcknowledgments()`),
- loads the trade extract (offer) and the VAT rate configured for the contract's Jalali year (throws if the year's tax rate is missing),
- decides whether the commodity is a precious metal,
- computes: contract amount = unit price × contract quantity; sale amount = unit price × sale quantity (net weight for precious metals); **cash deposit** = deposit base × offer's cash percentage; amount without tax = sale amount − deposit; VAT amount = sale amount × VAT coefficient (both rounded up); amount with tax = amount-without-tax + VAT,
- returns a prefilled `CashDepositCreateRequest` suggestion the operator can review before creating.

#### `create(CashDepositCreateRequest requestDto)` *(transactional, rolls back on any exception)*
The real creation step:
1. Validates the request via `ProformaValidationService.validateCashDepositData` and checks the user may start a workflow (`canStartProcess`) — otherwise throws *"شما دسترسی لازم برای این عملیات را ندارید"*.
2. Depending on whether the trade is a precious-metal trade, builds either a regular or a precious-metal proforma master (with details, good items, contract number generation).
3. Starts the proforma workflow and saves the master.
4. Creates and persists the `CashDepositModel` linked to the new master/detail: acknowledgment = `RECKONING`, issue date, contract number, trade id, deposit slip type, amounts with and without tax, `reckoningSend = false`.
5. Returns the generated contract number as a string.

The implementation also contains many private helpers (`buildDeposit`, `createRegularProformaMaster`, `createPreciousProformaMaster`, `saveProformaWithDetails`, `generateRegularPerformaDetailList`, `buildDetailModel`, `buildGoodItemModel`, `buildPreciousGoodItemModel`, `buildRegularMasterModel`, `buildPreciousMasterModel`, `buildDetailGenerator`, …) that split the proforma assembly into regular vs. precious-metal pipelines; they are internal building blocks and are not exposed through the interface.

---

## 3. Credit Helper

**Files:** `CreditHelper.java` / `CreditHelperImpl.java`
**Package:** `com.nicico.internal.sales.lc.service` (new in 5.0.5)

A tiny shared utility service extracted so that LC, GAAM and Cash Deposit code can resolve "who is the broker of this trade?" in one place.

### Functions

#### `fetchBrokerForTrade(Long tradeId)`
Two-step lookup:
1. Queries the IME trade repository for the **seller broker code** of the given trade id — if the trade doesn't exist, throws *"کالای مورد نظر وجود ندارد"*.
2. Loads the `BrokerModel` by that code — if the broker record is missing, throws *"اطلاعات تماس ایمیل کارگزار موجود نمی باشد."*

Returns the broker entity (name + e-mail), which callers use to address settlement notifications.

---

## 4. PMS GAAM Services

**Files:** `PMSGaamService.java`, `PMSGaamServiceImpl.java`, `PMSLcResponseListener.java`
**Package:** `com.nicico.internal.sales.pms.service` (all new in 5.0.5)

GAAM bills (ضمانتنامه بانکی) must now be exported to the external **PMS** system just like letters of credit. These classes implement that integration over RabbitMQ, reusing the existing LC messaging infrastructure with mark `oragh` to distinguish GAAM traffic.

### `PMSGaamService` functions

#### `createPMSGaam(Long proformaMasterId, String username, boolean resend)`
Batch entry point: finds **all GAAM bills** of a proforma master and sends each one to PMS. Guards against duplicates — unless `resend = true`, any GAAM that already has a `pmsBillId` is skipped with an info log. If none are found for the master, a validation exception is raised. Per-item failures are logged and rethrown as application-server exceptions.

#### `createPMSGaam(Long gaamId, boolean resend)` *(throws IOException)*
Single-bill variant: loads one GAAM by id, applies the same "already sent? skip unless resend" rule, and sends it using the currently authenticated user (`SecurityUtil.getUsername()`).

#### `sendGaamModelToPMS(GaamModel model, String username)`
The actual dispatch:
- checks the export notification configuration for entity type `LETTER_OF_CREDIT`; if `sendPms` is disabled, silently returns (feature switch),
- builds the full `PMSLcDTO.Create` payload via `buildPmsGaamCreateDto`,
- logs the requesting user (username, national code, full name),
- publishes a `RabbitListenerRequestDTO` (target URL, local GAAM id, create payload, response routing key) onto the PMS LC exchange/routing key.

#### `updatePmsGaam(String pmsId, String username)`
Pushes changes of an already-exported GAAM back to PMS. Locates the GAAM by its stored `pmsBillId`, re-resolves customer id, issuing bank LC id, trading (agent) bank LC id, pre-factor id, goods id, dates (converted to Jalali), amount, price and document number (Sepam code, falling back to treasury id), then publishes a `PMSLcDTO.Update` message to the **update** routing key with a matching update-response routing key. Fixed constants identify the record type to PMS: `type = 523`, `state = 281`, `mark = oragh`.

#### `savePmsId(Long gaamId, String pmsBillId)`
Persistence callback: stores the PMS-assigned bill id on the GAAM record. Called by the asynchronous response listener once PMS confirms creation. Logs an error (rather than throwing) if the GAAM vanished meanwhile.

Private helpers worth knowing: `buildPmsGaamCreateDto` (assembles the create payload), `extractCustomerId` (matches the customer in PMS by economic code or registration number), `extractIssuingPMSBankId` / `extractTradingPMSBankId` (map local banks to their PMS LC bank ids, with title-based fuzzy matching for the agent bank and fallback to the issuing bank), `getAmount` (chooses net quantity for precious metal, credit quantity for non-cash settlements, otherwise plain quantity), and `resolveUsername` (falls back to the configured default PMS user).

### `PMSLcResponseListener` (message consumer)

Not a classic service but a Spring `@Component` with two `@RabbitListener` methods plus one dispatcher:

#### `handlePmsLcResponse(String message)`
Consumes from the LC **response queue** (creation replies from PMS).

#### `handlePmsLcUpdateResponse(String message)`
Consumes from the LC **update-response queue** (update replies from PMS).

#### `dispatchResponse(String message, String actionName)` (private, shared logic)
Both listeners delegate here. It:
1. Checks the `sendPms` feature switch again (ignore responses if disabled),
2. Deserializes the response envelope, defensively skipping malformed messages (no request id or empty PMS id),
3. Routes by the request **mark**:
   - `oragh` / `ORAGH` → `PMSGaamService.savePmsId(...)` — this is how GAAM responses land on the GAAM record,
   - `brat` / `BRAT` → reserved for Extra-Bill (currently commented out pending `PMExtraBillService`),
   - anything else → `PMSLcService.savePmsId(...)` (classic letter-of-credit behaviour, unchanged).

This single-listener routing design lets GAAM reuse the LC queues without breaking existing LC flows.

---

## 5. Accounting Detail Service

**Files:** `AccountingDetailService.java` / `AccountingDetailServiceImpl.java`
**Package:** `com.nicico.internal.sales.accounting.service`

A business façade over the new Feign client to the central **Accounting** system. It deals with "detail accounts" (حساب تفصیلی) — the accounting sub-ledger codes for persons, companies and financial instruments. All inputs are validated, normalized (national-code checksums), and translated into Accounting REST calls.

### Query functions

#### `findDetailOfNationalCode(String nationalCodeOrId)`
Smart entry point: normalizes the input, then decides automatically — an 11-digit company national id goes to `findDetailForCompany`, a 10-digit personal code goes to `findDetailForPerson`; ambiguous input tries person first, then company. Throws if the value is blank.

#### `findDetailForPerson(String nationalCode)`
Searches the accounting details whose code equals the configured **person prefix ("01/") + national code**. Prefers an exact code match, falls back to the first hit. Returns `Optional.empty()` when nothing is found.

#### `findDetailForCompany(String nationalId)`
Same idea with the **company prefix ("02/") + national id**.

#### `searchDetailsByCode(String detailCode)` / `searchDetailsByCode(String detailCode, EOperator operator)`
Low-level grid query against Accounting's `detailGridFetch` endpoint. Builds a `CriteriaParamDTO` (sort by `detailNumber`, fixed component/data-source ids, one criteria on `code` with the supplied operator — `equals`, `replace`, etc.) and returns the matched rows, or an empty list for blank input.

#### `findParentByCode(String parentCode)`
Looks up a parent (group) detail account by its exact code, e.g. `"107/18"`.

#### `resolveParentDetail(String targetCode)`
Given a full child code such as `01/4270205776`, finds the correct parent among candidate prefixes (`0`, `01`, `01/4`, …) by choosing the **longest matching prefix that itself has children** (`childrenDigitCount > 0`) — e.g. resolves to `01/4`. Essential before creating any child detail.

#### `getChildrenByParentDetailId(Long parentDetailId, Integer startRow, Integer endRow, String sortBy)`
Pages through the children of a parent detail using `/rest/detail/detailGridFetch` (defaults: row 0–500, optional sort like `-detailName`). Throws a validation error if the parent id is missing.

### Sequence-generation functions

#### `generateNextSequenceCode(String parentCode)`
Computes the next sequential child code under a parent: fetches the latest children sorted descending, extracts the trailing digits of the newest child's code, adds one, zero-pads to at least 4 digits and appends to the parent code (`…0001 → …0002`). Throws if the parent code is blank.

#### `generateNextFinancialInstrumentSequenceCode(FinancialInstrumentType instrumentType, String twoDigitCode, String yearSuffix)`
Specialized sequence generator for financial instruments (Letter of Credit `107/18`, GAM `108/18`, Electronic Promissory Note `109/18`). Validates that the branch/user sub-code is exactly two digits, builds the base prefix as `mainCode + twoDigitCode + YY` (year defaults to the current Shamsi year when omitted), inspects the highest existing child and returns the next sequence **number**.

### Creation functions

#### `createDetailForPerson(CreatePersonDetailDto request)`
Registers a natural person in Accounting. Validates national code and full name, splits the normalized code into a leading part (configurable `parentDigits`, default 1) and the remaining digits (used as the `detailNumber`), resolves/loads the parent detail (error if absent), then assembles a `DetailInfoDto` — name, note `"sales <fullName>"`, `dashChildrenCode = true`, `branchFree = true`, `childrenDigitCount = 0`, parent references — and calls `saveDetail`. Finally returns the freshly created detail (re-fetched by code, falling back to the submitted info). Important invariant kept by the code: **the `code` field is never set manually** when creating details.

#### `createDetailForCompany(CreateCompanyDetailDto request)`
The legal-entity counterpart: validates company name and 11-digit national id (length/numeric/checksum rules), derives parent code and detail number from the configured company prefix scheme, saves via the Feign client and returns the resulting `DetailDto`.

#### `createDetailForFinancialInstrument(CreateFinancialInstrumentDetailDto request)`
Creates a detail account for a payment instrument using the shared private pipeline `prepareAndSaveFinancialInstrumentDetail` (validate type/two-digit code/name → compute expected code → save with retry).

#### `generateAndCreateFinancialInstrumentDetail(FinancialInstrumentType instrumentType, String twoDigitCode, String yearSuffix, String detailName, String submissionId)`
One-stop convenience used by the LC/GAAM/Extra-Bill issuance flows: generates the next sequence, creates the detail in Accounting (honouring the retry policy and passing the `submissionId` for server-side tracking), then verifies by searching the expected code and returns a `FinancialInstrumentCreateResponseDto` containing success flag, final code, detail number, submission id and the created detail record. If `detailName` is blank it defaults to *instrument title + two-digit code*.

Supporting private machinery: `executeSaveDetailWithRetry` (configurable max attempts and back-off delay; re-interrupts safely and only gives up after exhausting attempts), `calculateNextFinancialInstrumentSequenceNumber`, `findLatestFinancialInstrumentChild`, `extractChildPrefix`, `buildChildDetailNumberString`, `buildExpectedChildCode`, `buildFinancialInstrumentBasePrefix`, `resolveTwoDigitShamsiYear`, and `extractConfiguredParentCode`.

---

## 6. Workflow Step Detectors

**Package:** `com.nicico.internal.sales.wf.detection` (new package in 5.0.5 — detectors were extracted from the old `wf.service` classes; `GaamProcessVariableDetector(Impl)` was renamed/moved to `GaamStepDetector(Impl)`)

Four sibling services that answer one question for their respective document type: **"where is this document right now in its approval workflow, expressed as an Acknowledgment status?"** They inspect the Flowable/BPMS task history and keep the entity's `acknowledgment` column in sync so grids and filters can show settlement/remittance/final-check progress.

All four share the same three-method contract:

- **`detectStep(model)`** — pure calculation: derive the current `Acknowledgment` **without** writing anything.
- **`updateStep(model)`** — call `detectStep`, store the result on the entity and `saveAndFlush` it.
- **`updateAllAcknowledgments()`** — bulk refresh across all documents of that type (typically invoked before searches/suggestions so displayed statuses are fresh).

### `CashDepositStepDetector` / `CashDepositStepDetectorImpl`
Simplest of the four — cash deposits don't have multi-stage broker tasks:
- `detectStep`: if the deposit was already emailed for settlement (`reckoningSend`) → `REMITTANCE`; otherwise look at the proforma master's workflow status: `ACCEPTED` → `FINISHED`, `REVERSAL`/`CANCELED` → `CANCELED`, anything else → `RECKONING` (missing master or null status also yields `FINISHED`).
- `updateAllAcknowledgments`: iterates all deposits, skipping terminal ones (`CANCELED`/`FINISHED`), and updates the rest. *(Note: the loop currently uses `return` instead of `continue` on terminal items, so iteration stops at the first finished record — flagged here as a known quirk.)*
- `updateStep`: detect + persist.

### `GaamStepDetector` / `GaamStepDetectorImpl`
Full task-mining algorithm for GAAM bills:
- `detectStep`:
  1. Terminal workflow status wins first: `ACCEPTED` → `FINISHED`; `CANCELED`/`REVERSAL` → `CANCELED`.
  2. Pulls the user-task report for the process (`ProcessHistoryService.getUserTaskReportOrEmpty`); empty report or no named activities → `FINISHED`.
  3. If **any** task carries a local variable `approved = false` → `CANCELED` (someone rejected it).
  4. Otherwise find the currently open task (`endDate == null`) and map it via `GaamProcessVariable`: draft registration / settle-sure → `RECKONING` (تسویه), remit-sure → `REMITTANCE` (حواله), final check → `FINAL_CHECK` (بررسی نهایی).
  5. If no task is open, reconstruct progress from completed-and-approved tasks: approved FinalCheck → `FINISHED`, approved RemitSure → `FINAL_CHECK`, approved SettleSure → `REMITTANCE`, else → `RECKONING`.
- `updateAllAcknowledgments`: refreshes only bills whose workflow status is `IN_PROGRESS`.
- `updateStep`: detect + persist.

### `ExtraBillStepDetector` / `ExtraBillStepDetectorImpl`
Identical five-phase algorithm applied to extra bank bills (`ExtraBankBillModel`), keyed on `ExtraBillProcessVariable` steps (`BillDraftRegistration`, `BillSettleSure`, `BillRemitSure`, `BillFinalCheck`) with the same RECKONING → REMITTANCE → FINAL_CHECK → FINISHED ladder. Bulk update likewise targets only `IN_PROGRESS` bills.

### `LcStepDetector` / `LcStepDetectorImpl`
The letter-of-credit flavour, keyed on `LcProcessVariable` steps (`CreditBridge`, `SettleSure`, `RemitSure`, `FinalCheck`). Differences from GAAM/ExtraBill: unknown activity names fall back to `FinalCheck`, and the completed-task reconstruction maps approved FinalCheck → `FINISHED`, approved RemitSure → `FINAL_CHECK`, approved SettleSure → `REMITTANCE`, default → `RECKONING`. Note it evaluates the task report *before* checking terminal status (opposite order of GAAM/ExtraBill) — functionally equivalent outcomes.

---

## 7. Process History Service

**Files:** `ProcessHistoryService.java` / `ProcessHistoryServiceImpl.java`
**Package:** `com.nicico.internal.sales.wf.detection` (renamed in 5.0.5 from `wf.service.ProcessStatusDeterminerService(Impl)` and moved into the detection package)

A centralized read-only facade over the BPMS engine for every workflow-bearing document type. Two kinds of output per document:

- **History detail** (`ProcessInstanceHistory`) — the complete audit trail of the process instance, with every task's assignee id replaced by the user's real full name (parsed defensively; non-numeric assignees are left untouched).
- **Summary report** (`Map<String, List<UserTaskReportDTO>>`) — the raw per-user task report used both by the UI and by the step detectors above.

### Functions

| Function | What it does |
|---|---|
| `getLcHistoryDetail(Long lcId)` | Loads the LC (error *"آیتم مورد درخواست وجود ندارد"* if missing) and returns its process history with resolved assignee names. |
| `getLcSummaryReport(Long lcId)` | Same lookup, returns the task-report map instead. |
| `getProformaHistoryDetail(Long proformaMasterId)` | Process history for a proforma master. |
| `getProformaSummaryReport(Long proformaMasterId)` | Task report for a proforma master. |
| `getRemittanceHistoryDetail(Long remittanceId)` | Process history for a remittance master. |
| `getRemittanceSummaryReport(Long remittanceId)` | Task report for a remittance master. |
| `getGaamHistoryDetail(Long gaamId)` | Process history for a GAAM bill. |
| `getGaamSummaryReport(Long gaamId)` | Task report for a GAAM bill. |
| `getProformaBankBillHistoryDetail(Long billId)` | Process history for an extra bank bill. |
| `getProformaBankBillSummaryReport(Long billId)` | Task report for an extra bank bill. |
| `getUserTaskReportOrEmpty(String processInstanceId)` | Null-safe primitive used everywhere (including the step detectors): queries `ProcessService.getUserTasksReport` and converts `null` results into an empty map so callers can stream safely. |

Internally it relies on small `findXxxOrThrow` helpers per repository and a shared `getHistoryWithResolvedAssignees(...)` routine, keeping the "human-friendly history" behaviour consistent across all five document types.

---

## Deleted / superseded services in 5.0.5

For completeness, these service-layer files disappeared in this release:

- `nosa/LcNosaCodeService` & `LcNosaCodeServiceImpl` — replaced by the generic `NosaCodeService` plus the new scheduled jobs in `schedule/NosaCodeScheduler`.
- `wf/service/AcknowledgmentDeterminer(Impl)` — logic absorbed into the four `wf/detection/*StepDetector` services.
- `wf/service/GaamProcessVariableDetector(Impl)` — renamed to `GaamStepDetector(Impl)` in the detection package.
- `wf/service/ProcessStatusDeterminerService(Impl)` — renamed to `ProcessHistoryService(Impl)` and moved to the detection package.
- `wf/service/WorkflowProcessSupport` — removed; responsibilities lie with `ProcessService` / step detectors.
- `proforma/service/CashDepositReceiptSaleService(Impl)` — relocated into the `proforma.service.cash` sub-package alongside `CashSaleService`.

