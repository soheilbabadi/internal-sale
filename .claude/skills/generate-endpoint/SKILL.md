---
name: generate-endpoint
allowed-tools: Read, Write, Edit, Glob, Grep
description: Generate a REST endpoint (controller method or controller class) for this project. Trigger phrases: "add an endpoint for X", "expose X via API", "write a controller for X", "create a REST API for X", "add API for <module>".
---

# generate-endpoint — REST endpoints for com.nicico.internal.sales

All rules below are extracted from real controllers in `src/main/java/com/nicico/internal/sales/`. Do not invent new conventions.

## Where controllers live & how to find collaborators
- One controller per resource at `<module>/controller/<X>Controller.java` under package root `com.nicico.internal.sales` (see `broker/controller/BrokerController.java`, `customer/controller/CustomerController.java`).
- Locate collaborators by convention: service interface `<module>/service/<X>Service.java` (e.g. `broker/service/BrokerService.java`), DTOs `<module>/dto/<X>Dto.java` with nested `Create`/`Info` (e.g. `broker/dto/BrokerDto.java`).
- **If no service exists for the resource: STOP. Tell the user to run `generate-service` first. Never silently create a service/repository/mapper/DTO.**

## Class-level rules
Annotations (order as in `broker/controller/BrokerController.java`): `@RestController`, `@RequiredArgsConstructor`, `@RequestMapping("/api/v1/ins/...")`, class-level `@PreAuthorize("@secUtil.hasAuthority('R_INS_<RESOURCE>')")`; add `@Slf4j` only if the methods log (`loading/controller/LoadingPlaceController.java`, `deposit/controller/CashDepositController.java`).
- Base path style: `/api/v1/ins/<kebab-resource>` — e.g. `/api/v1/ins/customer`, `/api/v1/ins/cash-deposit`, `/api/v1/ins/loading/loading-place`, `/api/v1/ins/sales/brokers` (one legacy prefix). Context path `/internal-sales-backend` comes from `config/application.yml` — never put it in `@RequestMapping`. Do NOT invent `/api/v2` or other versions (single exception `api/v2/ins/accounting/detail` already exists; reuse existing prefix when extending that controller).
- Security is method-level `@PreAuthorize`, not inherited config. Codes seen: read `R_INS_*`, create/update `C_INS_*`, delete `D_INS_*` (`CustomerController`, `ProformaController`).
- Swagger v3: `@Operation(summary = "...", description = "...")` on every method, Persian text (all of `CashDepositController`). `@Tag` at class level only in `goods/controller/GoodBucketController.java` (others comment it out) — default: omit unless user asks.
- Inject services as `private final <X>Service service;` (constructor injection via Lombok). Controllers never inject repositories or mappers directly — mapping happens in services (`BrokerController.java`).

## New method vs new controller
If a controller already exists for the resource (grep its base path), ADD the method there. Create a new controller only for a new resource/module.

## Method-level rules
- Mappings: GET detail `/{id}`, list/search `POST /search`, create `POST /save` or bare `POST`, update `PUT /{id}`, delete `DELETE /{id}` (`BrokerController`, `CustomerController`, `PricingCurrencyController`). No PATCH exists anywhere — use PUT.
- Path variables: `@PathVariable Long id` (camelCase business ids like `masterId`, `processInstanceId` — `ProformaController`, `ProcessGeneralController`). Query: `@RequestParam String keyword` etc.
- Bodies: `@RequestBody <X>Dto.Create request`; add `@Valid` when the DTO has javax validation annotations (22 usages, e.g. `BrokerController.save(@Valid @RequestBody BrokerDto.Create ...)`). Search bodies are `@RequestBody(required = false) SearchDTO.SearchRq searchRq` (`com.nicico.copper.common.dto.search.SearchDTO`).
- Pagination: NO Spring `Pageable` resolver params. Two patterns — (a) primary: `POST /search` taking `SearchDTO.SearchRq` (+ optional `@RequestParam MultiValueMap<String,String> criteria` converted via `SearchUtil.createSearchRq(NICICOCriteria.of(criteria))`) returning `SearchDTO.SearchRs<Dto.Info>` (`CustomerController.search`); (b) ad-hoc lists: `@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "id") String sortColumn` then `PageRequest.of(pageIndex, pageSize, Sort.by(sortColumn))` passed to service (`ProformaController.getFailed`, `ProcessGeneralController.searchProcess`). Default page size is 10.
- Return wrapper: always `ResponseEntity<T>` — no custom ApiResponse class exists. Typed generics for single resources (`ResponseEntity<BrokerDto.Info>`), `ResponseEntity<?>` acceptable for search/list (33 controllers). Never return raw entities.
- Status codes: `ResponseEntity.ok(...)` everywhere INCLUDING create/update/delete (dominant: `BrokerController.save`, `CustomerController.save/update/delete`). Minority uses `new ResponseEntity<>(response, HttpStatus.CREATED)` for saves (`PricingCurrencyController`) and `ResponseEntity<HttpStatus>` + `new ResponseEntity<>(HttpStatus.OK)` for void deletes (`CustomerController.delete`). Default to `ResponseEntity.ok(...)`. Use `ResponseEntity.notFound().build()` only where service returns empty optionality (`ProcessGeneralController.getProformaProcessVariable`).
- Logging: `@Slf4j` + `log.info("Creating proforma with tradeId: {}", dto.getTradeId())` placeholder style on mutating endpoints; `log.error("...", ex)` around try/catch conversions (`ProformaController` lines 133–278). Reads usually unlogged.
- Exceptions: controllers DO NOT catch or build error DTOs. Let services throw `exception.InternalSaleCustomException.*` — handled centrally by `exception/FgostarExceptionHandlerControllerAdvice.java` (@ControllerAdvice → nested `ErrorResponse{timestamp,status,message,path}`; Validation→406, ResourceNotFound→404, ConstraintViolation→422). Only observed controller-level exception style: wf controllers declare `throws Exception` (`ReversalProcessController.approveTask`).

