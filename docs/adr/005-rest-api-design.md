# ADR-005: REST API Design

## Status

Accepted

## Date

2024-01-01

## Context

We need to design a REST API that provides programmatic access to the grammar parsing and mapping functionality. The API must be:

- RESTful and follow HTTP standards
- Easy to use and well-documented
- Secure and properly authenticated
- Versioned for backwards compatibility
- Performant and scalable
- Consistent in design and error handling

The API will be used by:
- Frontend web application
- Third-party integrations
- CLI tools
- Mobile applications (future)

## Decision

We will implement a **RESTful API** following these principles and standards:

### 1. URL Structure

```
/api/v1/{resource}/{id}/{sub-resource}
```

Examples:
- `GET /api/v1/grammar` - List grammars
- `GET /api/v1/grammar/{id}` - Get specific grammar
- `POST /api/v1/grammar/parse` - Parse grammar
- `POST /api/v1/mapping/execute` - Execute mapping

### 2. HTTP Methods

- `GET`: Retrieve resources (idempotent)
- `POST`: Create resources or non-idempotent operations
- `PUT`: Update entire resource (idempotent)
- `PATCH`: Partial update
- `DELETE`: Remove resource (idempotent)

### 3. Response Format

Consistent JSON structure:

```json
{
  "status": "success" | "error",
  "data": { /* response data */ },
  "error": { /* error details */ },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 4. Status Codes

- `200 OK`: Successful GET, PUT, PATCH, DELETE
- `201 Created`: Successful POST creating resource
- `204 No Content`: Successful DELETE with no response body
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict
- `422 Unprocessable Entity`: Validation failed
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error

### 5. Error Format

```json
{
  "status": "error",
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable message",
    "details": [
      {
        "field": "fieldName",
        "message": "Field-specific error"
      }
    ]
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 6. Authentication

- JWT (JSON Web Tokens) for authentication
- Bearer token in Authorization header
- Refresh token for token renewal

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### 7. Versioning

- URL path versioning: `/api/v1/`, `/api/v2/`
- Major version in URL
- Maintain backwards compatibility within major version
- Deprecation warnings in response headers

### 8. Pagination

Query parameters for list endpoints:
- `page`: Page number (0-indexed)
- `size`: Items per page
- `sort`: Sort field and direction

Response includes pagination metadata:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

### 9. Filtering and Search

Query parameters:
- `filter[field]=value`: Filter by field
- `search=term`: Full-text search
- `q=query`: Simple query

### 10. Rate Limiting

- Rate limits enforced
- Headers in response:
  ```http
  X-RateLimit-Limit: 1000
  X-RateLimit-Remaining: 999
  X-RateLimit-Reset: 1609459200
  ```

### 11. API Documentation

- OpenAPI/Swagger specification
- Interactive documentation at `/swagger-ui.html`
- Spec available at `/api-docs`

## Consequences

### Positive

- **Standard**: Follows REST principles and HTTP standards
- **Predictable**: Consistent patterns across all endpoints
- **Developer-Friendly**: Easy to understand and use
- **Self-Documenting**: RESTful URLs are intuitive
- **Cacheable**: GET requests can be cached
- **Versioned**: Can evolve without breaking clients
- **Secure**: JWT provides stateless authentication
- **Documented**: OpenAPI spec enables code generation
- **Testable**: Standard HTTP makes testing easy
- **Tooling**: Works with standard HTTP clients

### Negative

- **Over/Under-fetching**: REST can require multiple requests
- **Versioning Complexity**: Maintaining multiple versions
- **Performance**: Multiple round trips for related data
- **Rigid Structure**: Resource-based structure not always natural
- **Documentation Maintenance**: OpenAPI spec must be kept updated

### Neutral

- **Not Optimal for All Cases**: Some operations don't map well to REST
- **Learning Curve**: Developers must understand REST principles

## Alternatives Considered

### Alternative 1: GraphQL

**Pros:**
- Client specifies exact data needed
- Single endpoint
- No over/under-fetching
- Strong typing
- Real-time subscriptions

**Cons:**
- More complex to implement
- Caching more difficult
- Security considerations (query complexity)
- Steeper learning curve
- Tooling less mature than REST

**Why not chosen:**
- REST sufficient for requirements
- Simpler to implement and maintain
- Better caching with standard HTTP
- More mature tooling ecosystem
- Can add GraphQL later if needed

### Alternative 2: gRPC

**Pros:**
- High performance (binary protocol)
- Strong typing (Protocol Buffers)
- Bi-directional streaming
- Code generation

**Cons:**
- Not browser-friendly (requires proxy)
- Binary protocol harder to debug
- Less human-readable
- Limited language support
- Steeper learning curve

**Why not chosen:**
- Need browser compatibility
- REST performance sufficient
- Want human-readable API
- Debugging simplicity important

### Alternative 3: SOAP

**Pros:**
- Formal contract (WSDL)
- Strong typing
- Enterprise features
- Mature tooling

**Cons:**
- Very verbose (XML)
- Complex
- Declining popularity
- Poor developer experience
- Heavyweight

**Why not chosen:**
- Outdated technology
- Too complex and verbose
- Modern alternatives better
- Poor developer experience

### Alternative 4: JSON-RPC

**Pros:**
- Simple protocol
- Lightweight
- Easy to implement
- Language agnostic

**Cons:**
- Less standardized than REST
- No HTTP semantics
- Poor caching
- Limited tooling
- Not self-documenting

**Why not chosen:**
- Less standard than REST
- Loses HTTP benefits
- Less tooling support
- REST more familiar to developers

## Implementation Guidelines

### Endpoint Design Checklist

- [ ] Follow naming conventions
- [ ] Use appropriate HTTP methods
- [ ] Return correct status codes
- [ ] Include pagination for lists
- [ ] Implement filtering/search
- [ ] Add authentication where needed
- [ ] Document in OpenAPI spec
- [ ] Add validation
- [ ] Include error handling
- [ ] Add rate limiting
- [ ] Write integration tests

### Security Best Practices

- Validate all input
- Use HTTPS only
- Implement rate limiting
- Sanitize error messages (no stack traces)
- Use proper authentication
- Implement CORS correctly
- Audit trail for sensitive operations

### Performance Considerations

- Implement caching where appropriate
- Use pagination for large datasets
- Add database indexes
- Optimize queries (N+1 problem)
- Use async processing for long operations
- Monitor and log performance

## Related Decisions

- [ADR-001: Use Java for Backend Development](001-java-backend.md) - Spring MVC for REST API
- [ADR-010: Security and Authentication](010-security-authentication.md) - JWT authentication
- [ADR-006: Frontend Framework Selection](006-frontend-framework.md) - Frontend consumes REST API

## References

- [REST API Design Rulebook](https://www.oreilly.com/library/view/rest-api-design/9781449317904/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [HTTP Status Codes](https://httpstatuses.com/)
- [RESTful Web Services (book)](https://www.oreilly.com/library/view/restful-web-services/9780596529260/)
