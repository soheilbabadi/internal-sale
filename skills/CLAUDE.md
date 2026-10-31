# CLAUDE.md — internal-sales (com.nicico.internal.sales)

## Stack summary (from pom.xml)
- Java 17 (`maven.compiler.source/target`, `java.version`).
- Spring Boot via parent `com.nicico.copper:copper-starter-parent:5.1.1` (internal NICICO starter; still javax.* namespace — use `javax.persistence`, `javax.validation`, NOT jakarta).
- MapStruct 1.4.2.Final + Lombok 1.18.24 (+ lombok-mapstruct-binding 0.2.0) wired through `maven-compiler-plugin` annotationProcessorPaths.
- Liquibase (`spring.liquibase.change-log: classpath:config/liquibase/master.xml`, changelog tables `Z_LIQ_CHANGELOG`/`Z_LIQ_CHANGELOG_LOCK`, contexts = active profile — src/main/resources/config/application.yml). Oracle dialect (VARCHAR2/NUMBER).
- Hibernate Envers (`spring-data-envers`; `@Audited` entities extend audited `config/BaseClassModel`; revision entity `config/envers/RevInfo.java` → table REVINFO; *_AUD tables are created explicitly in Liquibase changesets).
- RabbitMQ AMQP (`spring-boot-starter-amqp`), Sleuth, Spring Security (`@PreAuthorize("@secUtil.hasAuthority('R_INS_X')")`), Swagger v3 annotations, Apache POI (exports), H2 (test).
- Internal SDKs: `com.nicico.bpms:bpms-client:3.6.9` (workflow), `com.fgostar.fms:fms-sdk:dev` (financial messaging), copper-common (`SearchDTO`, `SearchUtil`), copper-oauth (`OAUserDAO`).
- Sonar: project `internal-sales-backend` at http://sonar.icico.net.ir with `sonar.qualitygate.wait=true` — builds BLOCK on quality-gate failure. Rule java:S1192 is ignored only for `**/*DTO.java`.

## Package root & layout
Root: `com.nicico.internal.sales`. Feature-first modules (~32: proforma, wf, lc, broker, deposit, gaam, extrabill, pricing, ime, pms, …). Each module uses sub-packages: `controller / service / repository / model / dto / mapper (or dto-colocated) / enums`. Entities are `*Model` extending `config.BaseClassModel`; DTOs are `*Dto` classes with nested `Create`/`Info`; mappers are MapStruct interfaces with `componentModel = "spring"`; services are `XyzService` interface + `XyzServiceImpl` (`@RequiredArgsConstructor`, constructor injection); exceptions come from `exception.InternalSaleCustomException` nested types, handled centrally by `exception.FgostarExceptionHandlerControllerAdvice`. API base path pattern: `/api/v1/ins/...`.

## Skills
- `skills/generate-dto/SKILL.md` — triggers: "generate DTO", "create dto", "dto and mapper for". Produces `<module>.dto.XyzDto` (+ nested Create/Info) and a MapStruct `XyzMapper`.
- `skills/generate-service/SKILL.md` — triggers: "generate service", "service for entity", "repository and service". Produces repository (if missing) + service interface + impl. No controller.
- `skills/generate-liquibase/SKILL.md` — triggers: "generate liquibase", "create changeset", "schema change". Writes a changeset under `config/liquibase/changelog/ins/` and appends its include to `master.xml`. Never runs Liquibase.
- `.claude/skills/generate-endpoint/SKILL.md` — triggers: "add an endpoint for X", "expose X via API", "write a controller for X", "create a REST API for X". Produces controller methods/classes only (`/api/v1/ins/...`, `ResponseEntity`, per-method `@PreAuthorize`, Swagger v3 `@Operation`). Stops if service/DTOs are missing.

## Recommended workflow for a new feature
1. generate-dto (entity → DTOs + mapper)
2. generate-service (repository if missing + service interface + impl)
3. generate-endpoint (controller methods on top of the existing service)
4. generate-liquibase (only if schema changes; include `_AUD` table for @Audited entities)
Then build — Sonar quality gate is enforced (`sonar.qualitygate.wait=true`) and will fail the build on violations.
