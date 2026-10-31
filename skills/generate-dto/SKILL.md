---
name: generate-dto
description: Given an entity/model class in the sales project, generate its DTO(s) and MapStruct mapper following this codebase's exact conventions. Trigger phrases: "generate DTO", "create dto", "make a dto for", "dto and mapper for".
---

# Generate DTO + MapStruct Mapper (internal-sales)

Input: a `*Model.java` entity (e.g. `com.nicico.internal.sales.broker.model.BrokerModel`).
Output: one DTO file + one Mapper interface. **Never generate service, controller, or repository here.**

## Package paths
- DTO → `<module>.dto` under root `com.nicico.internal.sales` (see `src/main/java/com/nicico/internal/sales/broker/dto/BrokerDto.java`).
- Mapper → `<module>.mapper` if that dir exists (broker), otherwise colocate in `<module>.dto` (dominant pattern — see `src/main/java/com/nicico/internal/sales/deposit/dto/CashDepositMapper.java`, `src/main/java/com/nicico/internal/sales/lc/dto/LcMapper.java`). Check the module first; prefer `<module>.dto`.
- Request-only helper DTOs may live in `<module>.dto.request` with `...Request` suffix (see `src/main/java/com/nicico/internal/sales/lc/dto/request/BrokerEmailRequest.java`).

## DTO style (class, NOT record)
Plain Lombok classes — no Java records anywhere in DTOs (86 `*Dto/*DTO` files, all classes):
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XyzDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;   // keep unique random value
    ...
}
```
(see `src/main/java/com/nicico/internal/sales/broker/dto/BrokerDto.java`, `src/main/java/com/nicico/internal/sales/deposit/dto/CashDepositDto.java`)

Suffix: `Dto` (newer modules: broker, deposit, lc, proforma) — do not introduce `ResponseDTO`; legacy `*DTO.java` exists (`proforma/dto/ProformaMasterDTO.java`) but new code uses `Dto`.

### Nested variants (the project convention)
Base class holds shared fields; nested static classes extend it:
```java
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("XyzDto.Create")
@NoArgsConstructor
public static class Create extends XyzDto { }

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("XyzDto.Info")
@NoArgsConstructor
public static class Info extends XyzDto {
    private Date createdDate;      // audit fields added only on Info
    private Date lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String comment;
}
```
(see `BrokerDto.java` inner `Create`/`Info`; `CashDepositDto.java` same pattern)
- `Info` = response: include `id` + audit fields from `BaseClassModel` (`src/main/java/com/nicico/internal/sales/config/BaseClassModel.java`: createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment). Do NOT expose `version`/`description` unless existing module DTO does.
- `Create` = request: business fields with validation; id included (update-by-id flow uses it, see `BrokerServiceImpl.save`).
- Names use entity camelCase field names minus the `C_/N_/D_/F_` column prefixes.

## Field selection & relations
- FK relations are exposed as **plain Long id fields**, never nested entities: `private Long proformaDetailId; private Long proformaMasterId; private Long tradeId;` (see `src/main/java/com/nicico/internal/sales/deposit/dto/CashDepositDto.java` mirroring `CashDepositModel`).
- Enums used by the entity are referenced directly (shared enum types): `private Acknowledgment acknowledgment;` (see `CashDepositDto.java` importing `lc.enums.Acknowledgment`).
- Collections: child-item lists appear as `List<XyzDto>` on master DTOs (see `src/main/java/com/nicico/internal/sales/proforma/dto/ProformaDetailDto.java`).

## Validation & docs annotations
Use `javax.validation.*` (NOT jakarta — Spring Boot via copper parent still uses javax/javax.persistence, see `BrokerDto.java` imports):
- `@NotBlank(message = "...")`, `@Size(max = N, message = "...")`, `@Pattern(regexp = "...", message = "...")`, `@Email(message = "...")`.
- Messages are in **Persian** (see `BrokerDto.java`: `"نام الزامی است"`).
- Every documented field gets `@Schema(description = "Persian label", example = "...")` (io.swagger.v3; `@ApiModel` from io.swagger.annotations on nested classes).

## MapStruct mapper
```java
@Mapper(componentModel = "spring")
public interface XyzMapper {
    XyzDto.Info toDTO(XyzModel model);
    XyzModel fromDTO(XyzDto.Create request);
}
```
(see `src/main/java/com/nicico/internal/sales/broker/mapper/BrokerMapper.java`, `deposit/dto/CashDepositMapper.java`)
- Method names: `toDTO` (entity→Info) and `fromDTO` (Create→entity). No `nullValuePropertyMappingStrategy` or other config is used.
- Add `@Mapping(target=..., source=...)` only when names differ (rare; see `goods/dto/GoodsMapper.java`).
- For mappers handling relations, services map ids manually (no nested mapping helpers exist).

## Open questions
- Mapper package: `mapper` vs `dto` colocated — both exist; default chosen: follow whichever the target module already uses, else `<module>.dto` (most common overall).
- Some newer DTOs omit `serialVersionUID` consistency; default: include it (as in BrokerDto/CashDepositDto).
