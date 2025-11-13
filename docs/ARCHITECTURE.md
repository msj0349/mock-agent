# Architecture Documentation

[English](#english) | [中文](#中文)

---

## English

### System Architecture

This document describes the overall architecture of the system, including design principles, component interactions, and key architectural decisions.

## Design Principles

### 1. Modularity
The system is divided into independent, loosely-coupled modules that can be developed, tested, and deployed independently.

### 2. Extensibility
The architecture supports extension through well-defined extension points without modifying core code.

### 3. Separation of Concerns
Each layer and module has a specific responsibility, following the Single Responsibility Principle.

### 4. Scalability
The system is designed to handle increased load through horizontal and vertical scaling.

### 5. Testability
Components are designed to be easily testable in isolation.

## Architectural Layers

### Layer 1: Presentation Layer

**Components:**
- Web UI (Frontend)
- API Controllers
- Request/Response DTOs

**Responsibilities:**
- User interface rendering
- HTTP request handling
- Input validation
- Response formatting
- Session management

**Technology Stack:**
- Frontend: React/Vue.js/Angular
- API: Spring MVC / JAX-RS
- Serialization: Jackson / Gson

### Layer 2: Service Layer

**Components:**
- Service interfaces
- Service implementations
- Business logic orchestration
- Transaction management

**Responsibilities:**
- Business logic execution
- Workflow orchestration
- Transaction coordination
- Business rule validation
- Service composition

**Key Services:**
- `GrammarService`: Grammar parsing and validation
- `MappingService`: Data transformation and mapping
- `RuleService`: Rule management and evaluation
- `ExtensionService`: Plugin and extension management

### Layer 3: Domain Layer

**Components:**
- Domain models
- Domain services
- Business entities
- Value objects

**Responsibilities:**
- Core business logic
- Domain rules enforcement
- Entity relationships
- Business invariants

**Key Domains:**
- Grammar domain
- Mapping domain
- Rule domain
- Extension domain

### Layer 4: Infrastructure Layer

**Components:**
- Repository implementations
- External service clients
- Configuration management
- Logging and monitoring
- Caching

**Responsibilities:**
- Data persistence
- External integrations
- Cross-cutting concerns
- System configuration

## Core Modules

### Grammar Module

```
grammar/
├── api/
│   ├── GrammarParser.java
│   ├── GrammarValidator.java
│   └── GrammarCustomizer.java
├── model/
│   ├── Grammar.java
│   ├── GrammarRule.java
│   └── AST.java
├── impl/
│   ├── DefaultGrammarParser.java
│   └── CustomGrammarEngine.java
└── exception/
    └── GrammarException.java
```

**Purpose**: Handles grammar definition, parsing, and validation.

**Key Classes**:
- `GrammarParser`: Interface for parsing grammar definitions
- `Grammar`: Represents a complete grammar definition
- `GrammarRule`: Individual grammar rule
- `AST`: Abstract Syntax Tree representation

**Extension Points**:
- Custom grammar rule types
- Custom parser implementations
- Grammar preprocessors
- AST transformers

### Mapping Module

```
mapping/
├── api/
│   ├── MappingEngine.java
│   ├── MappingRule.java
│   └── MappingFunction.java
├── model/
│   ├── Mapping.java
│   ├── MappingContext.java
│   └── TransformationResult.java
├── impl/
│   ├── DefaultMappingEngine.java
│   └── RuleEvaluator.java
└── functions/
    ├── StringFunctions.java
    ├── DateFunctions.java
    └── CustomFunctions.java
```

**Purpose**: Manages data transformation and mapping operations.

**Key Classes**:
- `MappingEngine`: Core mapping execution engine
- `MappingRule`: Definition of a mapping rule
- `MappingFunction`: Custom transformation function
- `MappingContext`: Execution context for mappings

**Extension Points**:
- Custom mapping functions
- Custom rule types
- Transformation pipelines
- Data validators

### Extension Module

```
extensions/
├── api/
│   ├── Extension.java
│   ├── ExtensionPoint.java
│   └── ExtensionContext.java
├── loader/
│   ├── ExtensionLoader.java
│   └── ClasspathScanner.java
├── registry/
│   └── ExtensionRegistry.java
└── lifecycle/
    └── ExtensionLifecycle.java
```

**Purpose**: Plugin architecture for extending system functionality.

**Key Classes**:
- `Extension`: Base interface for all extensions
- `ExtensionPoint`: Marker for extension points
- `ExtensionLoader`: Loads extensions from classpath
- `ExtensionRegistry`: Central registry for extensions

**Extension Points**:
- Grammar extensions
- Mapping function extensions
- Validator extensions
- Output formatter extensions

## Component Interactions

### Grammar Parsing Flow

```
User/API
   │
   ├──> API Controller
   │       │
   │       ├──> GrammarService
   │       │       │
   │       │       ├──> GrammarParser
   │       │       │       │
   │       │       │       ├──> Lexer
   │       │       │       │
   │       │       │       ├──> Parser
   │       │       │       │
   │       │       │       └──> AST Builder
   │       │       │
   │       │       └──> GrammarValidator
   │       │
   │       └──> Response
   │
   └──< Result
```

### Mapping Execution Flow

```
Input Data
   │
   ├──> MappingService
   │       │
   │       ├──> Rule Loader
   │       │       │
   │       │       └──> Rule Repository
   │       │
   │       ├──> MappingEngine
   │       │       │
   │       │       ├──> Rule Evaluator
   │       │       │       │
   │       │       │       ├──> Condition Checker
   │       │       │       │
   │       │       │       └──> Function Executor
   │       │       │
   │       │       └──> Transformer
   │       │
   │       └──> Validator
   │
   └──> Output Data
```

## Data Flow

### Request Processing Pipeline

```
HTTP Request
   │
   ├──> Security Filter (Authentication/Authorization)
   │
   ├──> Logging Filter (Request Logging)
   │
   ├──> Validation Filter (Input Validation)
   │
   ├──> Controller (Request Handling)
   │       │
   │       ├──> Service Layer (Business Logic)
   │       │       │
   │       │       ├──> Domain Layer (Domain Logic)
   │       │       │
   │       │       └──> Repository Layer (Data Access)
   │       │
   │       └──> Response Transformation
   │
   ├──> Exception Handler (Error Handling)
   │
   └──> HTTP Response
```

## Deployment Architecture

### Single Server Deployment

```
┌─────────────────────────────────────┐
│         Application Server          │
│                                     │
│  ┌──────────┐      ┌─────────────┐ │
│  │ Frontend │      │  Backend    │ │
│  │  (Static)│      │  (Java)     │ │
│  └──────────┘      └─────────────┘ │
│                           │         │
│                    ┌──────▼──────┐  │
│                    │  Database   │  │
│                    └─────────────┘  │
└─────────────────────────────────────┘
```

### Distributed Deployment

```
┌──────────────┐
│ Load Balancer│
└──────┬───────┘
       │
       ├────────────┬────────────┐
       │            │            │
┌──────▼──────┐ ┌──▼──────┐ ┌───▼──────┐
│ App Server 1│ │App Srv 2│ │App Srv 3 │
└──────┬──────┘ └────┬────┘ └────┬─────┘
       │             │            │
       └─────────────┴────────────┘
                     │
            ┌────────▼────────┐
            │  Database       │
            │  (Clustered)    │
            └─────────────────┘
```

### Microservices Deployment

```
┌──────────────────┐
│  API Gateway     │
└────────┬─────────┘
         │
    ┌────┴─────┬──────────┬──────────┐
    │          │          │          │
┌───▼─────┐ ┌─▼──────┐ ┌─▼──────┐ ┌─▼──────┐
│Grammar  │ │Mapping │ │Rule    │ │Frontend│
│Service  │ │Service │ │Service │ │Service │
└────┬────┘ └───┬────┘ └───┬────┘ └────────┘
     │          │          │
     └──────────┴──────────┘
                │
         ┌──────▼──────┐
         │Service Mesh │
         └─────────────┘
```

## Security Architecture

### Authentication Flow

```
User
 │
 ├──> Login Request
 │       │
 │       └──> Authentication Service
 │               │
 │               ├──> User Repository
 │               │
 │               └──> Token Generator (JWT)
 │                       │
 │                       └──> Access Token
 │
 └──< Authenticated Session
```

### Authorization

- **Role-Based Access Control (RBAC)**: Users assigned to roles with specific permissions
- **Permission-Based**: Fine-grained permissions on resources
- **Resource-Level**: Access control at individual resource level

## Performance Considerations

### Caching Strategy

1. **Grammar Cache**: Parsed grammars cached in memory
2. **Rule Cache**: Frequently used mapping rules cached
3. **Result Cache**: Transformation results cached for repeated inputs
4. **HTTP Cache**: API responses cached at appropriate levels

### Optimization Techniques

1. **Lazy Loading**: Load resources only when needed
2. **Connection Pooling**: Reuse database connections
3. **Async Processing**: Non-blocking operations for I/O
4. **Batch Processing**: Bulk operations for multiple items
5. **Database Indexing**: Optimize query performance

## Monitoring and Observability

### Metrics

- Request rate and latency
- Error rates
- Resource utilization (CPU, memory, disk)
- Cache hit rates
- Database query performance

### Logging

- Structured logging (JSON format)
- Log levels: ERROR, WARN, INFO, DEBUG
- Correlation IDs for request tracing
- Centralized log aggregation

### Tracing

- Distributed tracing across services
- Request flow visualization
- Performance bottleneck identification

## Disaster Recovery

### Backup Strategy

- **Database Backups**: Daily full, hourly incremental
- **Configuration Backups**: Version controlled
- **Code Backups**: Git repository with multiple remotes

### High Availability

- **Load Balancing**: Distribute traffic across servers
- **Failover**: Automatic switch to backup systems
- **Replication**: Database replication for redundancy
- **Health Checks**: Continuous monitoring and alerting

---

## 中文

### 系统架构

本文档描述系统的整体架构，包括设计原则、组件交互和关键架构决策。

## 设计原则

### 1. 模块化
系统被划分为独立的、松耦合的模块，可以独立开发、测试和部署。

### 2. 可扩展性
架构通过明确定义的扩展点支持扩展，无需修改核心代码。

### 3. 关注点分离
每个层和模块都有特定的职责，遵循单一职责原则。

### 4. 可伸缩性
系统设计支持通过水平和垂直扩展处理增加的负载。

### 5. 可测试性
组件设计为可以轻松地进行隔离测试。

## 架构层次

### 第 1 层：表示层

**组件：**
- Web UI（前端）
- API 控制器
- 请求/响应 DTO

**职责：**
- 用户界面渲染
- HTTP 请求处理
- 输入验证
- 响应格式化
- 会话管理

**技术栈：**
- 前端：React/Vue.js/Angular
- API：Spring MVC / JAX-RS
- 序列化：Jackson / Gson

### 第 2 层：服务层

**组件：**
- 服务接口
- 服务实现
- 业务逻辑编排
- 事务管理

**职责：**
- 业务逻辑执行
- 工作流编排
- 事务协调
- 业务规则验证
- 服务组合

**关键服务：**
- `GrammarService`：语法解析和验证
- `MappingService`：数据转换和映射
- `RuleService`：规则管理和评估
- `ExtensionService`：插件和扩展管理

### 第 3 层：领域层

**组件：**
- 领域模型
- 领域服务
- 业务实体
- 值对象

**职责：**
- 核心业务逻辑
- 领域规则执行
- 实体关系
- 业务不变量

### 第 4 层：基础设施层

**组件：**
- 仓储实现
- 外部服务客户端
- 配置管理
- 日志和监控
- 缓存

**职责：**
- 数据持久化
- 外部集成
- 横切关注点
- 系统配置

## 核心模块

详细的模块结构和职责请参见英文版本。

## 组件交互

系统组件之间的交互流程请参见英文版本的流程图。

## 部署架构

### 单服务器部署

适用于小型应用或开发环境。

### 分布式部署

适用于中等规模应用，提供负载均衡和冗余。

### 微服务部署

适用于大规模应用，每个服务独立部署和扩展。

## 安全架构

- **认证**：基于 JWT 的令牌认证
- **授权**：基于角色的访问控制（RBAC）
- **加密**：传输层加密（TLS）和敏感数据加密

## 性能考虑

### 缓存策略

1. 语法缓存
2. 规则缓存
3. 结果缓存
4. HTTP 缓存

### 优化技术

1. 延迟加载
2. 连接池
3. 异步处理
4. 批处理
5. 数据库索引

## 监控和可观察性

- **指标**：请求率、延迟、错误率、资源使用率
- **日志**：结构化日志、集中式日志聚合
- **追踪**：分布式追踪、请求流可视化

## 灾难恢复

- **备份策略**：数据库每日完整备份，每小时增量备份
- **高可用性**：负载均衡、故障转移、复制、健康检查
