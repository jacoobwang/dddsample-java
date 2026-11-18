# dddsample-java (Lightweight DDD + CQRS Template Repository)

[English](./README.md) | 简体中文

本仓库是基于「规范税治理优化方案」构建的 Java 21 + Spring Boot 4 轻量级 DDD 标准工程模版。
通过两个完整的限界上下文示例（**核心业务聚合 `order`** 与 **简单单表 CRUD `auditlog`**），演示如何在保持严格分层边界的前提下，消除冗余 DTO、纯搬运 Converter 和形式主义装配代码。

---

## 1. 四层架构与依赖方向

```text
adapter  ──▶  app  ──▶  domain
               ▲          ▲
               │          │
             infra ───────┘
```

| 层级 | 职责 | 允许依赖 | 禁止依赖 |
|---|---|---|---|
| `adapter` | HTTP Controller、SQS Consumer、定时任务、协议转换 | `app`、`domain`、`shared` | `infra`（DO、Mapper、RepoImpl、QueryImpl） |
| `app` | 写用例编排（`CommandService`）、读用例契约（`QueryService`）、跨域入站契约（`Port`）、事务边界 | `domain`、`shared`、其他域 `app..port` | `adapter`、`infra`、DO、Mapper、第三方 SDK |
| `domain` | 聚合根/实体、值对象（`*VO`）、校验器（`*Validator`）、外部策略接口（`*PolicyService`）、仓储接口（`*Repo`） | 纯 Java、`shared`、统一异常 | Spring、ORM、HTTP/AWS SDK、`adapter`/`app`/`infra` |
| `infra` | 仓储实现（`repoimpl`）、查询实现（`queryimpl`）、外部策略实现（`policyimpl`）、DO、Mapper、MapStruct Converter | `app`、`domain`、`shared`、技术框架 | `adapter` |
| `shared` | 跨上下文共享的稳定枚举与契约常量 | 纯 Java | 任何业务层（`adapter`/`app`/`domain`/`infra`） |

---

## 2. 三条核心减负规则（本模版示范重点）

### 规则一：【参数归位】上下文/路径参数归方法签名，请求体归 `Command`/`Query`
- **`*Command` / `*Query` 只放请求体（Body）字段或 ≥2 个组合过滤条件**，绝不塞入 `@PathVariable`（`id`、`pageNum`、`pageSize`）和 `RequestContext`（`userId`、`operatorId`）。
- **路径与登录态参数直接作为 Service 方法的前置标量参数（0~2 个）**。
- **单值/主键集合操作直接传参（0 DTO）**：如 `deleteById(Long id)`、`batchDelete(List<Long> ids)`、`getById(Long id)`。
- **Controller 直接 `@RequestBody @Valid` 绑定 App 层 `Command`/`Query`**：消除 85%+ 重复的 Adapter `Request` 与纯搬运 Converter，且天然免疫客户端伪造 `id`/`operatorId`，无 `@Valid` 时序死锁。

### 规则二：【出参守界】单值直返、多字段建 `Result`、无变形不建 `Response`
- **`CommandService` 出参**：允许 `void`、`Long/long`（创建返回 ID）、`String`（单值短链/凭证）、`*CommandResult`（≥2 字段）。**严禁返回 `boolean`（失败必须抛 `BackendException`）和集合类型**。
- **`QueryService` 出参**：允许所有单值标量（`boolean`/`Long`/`Integer`/`String`/`BigDecimal`/`byte[]`）、`*QueryResult` 及标准容器（`Optional`/`List`/`Set`/`Map`/`PageResult`）。
- **Adapter `*Response` 按需创建**：
  - 字段一致时 Controller 直接返回 `BaseResponse<XxxQueryResult>`（见 `AuditLogAdminController`）；
  - 仅在需要**枚举转中文/国际化文案（如 `statusLabel`）、脱敏裁剪、层级展平、Protobuf 转换**时才创建 `*Response` 与 `AdapterConverter`（见 `OrderController#getDisplayDetail`）。

### 规则三：【模板减负】保留 Service 接口，`@Service` 自动装配，纯记录表用 Domain `record`
- **免写 `@Configuration @Bean`**：保留 `CommandService` / `QueryService` 接口契约，`CommandServiceImpl` 直接标注 `@Service` + `@Validated`，public 写方法标注 `@Transactional(rollbackFor = Exception.class)`。
- **纯数据表轻量建模**：无状态机与复杂不变量的纯记录表（如 `auditlog`），Domain 实体直接定义为 `public record AuditLog(...)` + `create/restore` 静态工厂，单模块仅需 13 个文件。

---

## 3. 工程目录导览

```text
src/main/java/com/example/dddsample/
├── common/                        # 基础响应封套 BaseResponse、分页 PageResult、统一异常 BackendException
├── shared/order/                  # 跨层共享枚举 OrderStatusEnum
│
├── # [示例 A] 核心业务上下文：order（含聚合状态机、VO、Validator、Policy、Port、展示变形 Response）
├── domain/order/
│   ├── Order.java                 # 聚合根（final class + private 构造 + pay/ship 状态机）
│   ├── valueobject/MoneyVO.java   # 值对象（record，≤4 字段，强制 VO 后缀）
│   ├── validator/OrderQuotaValidator.java # 跨聚合静态校验器（无状态纯函数）
│   ├── policy/                    # 外部支付能力抽象接口 + Policy/PolicyResult DTO
│   └── repo/OrderRepo.java        # 领域仓储接口（仅收发 Order 聚合与主键 ID）
├── app/order/
│   ├── command/                   # OrderCommandService + Impl(@Service) + Create/ShipCommand
│   ├── query/                     # OrderQueryService + OrderPageQuery + OrderDetailQueryResult
│   └── port/                      # 跨上下文入站契约 OrderExternalPort + Impl + PortData
├── infra/order/
│   ├── dataobject/OrderDO.java    # 数据库表对象（严禁出 Infra）
│   ├── mapper/OrderMapper.java    # 数据库读写接口
│   ├── converter/OrderConverter.java # MapStruct 转换器（4 属性严格策略）
│   ├── repoimpl/OrderRepoImpl.java
│   ├── policyimpl/PaymentPolicyServiceImpl.java
│   └── queryimpl/OrderQueryServiceImpl.java # CQRS 读链路实现（无事务，直接查 Mapper）
├── adapter/order/
│   ├── controller/OrderController.java # 演示直接绑定 Command + 按需转换 DisplayResponse
│   ├── dto/OrderDisplayResponse.java   # 演示枚举转中文 statusLabel 的展示 DTO
│   └── converter/OrderAdapterConverter.java
│
└── # [示例 B] 简单单表 CRUD 上下文：auditlog（演示规则三 record 实体 + 0 Adapter DTO）
    ├── domain/auditlog/AuditLog.java   # 不可变 record 实体
    ├── domain/auditlog/repo/AuditLogRepo.java
    ├── app/auditlog/command/...        # SaveCommand + CommandService + Impl(@Service)
    ├── app/auditlog/query/...          # PageQuery + QueryResult + QueryService
    ├── infra/auditlog/...              # DO + Mapper + Converter + RepoImpl + QueryServiceImpl
    └── adapter/auditlog/controller/AuditLogAdminController.java # 0 Request / 0 Response / 0 AdapterConverter
```

---

## 4. 运行与架构门禁验证

本模版内置 `ArchUnitAllRulesTest`，在一个测试入口中自动校验分层依赖、参数归位白名单、CQRS 出参边界、事务注解与 VO 字段上限：

```bash
mvn test
```
