# dddsample-java (Lightweight DDD + CQRS Template Repository)

English | [简体中文](./README_zh-CN.md)

A pragmatic, lightweight Domain-Driven Design (DDD) + CQRS template built with **Java 21** and **Spring Boot 4**.
Through two complete Bounded Context examples (**complex business aggregate `order`** and **simple single-table CRUD `auditlog`**), this repository demonstrates how to eliminate redundant DTOs, pass-through converters, and ceremonial wiring while preserving strict architectural boundaries enforced by ArchUnit.

---

## 1. Four-Layer Architecture & Dependency Direction

```text
adapter  ──▶  app  ──▶  domain
               ▲          ▲
               │          │
             infra ───────┘
```

| Layer | Responsibilities | Allowed Dependencies | Forbidden Dependencies |
|---|---|---|---|
| `adapter` | HTTP Controllers, MQ Consumers, Scheduled Jobs, protocol translation | `app`, `domain`, `shared` | `infra` (DO, Mapper, RepoImpl, QueryImpl) |
| `app` | Write use-case orchestration (`CommandService`), read contracts (`QueryService`), cross-context inbound contracts (`Port`), transaction boundaries | `domain`, `shared`, other contexts' `app..port` | `adapter`, `infra`, DO, Mapper, 3rd-party SDKs |
| `domain` | Aggregates/Entities, Value Objects (`*VO`), Validators (`*Validator`), External Policy interfaces (`*PolicyService`), Repository interfaces (`*Repo`) | Pure Java, `shared`, common exceptions | Spring, ORM, HTTP/Cloud SDKs, `adapter`/`app`/`infra` |
| `infra` | Repository implementations (`repoimpl`), Query implementations (`queryimpl`), Policy implementations (`policyimpl`), DO, Mapper, MapStruct Converters | `app`, `domain`, `shared`, frameworks/drivers | `adapter` |
| `shared` | Stable cross-context enums and contract constants | Pure Java | Any business layer (`adapter`/`app`/`domain`/`infra`) |

---

## 2. Three Core Boilerplate-Reduction Rules

### Rule 1: [Parameter Alignment] Context/Path Parameters in Method Signatures, Payload in `Command`/`Query`
- **`*Command` / `*Query` only hold request body payload fields or ≥2 combined filter fields**—never stuff `@PathVariable` (`id`, `pageNum`, `pageSize`) or `RequestContext` (`userId`, `operatorId`) into them.
- **Path and identity context parameters are passed directly as leading scalar arguments (0–2 parameters) on Service methods.**
- **Single-value / primary-key collection operations take direct scalar/collection arguments (0 DTOs)**: e.g., `deleteById(Long id)`, `batchDelete(List<Long> ids)`, `getById(Long id)`.
- **Controllers bind `@RequestBody @Valid` directly to App-layer `Command`/`Query`**: eliminates 85%+ of duplicate Adapter `Request` classes and pass-through Converters, naturally prevents client-side spoofing of `id`/`operatorId`, and avoids `@Valid` lifecycle ordering issues.

### Rule 2: [Return Boundary] Direct Scalars for Single Values, `Result` for Multi-Field, No `Response` Without Transformation
- **`CommandService` Return Types**: Allowed types are `void`, `Long/long` (returning created ID), `String` (single token/URL), or `*CommandResult` (≥2 fields). **Returning `boolean` (failures must throw `BackendException`) or collection types is strictly prohibited.**
- **`QueryService` Return Types**: Allowed types are single scalars (`boolean`/`Long`/`Integer`/`String`/`BigDecimal`/`byte[]`), `*QueryResult`, and standard containers (`Optional`/`List`/`Set`/`Map`/`PageResult`).
- **On-Demand Adapter `*Response`**:
  - When fields match 1:1, Controllers return `BaseResponse<XxxQueryResult>` directly (see `AuditLogAdminController`);
  - Create `*Response` and `AdapterConverter` **only** when view-specific transformation is required—such as enum-to-label localization (`statusLabel`), field masking/trimming, hierarchy flattening, or Protobuf serialization (see `OrderController#getDisplayDetail`).

### Rule 3: [Template Simplification] Keep Service Interfaces, Use `@Service` Auto-Wiring, Use Domain `record` for Append-Only/CRUD Tables
- **Zero `@Configuration @Bean` Boilerplate**: Keep `CommandService` / `QueryService` interfaces as layer contracts; annotate `CommandServiceImpl` directly with `@Service` + `@Validated`, and annotate public write methods with `@Transactional(rollbackFor = Exception.class)`.
- **Lightweight Modeling for Pure Data Tables**: For simple record tables without state machines or complex invariants (such as `auditlog`), define the Domain entity directly as an immutable `public record AuditLog(...)` with `create/restore` static factories—requiring only 13 files for a complete module.

---

## 3. Project Structure

```text
src/main/java/com/example/dddsample/
├── common/                        # BaseResponse envelope, PageResult, unified BackendException
├── shared/order/                  # Cross-layer shared enum OrderStatusEnum
│
├── # [Example A] Core Business Context: order (State Machine, VO, Validator, Policy, Port, Display Response)
├── domain/order/
│   ├── Order.java                 # Aggregate Root (final class + private constructor + pay/ship state machine)
│   ├── valueobject/MoneyVO.java   # Value Object (record, ≤4 fields, mandatory VO suffix)
│   ├── validator/OrderQuotaValidator.java # Cross-aggregate static validator (stateless pure function)
│   ├── policy/                    # External payment capability interface + Policy/PolicyResult DTOs
│   └── repo/OrderRepo.java        # Domain repository interface (exchanges only Order aggregate & ID)
├── app/order/
│   ├── command/                   # OrderCommandService + Impl(@Service) + Create/ShipCommand
│   ├── query/                     # OrderQueryService + OrderPageQuery + OrderDetailQueryResult
│   └── port/                      # Cross-context inbound contract OrderExternalPort + Impl + PortData
├── infra/order/
│   ├── dataobject/OrderDO.java    # Persistence Data Object (strictly confined to infra)
│   ├── mapper/OrderMapper.java    # Database access interface
│   ├── converter/OrderConverter.java # MapStruct converter (strict 4-attribute policy)
│   ├── repoimpl/OrderRepoImpl.java
│   ├── policyimpl/PaymentPolicyServiceImpl.java
│   └── queryimpl/OrderQueryServiceImpl.java # CQRS read path (non-transactional, queries Mapper directly)
├── adapter/order/
│   ├── controller/OrderController.java # Direct Command binding + on-demand DisplayResponse conversion
│   ├── dto/OrderDisplayResponse.java   # Presentation DTO adding localized statusLabel
│   └── converter/OrderAdapterConverter.java
│
└── # [Example B] Simple Single-Table CRUD Context: auditlog (Rule 3 record entity + 0 Adapter DTOs)
    ├── domain/auditlog/AuditLog.java   # Immutable record entity
    ├── domain/auditlog/repo/AuditLogRepo.java
    ├── app/auditlog/command/...        # SaveCommand + CommandService + Impl(@Service)
    ├── app/auditlog/query/...          # PageQuery + QueryResult + QueryService
    ├── infra/auditlog/...              # DO + Mapper + Converter + RepoImpl + QueryServiceImpl
    └── adapter/auditlog/controller/AuditLogAdminController.java # 0 Request / 0 Response / 0 AdapterConverter
```

---

## 4. Build & Architecture Guardrails (ArchUnit)

This repository includes `ArchUnitAllRulesTest`, which automatically verifies layer dependencies, parameter alignment whitelists, CQRS return type boundaries, transaction annotations, and VO field limits in a single test suite:

```bash
mvn test
```
