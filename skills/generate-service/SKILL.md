---
name: generate-service
description: Given an entity in the sales project, generate repository (if missing) + service interface + ServiceImpl following this codebase's exact patterns. Trigger phrases: "generate service", "create service layer", "service for entity", "repository and service".
---

# Generate Repository + Service (internal-sales)

Input: a `*Model` entity class. Output: `XyzRepository` (only if missing), `XyzService` interface, `XyzServiceImpl`. **Never generate a controller here.**

## Pattern: interface + impl (dominant)
79 `*Service.java` interfaces vs 76 `*ServiceImpl.java` (e.g. `src/main/java/com/nicico/internal/sales/broker/service/BrokerService.java` + `BrokerServiceImpl.java`; also `deposit/service/CashDepositService(Impl).java`). Always generate both, in `<module>.service`.

## Repository
```java
@Repository
public interface XyzRepository extends JpaRepository<XyzModel, Long>, JpaSpecificationExecutor<XyzModel> {
}
```
(see `src/main/java/com/nicico/internal/sales/broker/repository/BrokerRepository.java`) — package `<module>.repository`. Add derived query methods only as needed; audit repositories exist separately (e.g. `gaam/repository/GaamAuditRepository.java`).

## Service interface — standard method set
Copy these signatures verbatim from `broker/service/BrokerService.java`:
```java
SearchDTO.SearchRs<XyzDto.Info> search(SearchDTO.SearchRq request);
XyzDto.Info getById(long id);
List<XyzDto.Info> getAll();
XyzDto.Info save(XyzDto.Create request);
void delete(Long id);   // present in impl; add to interface when module needs it
```
- `SearchDTO.SearchRq/SearchRs` come from the copper SDK: `com.nicico.copper.common.dto.search.SearchDTO` (copper-starter-parent 5.1.1, see `pom.xml`).

## ServiceImpl skeleton
```java
@RequiredArgsConstructor
@Service
public class XyzServiceImpl implements XyzService {
    private static final String XYZ_NOT_FOUND_MESSAGE = "کارگزار تعریف نشده است"; // Persian literal constant
    private final XyzRepository repository;
    private final XyzMapper mapper;

    @Override
    public SearchDTO.SearchRs<XyzDto.Info> search(SearchDTO.SearchRq request) {
        return SearchUtil.search(repository, request, mapper::toDTO);
    }

    @Override
    public XyzDto.Info getById(long id) {
        XyzModel model = repository.findById(id)
            .orElseThrow(() -> new InternalSaleCustomException.ValidationException(XYZ_NOT_FOUND_MESSAGE));
        return mapper.toDTO(model);
    }
    ...
}
```
Grounded in `src/main/java/com/nicico/internal/sales/broker/service/BrokerServiceImpl.java` and `deposit/service/CashDepositServiceImpl.java`.

Conventions:
- **Injection:** constructor via Lombok `@RequiredArgsConstructor` + `private final` fields. Never `@Autowired` field injection. Add `@Slf4j` when logging (see `CashDepositServiceImpl.java` line 37).
- **Mapper usage:** inject the MapStruct mapper (`componentModel = "spring"`) and call `mapper.toDTO(...)`/`mapper.fromDTO(...)`. One exception exists where `BeanUtils.copyProperties(request, model)` is used inside `save` (BrokerServiceImpl.save) — prefer mapper; BeanUtils only for update-in-place copy.
- **Search:** always delegate to `com.nicico.copper.common.domain.criteria.SearchUtil.search(repository, request, mapper::toDTO)` (BrokerServiceImpl).
- **Transactions:** no class-level `@Transactional`. Annotate only multi-write methods with method-level `@Transactional` from `org.springframework.transaction.annotation.Transactional` (see `CashDepositServiceImpl.sendReckoningEmail`, `gaam/service/GaamServiceImpl.java` lines 119/158/280). Use `@Transactional(readOnly = true)` on heavy read methods (GaamServiceImpl line 413). Simple single-repo CRUD methods are left unannotated.
- **Exceptions:** throw nested types of `com.nicico.internal.sales.exception.InternalSaleCustomException` — most commonly `ValidationException` for not-found/business errors (Persian messages); also available: `ResourceNotFoundException`, `DuplicateEntityException`, `AccessDeniedException`, `ParentNotFoundException`, `PricingCalculationException` (see `exception/InternalSaleCustomException.java`). Do NOT create new exception classes; the advice in `exception/FgostarExceptionHandlerControllerAdvice.java` already maps them.
- Error message constants: `private static final String MSG_...` or `..._MESSAGE` at top of impl (both spellings exist; prefer `MSG_` per CashDepositServiceImpl).

## Cross-module dependencies
Services freely inject other modules' repositories/services (e.g. BrokerServiceImpl injects `IMETradeRepository`; CashDepositServiceImpl injects `ProformaDetailRepository`, `NotificationService`, bpms `BpmsClientService`, copper `OAUserDAO`). Keep such injections as `private final` too, but avoid adding NEW cross-module edges when a same-module alternative exists (proforma/wf cycles are a known debt).

## Open questions
- `delete` is public in `BrokerServiceImpl` but absent from `BrokerService` interface — default: declare it on the interface.
- Return type of delete: existing impls use `void` — default `void`.
- No active unit tests enforce service shape (test files under `src/test` are commented out, e.g. `lc/service/LcServiceImplTest.java`); default: no test generation unless requested.
