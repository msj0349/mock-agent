# Project Name

[English](#english) | [中文](#中文)

---

## English

### Project Overview

This project is a comprehensive Java-based application designed for grammar parsing, transformation, and mapping operations. It provides a flexible architecture with extensible grammar customization, configurable mapping rules, and a modern frontend interface for user interaction.

**Key Features:**
- Customizable grammar definitions and parsing engine
- Flexible mapping rule system for data transformation
- Extensible plugin architecture with well-defined extension points
- RESTful API for programmatic access
- Modern web-based frontend interface
- Comprehensive testing framework
- Multi-language support (English and Chinese)

### Architecture Overview

The project follows a modular, layered architecture:

```
┌─────────────────────────────────────────────┐
│           Frontend Layer                     │
│   (Web UI, API Client)                      │
└──────────────────┬──────────────────────────┘
                   │ HTTP/REST
┌──────────────────▼──────────────────────────┐
│           API Layer                          │
│   (REST Controllers, Request Handlers)       │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         Service Layer                        │
│   (Business Logic, Orchestration)            │
└──────┬─────────────────────┬────────────────┘
       │                     │
┌──────▼────────┐   ┌───────▼─────────────────┐
│  Grammar      │   │   Mapping Engine        │
│  Engine       │   │   (Rules, Transformers) │
└───────────────┘   └─────────────────────────┘
       │                     │
┌──────▼─────────────────────▼────────────────┐
│         Core Utilities & Infrastructure      │
│   (Logging, Config, Persistence)            │
└─────────────────────────────────────────────┘
```

### Module Descriptions

#### 1. Core Module (`core/`)
The foundation module containing shared utilities, base classes, and core interfaces.

**Responsibilities:**
- Configuration management
- Logging infrastructure
- Exception handling
- Common utilities and helpers

#### 2. Grammar Module (`grammar/`)
Handles grammar definition, parsing, and validation.

**Responsibilities:**
- Grammar definition DSL
- Parser implementation
- AST (Abstract Syntax Tree) generation
- Syntax validation
- Grammar customization API

#### 3. Mapping Module (`mapping/`)
Manages data transformation and mapping operations.

**Responsibilities:**
- Mapping rule definition and storage
- Rule evaluation engine
- Data transformation pipeline
- Custom mapping function support

#### 4. API Module (`api/`)
Provides RESTful API endpoints for external access.

**Responsibilities:**
- REST controllers
- Request/response handling
- API documentation (OpenAPI/Swagger)
- Authentication and authorization

#### 5. Frontend Module (`frontend/`)
Web-based user interface for interacting with the system.

**Responsibilities:**
- User interface components
- API integration
- State management
- User experience and workflows

#### 6. Extension Module (`extensions/`)
Plugin architecture for extending system functionality.

**Responsibilities:**
- Extension point definitions
- Plugin loading and lifecycle management
- Extension registry
- Custom extension implementations

### Prerequisites

- **Java**: JDK 11 or higher
- **Maven**: 3.6+ or **Gradle**: 7.0+ (depending on build tool)
- **Node.js**: 16+ (for frontend development)
- **npm** or **yarn**: Latest stable version

### Build Instructions

#### Backend (Java)

Using Maven:
```bash
# Clean and build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl core,grammar
```

Using Gradle:
```bash
# Clean and build
./gradlew clean build

# Skip tests
./gradlew clean build -x test

# Build specific module
./gradlew :core:build :grammar:build
```

#### Frontend

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Build for production
npm run build

# Build for development
npm run build:dev
```

### Run Instructions

#### Running the Backend

Using Maven:
```bash
# Run the application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Using Gradle:
```bash
# Run the application
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Using JAR:
```bash
# After building, run the JAR
java -jar target/project-name-1.0.0.jar

# With specific profile
java -jar target/project-name-1.0.0.jar --spring.profiles.active=prod
```

#### Running the Frontend

```bash
# Development server with hot reload
cd frontend
npm run dev

# Production server
npm run start
```

#### Running with Docker

```bash
# Build images
docker-compose build

# Start all services
docker-compose up

# Start in detached mode
docker-compose up -d

# Stop services
docker-compose down
```

### Testing

#### Backend Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=GrammarParserTest

# Run integration tests
mvn verify

# Generate test coverage report
mvn test jacoco:report
```

#### Frontend Tests

```bash
cd frontend

# Run unit tests
npm run test

# Run tests with coverage
npm run test:coverage

# Run e2e tests
npm run test:e2e
```

### Configuration

Configuration files are located in `src/main/resources/`:

- `application.yml` - Main application configuration
- `application-dev.yml` - Development environment settings
- `application-prod.yml` - Production environment settings
- `grammar-config.yml` - Grammar engine configuration
- `mapping-rules.yml` - Default mapping rules

Key configuration properties:

```yaml
# Server configuration
server:
  port: 8080
  
# Grammar engine settings
grammar:
  cache-enabled: true
  max-parse-depth: 100
  
# Mapping engine settings
mapping:
  rule-directory: ./rules
  enable-custom-functions: true
```

### API Documentation

Once the application is running, API documentation is available at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

See [docs/API.md](docs/API.md) for detailed API usage examples.

### Extension Points

The system provides several extension points for customization:

1. **Custom Grammar Rules**: Extend `GrammarRule` interface
2. **Mapping Functions**: Implement `MappingFunction` interface
3. **Data Validators**: Extend `Validator` abstract class
4. **Output Formatters**: Implement `OutputFormatter` interface
5. **Authentication Providers**: Implement `AuthenticationProvider` interface

See [docs/EXTENSIONS.md](docs/EXTENSIONS.md) for detailed extension development guide.

### Contributing

We welcome contributions! Please see our [CONTRIBUTING.md](CONTRIBUTING.md) for details on:

- Code of conduct
- Development workflow
- Coding standards
- Pull request process
- Testing requirements

### License

[Specify your license here, e.g., MIT, Apache 2.0, etc.]

### Support

- **Documentation**: [docs/](docs/)
- **Issue Tracker**: [GitHub Issues](https://github.com/your-org/your-repo/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/your-repo/discussions)

---

## 中文

### 项目概述

本项目是一个基于 Java 的综合性应用程序，专为语法解析、转换和映射操作而设计。它提供了灵活的架构，具有可扩展的语法自定义、可配置的映射规则以及用于用户交互的现代前端界面。

**核心特性：**
- 可自定义的语法定义和解析引擎
- 灵活的映射规则系统用于数据转换
- 具有明确扩展点的可扩展插件架构
- 用于程序化访问的 RESTful API
- 现代化的 Web 前端界面
- 全面的测试框架
- 多语言支持（英文和中文）

### 架构概览

项目遵循模块化分层架构：

```
┌─────────────────────────────────────────────┐
│           前端层                             │
│   (Web UI, API 客户端)                      │
└──────────────────┬──────────────────────────┘
                   │ HTTP/REST
┌──────────────────▼──────────────────────────┐
│           API 层                             │
│   (REST 控制器, 请求处理器)                  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         服务层                               │
│   (业务逻辑, 编排)                           │
└──────┬─────────────────────┬────────────────┘
       │                     │
┌──────▼────────┐   ┌───────▼─────────────────┐
│  语法引擎      │   │   映射引擎              │
│               │   │   (规则, 转换器)        │
└───────────────┘   └─────────────────────────┘
       │                     │
┌──────▼─────────────────────▼────────────────┐
│      核心工具和基础设施                      │
│   (日志, 配置, 持久化)                       │
└─────────────────────────────────────────────┘
```

### 模块说明

#### 1. 核心模块 (`core/`)
包含共享工具、基类和核心接口的基础模块。

**职责：**
- 配置管理
- 日志基础设施
- 异常处理
- 通用工具和辅助函数

#### 2. 语法模块 (`grammar/`)
处理语法定义、解析和验证。

**职责：**
- 语法定义 DSL
- 解析器实现
- AST（抽象语法树）生成
- 语法验证
- 语法自定义 API

#### 3. 映射模块 (`mapping/`)
管理数据转换和映射操作。

**职责：**
- 映射规则定义和存储
- 规则评估引擎
- 数据转换管道
- 自定义映射函数支持

#### 4. API 模块 (`api/`)
提供外部访问的 RESTful API 端点。

**职责：**
- REST 控制器
- 请求/响应处理
- API 文档（OpenAPI/Swagger）
- 身份验证和授权

#### 5. 前端模块 (`frontend/`)
基于 Web 的用户界面，用于与系统交互。

**职责：**
- 用户界面组件
- API 集成
- 状态管理
- 用户体验和工作流

#### 6. 扩展模块 (`extensions/`)
用于扩展系统功能的插件架构。

**职责：**
- 扩展点定义
- 插件加载和生命周期管理
- 扩展注册表
- 自定义扩展实现

### 系统要求

- **Java**: JDK 11 或更高版本
- **Maven**: 3.6+ 或 **Gradle**: 7.0+（取决于构建工具）
- **Node.js**: 16+（用于前端开发）
- **npm** 或 **yarn**: 最新稳定版本

### 构建说明

#### 后端（Java）

使用 Maven：
```bash
# 清理并构建
mvn clean install

# 跳过测试
mvn clean install -DskipTests

# 构建特定模块
mvn clean install -pl core,grammar
```

使用 Gradle：
```bash
# 清理并构建
./gradlew clean build

# 跳过测试
./gradlew clean build -x test

# 构建特定模块
./gradlew :core:build :grammar:build
```

#### 前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 生产环境构建
npm run build

# 开发环境构建
npm run build:dev
```

### 运行说明

#### 运行后端

使用 Maven：
```bash
# 运行应用程序
mvn spring-boot:run

# 使用特定配置文件运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

使用 Gradle：
```bash
# 运行应用程序
./gradlew bootRun

# 使用特定配置文件运行
./gradlew bootRun --args='--spring.profiles.active=dev'
```

使用 JAR：
```bash
# 构建后，运行 JAR
java -jar target/project-name-1.0.0.jar

# 使用特定配置文件
java -jar target/project-name-1.0.0.jar --spring.profiles.active=prod
```

#### 运行前端

```bash
# 带热重载的开发服务器
cd frontend
npm run dev

# 生产服务器
npm run start
```

#### 使用 Docker 运行

```bash
# 构建镜像
docker-compose build

# 启动所有服务
docker-compose up

# 以分离模式启动
docker-compose up -d

# 停止服务
docker-compose down
```

### 测试

#### 后端测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=GrammarParserTest

# 运行集成测试
mvn verify

# 生成测试覆盖率报告
mvn test jacoco:report
```

#### 前端测试

```bash
cd frontend

# 运行单元测试
npm run test

# 运行带覆盖率的测试
npm run test:coverage

# 运行端到端测试
npm run test:e2e
```

### 贡献指南

我们欢迎贡献！请查看我们的 [CONTRIBUTING.md](CONTRIBUTING.md) 了解以下详情：

- 行为准则
- 开发工作流
- 编码标准
- 拉取请求流程
- 测试要求

### 许可证

[在此指定您的许可证，例如 MIT、Apache 2.0 等]

### 支持

- **文档**: [docs/](docs/)
- **问题跟踪**: [GitHub Issues](https://github.com/your-org/your-repo/issues)
- **讨论**: [GitHub Discussions](https://github.com/your-org/your-repo/discussions)
