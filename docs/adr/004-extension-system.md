# ADR-004: Extension Plugin System

## Status

Accepted

## Date

2024-01-01

## Context

We need a way to extend the system's functionality without modifying core code. Users should be able to:

- Add custom grammar rules
- Implement custom mapping functions
- Add custom validators
- Implement custom output formatters
- Extend API functionality

Requirements:
- Clean extension points
- Type-safe extension API
- Hot-loading capability (optional)
- Extension isolation
- Dependency management
- Version compatibility

The system should be extensible but maintain stability and security.

## Decision

We will implement a **Java-based plugin system** with the following design:

### Extension Architecture

1. **Extension API**: Core interfaces that extensions implement
   ```java
   public interface Extension {
       void initialize(ExtensionContext context);
       void shutdown(ExtensionContext context);
   }
   ```

2. **Extension Points**: Specific extension types
   - `GrammarRule`: Custom grammar rules
   - `MappingFunction`: Custom transformation functions
   - `Validator`: Custom validators
   - `OutputFormatter`: Custom formatters

3. **Extension Discovery**: 
   - Classpath scanning
   - Service Provider Interface (SPI)
   - Manual registration
   - Extension directory scanning

4. **Extension Descriptor**: YAML metadata
   ```yaml
   extension:
     id: my-extension
     name: My Extension
     version: 1.0.0
     requires:
       minCoreVersion: 1.0.0
     provides:
       grammarRules: [...]
       mappingFunctions: [...]
   ```

5. **Extension Context**: API for extensions
   ```java
   public interface ExtensionContext {
       Configuration getConfiguration();
       <T> T getService(Class<T> serviceClass);
       void registerComponent(String name, Object component);
       Logger getLogger();
   }
   ```

### Loading Mechanism

1. **Startup Loading**: Extensions loaded at application startup
2. **Classpath Discovery**: Scan classpath for extension descriptors
3. **Validation**: Verify compatibility and dependencies
4. **Initialization**: Call extension initialize() methods
5. **Registration**: Register extension components with core

### Isolation

- Extensions run in same JVM (for simplicity)
- No class loader isolation (initially)
- Security through API boundaries
- Extensions cannot access core internals

## Consequences

### Positive

- **Extensibility**: Add functionality without modifying core
- **Type Safety**: Compile-time checking of extension code
- **Performance**: Native Java performance, no overhead
- **Tooling**: Full IDE support for extension development
- **Debugging**: Easy to debug extensions
- **Distribution**: Extensions distributed as JAR files
- **Dependency Management**: Maven/Gradle for dependencies
- **Version Control**: Extensions versioned independently
- **Testability**: Extensions easily unit tested
- **Discovery**: Automatic discovery via classpath scanning

### Negative

- **No Isolation**: Extensions share JVM, can affect stability
- **Version Conflicts**: Dependency conflicts possible
- **Breaking Changes**: Core API changes can break extensions
- **Security**: Extensions have significant access
- **Hot Reload**: Difficult to implement safely
- **Learning Curve**: Developers need Java knowledge
- **Deployment**: Extensions must be deployed with application

### Neutral

- **Language Lock-in**: Extensions must be in JVM languages (Java, Kotlin, Scala)
- **Binary Distribution**: JAR files vs source code

## Alternatives Considered

### Alternative 1: JavaScript Plugin System

Execute extensions in JavaScript (GraalVM, Nashorn).

**Pros:**
- Sandboxing possible
- Accessible to more developers
- Hot reload easier
- Runtime interpretation

**Cons:**
- Performance overhead
- Type safety lost
- Security risks with user code
- Harder to debug
- Limited API access

**Why not chosen:**
- Performance requirements
- Type safety important
- Want full Java API access
- Security concerns with runtime code execution

### Alternative 2: External Process Plugins

Extensions run as separate processes, communicate via RPC.

**Pros:**
- Complete isolation
- Any language
- Crash isolation
- Hot reload trivial

**Cons:**
- Significant performance overhead
- Complex inter-process communication
- Deployment complexity
- Debugging difficulty
- State management issues

**Why not chosen:**
- Performance overhead too high
- Unnecessary complexity for use case
- Want tight integration with core

### Alternative 3: OSGi Bundles

Use OSGi framework for modular system.

**Pros:**
- Standard Java modularity
- Class loader isolation
- Mature framework
- Hot reload supported
- Dependency management

**Cons:**
- High complexity
- Steep learning curve
- Heavyweight framework
- Debugging difficulty
- Class loader issues common

**Why not chosen:**
- Too complex for requirements
- Want simpler solution
- OSGi learning curve steep
- Modern alternatives exist (Java modules)

### Alternative 4: Java Platform Module System (JPMS)

Use Java 9+ module system.

**Pros:**
- Standard Java feature
- Better encapsulation
- No extra framework
- Compile-time checking

**Cons:**
- Complex module configuration
- Adoption still growing
- Retroactive modularization difficult
- Tooling still maturing

**Why not chosen:**
- Too complex for initial implementation
- Can migrate to JPMS later
- Want simpler initial solution

## Implementation Details

### Extension Lifecycle

1. **Discovery**: Scan for extensions
2. **Load**: Load extension classes
3. **Validate**: Check compatibility
4. **Initialize**: Call initialize()
5. **Active**: Extension provides functionality
6. **Shutdown**: Call shutdown()
7. **Unload**: Release resources

### Extension API Versioning

- Semantic versioning for extension API
- Extension declares compatible API versions
- Runtime compatibility checking
- Deprecation warnings

### Security Considerations

- Extensions trusted (no sandboxing initially)
- Document security implications
- Code review required for extensions
- Future: Add security manager constraints

### Testing

- Unit tests for extension API
- Integration tests with sample extensions
- Extension testing framework provided
- Documentation with examples

## Migration Path

### Phase 1 (Current)
- Simple classpath-based loading
- Manual registration option
- Basic extension points

### Phase 2 (Future)
- Enhanced discovery mechanism
- Extension dependency resolution
- Hot reload support (development mode)

### Phase 3 (Future)
- Class loader isolation (if needed)
- Enhanced security
- Extension marketplace

## Related Decisions

- [ADR-001: Use Java for Backend Development](001-java-backend.md) - Java enables type-safe extensions
- [ADR-002: Grammar Definition DSL](002-grammar-dsl.md) - Grammar rules extensible via plugins
- [ADR-003: Mapping Rule Engine Architecture](003-mapping-engine.md) - Mapping functions extensible

## References

- [Java Service Provider Interface](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html)
- [Plugin Architecture Patterns](https://www.oreilly.com/library/view/software-architecture-patterns/9781491971437/)
- [IntelliJ Platform SDK (example of plugin system)](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [Eclipse Plugin Development](https://www.eclipse.org/pde/)
