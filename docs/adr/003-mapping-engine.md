# ADR-003: Mapping Rule Engine Architecture

## Status

Accepted

## Date

2024-01-01

## Context

We need a flexible and powerful system for transforming data between different formats and structures. The system must support:

- Field-to-field mapping
- Data transformations (uppercase, date formatting, etc.)
- Conditional mapping based on data values
- Nested object mapping
- Array/collection mapping
- Custom transformation functions
- Rule validation
- Easy configuration by non-developers

The system will be used for:
- API to database mapping
- Data format conversions
- ETL operations
- Integration between systems

Key requirements:
- Declarative rule definition
- Extensible transformation functions
- Performance for bulk operations
- Clear error handling
- Version control friendly
- Testable rules

## Decision

We will implement a **declarative YAML-based mapping rule engine** with the following architecture:

### Rule Definition Format

```yaml
mappings:
  - name: mapping_name
    description: Human-readable description
    rules:
      - source: source_field
        target: target_field
        transform: transformation_function
        condition: optional_condition
        default: optional_default_value
```

### Core Components

1. **Mapping Engine**: Executes mapping rules
   - Rule loader and validator
   - Rule execution engine
   - Context management
   - Error handling and recovery

2. **Function Registry**: Manages transformation functions
   - Built-in functions (string, numeric, date, collection)
   - Custom function registration
   - Function validation

3. **Rule Evaluator**: Evaluates conditions
   - Condition parser
   - Expression evaluator
   - Logical operators (AND, OR, NOT)

4. **Type System**: Handles data types
   - Type detection and coercion
   - Validation
   - Null handling

### Execution Flow

```
Input Data
    ↓
Rule Loader (YAML → Rule Objects)
    ↓
Validation (Validate rules)
    ↓
Execution Engine (Apply rules)
    ↓
    ├→ Condition Evaluation
    ├→ Function Execution
    ├→ Type Conversion
    └→ Error Handling
    ↓
Output Data
```

## Consequences

### Positive

- **Declarative**: Rules are data, not code - easier to understand and modify
- **Version Control**: YAML format works well with Git
- **Non-programmer Friendly**: Domain experts can write mapping rules
- **Flexible**: Supports simple and complex mappings
- **Extensible**: Custom functions can be added via plugins
- **Testable**: Rules can be tested independently
- **Portable**: YAML rules can be exported/imported between environments
- **Documentation**: Rules serve as documentation of data transformations
- **IDE Support**: YAML editors provide syntax highlighting and validation
- **Composable**: Rules can reference other rules
- **Performance**: Rules are compiled once, executed many times

### Negative

- **YAML Limitations**: Complex logic can be difficult to express in YAML
- **Debugging**: Harder to debug than code (need special tooling)
- **Type Safety**: No compile-time checking (mitigated by validation)
- **Performance Overhead**: Interpretation overhead vs compiled code
- **Complexity**: Feature-rich rule engine is complex to implement
- **Learning Curve**: Users must learn YAML syntax and rule DSL

### Neutral

- **Rule Versioning**: Need strategy for versioning rules (can be managed with version control)
- **Migration**: Need tools to migrate rules when format changes
- **Testing**: Need test framework specifically for rules

## Alternatives Considered

### Alternative 1: Programmatic Java API Only

```java
Mapping.builder()
    .map("firstName").to("first_name").transform(uppercase())
    .map("lastName").to("last_name")
    .build();
```

**Pros:**
- Type-safe
- IDE support
- Refactoring tools work
- Compile-time checking

**Cons:**
- Requires developer to change mappings
- Not accessible to non-programmers
- Changes require recompilation and deployment
- Poor for version control diffs

**Why not chosen:**
- Need non-developers to define mappings
- Want hot-reload capability
- Prefer declarative over programmatic

### Alternative 2: JavaScript/Expression Language

```javascript
{
  "firstName": "$.user.name.first.toUpperCase()",
  "age": "new Date().getFullYear() - $.user.birthYear"
}
```

**Pros:**
- Powerful and flexible
- Familiar to many developers
- Can express complex logic

**Cons:**
- Security concerns (code injection)
- Difficult to validate
- Performance overhead
- Steeper learning curve
- Hard to debug

**Why not chosen:**
- Security risks with user-provided code
- Too much power leads to maintenance issues
- Prefer constrained, validated rules

### Alternative 3: Visual Mapping Tool

Drag-and-drop GUI for creating mappings.

**Pros:**
- Very user-friendly
- Visual representation
- No syntax to learn

**Cons:**
- Difficult to version control
- Hard to code review
- Not text-based
- Tool lock-in
- Complex to implement

**Why not chosen:**
- Poor for version control and code review
- Want text-based format for Git workflow
- Can add visual tool later on top of YAML

### Alternative 4: XSLT

```xml
<xsl:template match="user">
  <person>
    <name><xsl:value-of select="firstName"/></name>
  </person>
</xsl:template>
```

**Pros:**
- Standard for XML transformations
- Mature and proven
- Tool support exists

**Cons:**
- XML-specific
- Verbose
- Steep learning curve
- Complex syntax
- Declining popularity

**Why not chosen:**
- Not limited to XML
- Too complex and verbose
- Modern alternatives are better

## Implementation Strategy

### Phase 1: Core Engine
- Rule parser and validator
- Basic transformations (string, numeric)
- Simple field mapping

### Phase 2: Advanced Features
- Conditional mapping
- Nested object support
- Array operations
- Error handling

### Phase 3: Extensions
- Custom function plugins
- Complex expressions
- Performance optimizations

### Phase 4: Tooling
- Rule validator CLI
- Testing framework
- Visual editor (optional)

## Related Decisions

- [ADR-002: Grammar Definition DSL](002-grammar-dsl.md) - Similar declarative approach
- [ADR-004: Extension Plugin System](004-extension-system.md) - Custom functions via extensions
- [ADR-007: Database Choice](007-database-choice.md) - Storage for mapping rules

## References

- [JSONPath Specification](https://goessner.net/articles/JsonPath/)
- [YAML Specification](https://yaml.org/spec/)
- [Apache Camel Mapping](https://camel.apache.org/)
- [Spring Integration Transformers](https://docs.spring.io/spring-integration/reference/html/message-transformation.html)
