# API Documentation

[English](#english) | [中文](#中文)

---

## English

## Overview

This document provides comprehensive API documentation for the RESTful API endpoints exposed by the system. All endpoints follow REST principles and return JSON responses.

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

Most API endpoints require authentication. Include the JWT token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

### Common Response Formats

#### Success Response

```json
{
  "status": "success",
  "data": {
    // response data
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### Error Response

```json
{
  "status": "error",
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": []
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Status Codes

- `200 OK`: Successful request
- `201 Created`: Resource successfully created
- `204 No Content`: Successful request with no response body
- `400 Bad Request`: Invalid request parameters
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict
- `500 Internal Server Error`: Server error

## Grammar API

### Parse Grammar

Parse a grammar definition and return an AST.

**Endpoint**: `POST /api/v1/grammar/parse`

**Request Body**:

```json
{
  "grammar": "rule: 'example';",
  "options": {
    "validateOnly": false,
    "includeDebugInfo": false
  }
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "ast": {
      "type": "Grammar",
      "rules": [
        {
          "name": "rule",
          "value": "example"
        }
      ]
    },
    "metadata": {
      "parseTime": 15,
      "ruleCount": 1
    }
  }
}
```

**cURL Example**:

```bash
curl -X POST http://localhost:8080/api/v1/grammar/parse \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "grammar": "rule: '\''example'\'';",
    "options": {
      "validateOnly": false
    }
  }'
```

### Validate Grammar

Validate a grammar definition without generating AST.

**Endpoint**: `POST /api/v1/grammar/validate`

**Request Body**:

```json
{
  "grammar": "rule: 'example';"
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "valid": true,
    "errors": [],
    "warnings": []
  }
}
```

### Get Grammar Definition

Retrieve a saved grammar definition by ID.

**Endpoint**: `GET /api/v1/grammar/{grammarId}`

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "grammar-123",
    "name": "Example Grammar",
    "definition": "rule: 'example';",
    "createdAt": "2024-01-01T12:00:00Z",
    "updatedAt": "2024-01-01T12:00:00Z"
  }
}
```

### List Grammars

List all available grammar definitions.

**Endpoint**: `GET /api/v1/grammar`

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: createdAt)

**Response**:

```json
{
  "status": "success",
  "data": {
    "content": [
      {
        "id": "grammar-123",
        "name": "Example Grammar",
        "description": "An example grammar"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Create Grammar

Create a new grammar definition.

**Endpoint**: `POST /api/v1/grammar`

**Request Body**:

```json
{
  "name": "New Grammar",
  "description": "A new grammar definition",
  "definition": "rule: 'example';"
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "grammar-456",
    "name": "New Grammar",
    "definition": "rule: 'example';",
    "createdAt": "2024-01-01T12:00:00Z"
  }
}
```

### Update Grammar

Update an existing grammar definition.

**Endpoint**: `PUT /api/v1/grammar/{grammarId}`

**Request Body**:

```json
{
  "name": "Updated Grammar",
  "description": "Updated description",
  "definition": "rule: 'updated';"
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "grammar-123",
    "name": "Updated Grammar",
    "updatedAt": "2024-01-01T13:00:00Z"
  }
}
```

### Delete Grammar

Delete a grammar definition.

**Endpoint**: `DELETE /api/v1/grammar/{grammarId}`

**Response**:

```json
{
  "status": "success",
  "data": {
    "message": "Grammar deleted successfully"
  }
}
```

## Mapping API

### Execute Mapping

Execute a mapping rule on input data.

**Endpoint**: `POST /api/v1/mapping/execute`

**Request Body**:

```json
{
  "ruleId": "rule-123",
  "inputData": {
    "field1": "value1",
    "field2": 123
  },
  "context": {
    "userId": "user-456"
  }
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "outputData": {
      "mappedField1": "VALUE1",
      "mappedField2": 123
    },
    "executionTime": 25,
    "appliedRules": ["rule-123"]
  }
}
```

**cURL Example**:

```bash
curl -X POST http://localhost:8080/api/v1/mapping/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "ruleId": "rule-123",
    "inputData": {
      "field1": "value1"
    }
  }'
```

### Create Mapping Rule

Create a new mapping rule.

**Endpoint**: `POST /api/v1/mapping/rules`

**Request Body**:

```json
{
  "name": "Example Mapping",
  "description": "Maps input to output",
  "rules": [
    {
      "source": "field1",
      "target": "mappedField1",
      "transformation": "UPPERCASE"
    }
  ]
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "rule-789",
    "name": "Example Mapping",
    "createdAt": "2024-01-01T12:00:00Z"
  }
}
```

### Get Mapping Rule

Retrieve a mapping rule by ID.

**Endpoint**: `GET /api/v1/mapping/rules/{ruleId}`

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "rule-123",
    "name": "Example Mapping",
    "description": "Maps input to output",
    "rules": [
      {
        "source": "field1",
        "target": "mappedField1",
        "transformation": "UPPERCASE"
      }
    ],
    "createdAt": "2024-01-01T12:00:00Z"
  }
}
```

### List Mapping Rules

List all mapping rules.

**Endpoint**: `GET /api/v1/mapping/rules`

**Query Parameters**:
- `page` (optional): Page number
- `size` (optional): Page size
- `search` (optional): Search term

**Response**:

```json
{
  "status": "success",
  "data": {
    "content": [
      {
        "id": "rule-123",
        "name": "Example Mapping",
        "description": "Maps input to output"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1
  }
}
```

### Update Mapping Rule

Update an existing mapping rule.

**Endpoint**: `PUT /api/v1/mapping/rules/{ruleId}`

**Request Body**:

```json
{
  "name": "Updated Mapping",
  "rules": [
    {
      "source": "field1",
      "target": "newField",
      "transformation": "LOWERCASE"
    }
  ]
}
```

### Delete Mapping Rule

Delete a mapping rule.

**Endpoint**: `DELETE /api/v1/mapping/rules/{ruleId}`

**Response**:

```json
{
  "status": "success",
  "data": {
    "message": "Mapping rule deleted successfully"
  }
}
```

## Extension API

### List Extensions

List all loaded extensions.

**Endpoint**: `GET /api/v1/extensions`

**Response**:

```json
{
  "status": "success",
  "data": {
    "extensions": [
      {
        "id": "ext-grammar-custom",
        "name": "Custom Grammar Extension",
        "version": "1.0.0",
        "status": "active"
      }
    ]
  }
}
```

### Get Extension Info

Get detailed information about an extension.

**Endpoint**: `GET /api/v1/extensions/{extensionId}`

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "ext-grammar-custom",
    "name": "Custom Grammar Extension",
    "version": "1.0.0",
    "description": "Adds custom grammar rules",
    "author": "Extension Developer",
    "extensionPoints": ["GrammarRule", "Parser"],
    "status": "active"
  }
}
```

## Webhook API

### Register Webhook

Register a webhook for event notifications.

**Endpoint**: `POST /api/v1/webhooks`

**Request Body**:

```json
{
  "url": "https://example.com/webhook",
  "events": ["grammar.created", "mapping.executed"],
  "secret": "webhook-secret"
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "id": "webhook-123",
    "url": "https://example.com/webhook",
    "createdAt": "2024-01-01T12:00:00Z"
  }
}
```

## Authentication API

### Login

Authenticate and receive JWT token.

**Endpoint**: `POST /api/v1/auth/login`

**Request Body**:

```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  }
}
```

### Refresh Token

Refresh an expired access token.

**Endpoint**: `POST /api/v1/auth/refresh`

**Request Body**:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response**:

```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600
  }
}
```

## Rate Limiting

API requests are rate-limited to prevent abuse:

- **Authenticated users**: 1000 requests per hour
- **Unauthenticated users**: 100 requests per hour

Rate limit headers are included in responses:

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1609459200
```

## Pagination

List endpoints support pagination:

**Query Parameters**:
- `page`: Page number (0-indexed)
- `size`: Items per page (max 100)
- `sort`: Sort field and direction (e.g., `createdAt,desc`)

**Response Headers**:
```http
X-Total-Count: 150
X-Page-Number: 0
X-Page-Size: 20
X-Total-Pages: 8
```

## Error Codes

| Code | Description |
|------|-------------|
| `INVALID_REQUEST` | Request validation failed |
| `AUTHENTICATION_FAILED` | Invalid credentials |
| `UNAUTHORIZED` | Authentication required |
| `FORBIDDEN` | Insufficient permissions |
| `NOT_FOUND` | Resource not found |
| `CONFLICT` | Resource already exists |
| `VALIDATION_ERROR` | Data validation failed |
| `GRAMMAR_PARSE_ERROR` | Grammar parsing failed |
| `MAPPING_EXECUTION_ERROR` | Mapping execution failed |
| `INTERNAL_ERROR` | Internal server error |

---

## 中文

## 概述

本文档提供系统公开的 RESTful API 端点的全面文档。所有端点遵循 REST 原则并返回 JSON 响应。

### 基础 URL

```
http://localhost:8080/api/v1
```

### 认证

大多数 API 端点需要认证。在 Authorization 头中包含 JWT 令牌：

```http
Authorization: Bearer <your-jwt-token>
```

### 通用响应格式

#### 成功响应

```json
{
  "status": "success",
  "data": {
    // 响应数据
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### 错误响应

```json
{
  "status": "error",
  "error": {
    "code": "ERROR_CODE",
    "message": "人类可读的错误消息",
    "details": []
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 状态码

- `200 OK`: 请求成功
- `201 Created`: 资源创建成功
- `204 No Content`: 请求成功但无响应体
- `400 Bad Request`: 无效的请求参数
- `401 Unauthorized`: 需要认证
- `403 Forbidden`: 权限不足
- `404 Not Found`: 资源未找到
- `409 Conflict`: 资源冲突
- `500 Internal Server Error`: 服务器错误

## 语法 API

### 解析语法

解析语法定义并返回 AST。

**端点**: `POST /api/v1/grammar/parse`

详细的 API 使用示例请参见英文版本。

## 映射 API

### 执行映射

在输入数据上执行映射规则。

**端点**: `POST /api/v1/mapping/execute`

详细的 API 使用示例请参见英文版本。

## 速率限制

API 请求受速率限制以防止滥用：

- **已认证用户**: 每小时 1000 个请求
- **未认证用户**: 每小时 100 个请求

## 分页

列表端点支持分页：

**查询参数**:
- `page`: 页码（从 0 开始）
- `size`: 每页项目数（最大 100）
- `sort`: 排序字段和方向（例如：`createdAt,desc`）

## 错误代码

详细的错误代码列表请参见英文版本。