## Method template
```java
@Operation(summary = "<Persian summary>")
@PreAuthorize("@secUtil.hasAuthority('<R|C|D>_INS_<RESOURCE>')")
@PostMapping("/save")
public ResponseEntity<<X>Dto.Info> save(@Valid @RequestBody <X>Dto.Create request) {
    return ResponseEntity.ok(service.save(request));
}
```

## Full class template (one controller per resource)
```java
package com.nicico.internal.sales.<module>.controller;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.<module>.dto.<X>Dto;
import com.nicico.internal.sales.<module>.service.<X>Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/<resource-path>")
@PreAuthorize("@secUtil.hasAuthority('R_INS_<RESOURCE>')")
public class <X>Controller {
    private final <X>Service service; // getters/search need no extra @PreAuthorize (read inherited)

    @Operation(summary = "<Persian>")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "<Persian>")
    @PostMapping("/search")
    public ResponseEntity<SearchDTO.SearchRs<<X>Dto.Info>> search(@RequestBody SearchDTO.SearchRq request) {
        return ResponseEntity.ok(service.search(request));
    }

    @Operation(summary = "<Persian>")
    @PreAuthorize("@secUtil.hasAuthority('C_INS_<RESOURCE>')")
    @PostMapping("/save")
    public ResponseEntity<<X>Dto.Info> save(@Valid @RequestBody <X>Dto.Create request) {
        return ResponseEntity.ok(service.save(request));
    }

    @Operation(summary = "<Persian>")
    @PreAuthorize("@secUtil.hasAuthority('D_INS_<RESOURCE>')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
```
(Import `org.springframework.http.HttpStatus` when using the delete method.)

## Pre-write checklist
1. Service + method exist? Grep `<module>/service/`. If missing → stop, tell user to run generate-service.
2. Request/response DTOs exist (`<module>/dto/<X>Dto.Create|Info`)? If missing → stop, suggest generate-dto.
3. Path collision? `grep -rn "@RequestMapping\|@GetMapping\|@PostMapping" src/main/java` for the intended base path + sub-path before writing.
4. Added `@PreAuthorize` with correct R_/C_/D_ code for non-read methods (this project adds them per-method).
5. Return type is `ResponseEntity<...>` wrapping DTO types only.

## Hard constraints
- Do NOT generate service, repository, mapper, DTO, or entity files.
- Do NOT invent a response wrapper or error format — `ResponseEntity` + `FgostarExceptionHandlerControllerAdvice.ErrorResponse` only.
- Do NOT add new base paths/versioning — reuse `/api/v1/ins/...` (or the module's existing prefix).

## Open questions
- Save status: OK vs CREATED ambiguous → default `ResponseEntity.ok` (majority: `BrokerController`, `CustomerController`).
- `@Tag` usage: only 1 active controller (`GoodBucketController`) → default omit.
- Interface vs Impl injection: `LoadingPlaceController` injects `LoadingPlaceServiceImpl` directly (anomaly) → default: inject the interface.
- File upload/download: no `MultipartFile` controller endpoints exist (only multipart limits in application.yml) → if requested, ask the user; no pattern to copy.
