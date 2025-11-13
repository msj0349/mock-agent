# Testing Suite Documentation

This document describes the comprehensive testing infrastructure implemented for the SQL Converter project.

## Overview

The testing suite includes:
- **Unit Tests**: Test individual components with mocked dependencies (JUnit 5 + Mockito)
- **Integration Tests**: Test component interactions with real dependencies
- **E2E Tests**: Test complete workflows through REST API (REST Assured)
- **MySQL Validation Tests**: Validate generated SQL against MySQL using Testcontainers
- **Code Coverage**: Enforce coverage thresholds via Jacoco (80% line, 75% branch)

## Test Structure

```
project/
├── core/
│   └── src/test/java/
│       └── com/project/core/
│           └── util/
│               └── StringUtilsTest.java (Unit Tests)
├── grammar/
│   └── src/test/
│       ├── java/com/project/grammar/parser/
│       │   ├── OracleToMySQLConverterTest.java (Unit Tests)
│       │   └── OracleToMySQLConverterIntegrationTest.java (Integration Tests)
│       └── resources/oracle-samples/
│           ├── simple_procedure.sql (Oracle Input)
│           ├── simple_procedure_expected.sql (Expected MySQL Output)
│           ├── function_with_decode.sql
│           └── complex_procedure.sql
├── mapping/
│   └── src/test/java/
│       └── com/project/mapping/service/
│           └── ConversionServiceTest.java (Unit Tests with Mocks)
└── api/
    └── src/test/java/com/project/api/
        ├── controller/
        │   └── ConversionControllerE2ETest.java (E2E REST API Tests)
        ├── repository/
        │   └── ConversionHistoryRepositoryIT.java (Integration Tests)
        └── integration/
            └── MySQLValidationIT.java (Testcontainers MySQL Tests)
```

## Running Tests

### Run All Tests
```bash
mvn clean verify
```

### Run Unit Tests Only
```bash
mvn test
```

### Run Integration Tests Only
```bash
mvn failsafe:integration-test
```

### Run Tests with Coverage Report
```bash
mvn clean verify jacoco:report
```

### View Coverage Report
After running tests with coverage:
```bash
# Open in browser
open target/site/jacoco/index.html
```

## Test Categories

### 1. Unit Tests

**Purpose**: Test individual components in isolation with mocked dependencies

**Example**: `OracleToMySQLConverterTest.java`
- Tests data type conversions (NUMBER → DECIMAL, VARCHAR2 → VARCHAR)
- Tests function conversions (SYSDATE → NOW())
- Tests procedure syntax conversions
- Uses pure JUnit 5 assertions and AssertJ

**Example**: `StringUtilsTest.java`
- Tests utility methods
- Uses parameterized tests for multiple inputs
- Validates edge cases (null, empty, blank)

**Example**: `ConversionServiceTest.java`
- Tests service layer with mocked converter
- Uses Mockito for dependency mocking
- Validates error handling and batch processing

### 2. Integration Tests

**Purpose**: Test interactions between multiple components

**Example**: `OracleToMySQLConverterIntegrationTest.java`
- Reads Oracle SQL from resource files
- Converts using actual converter (no mocks)
- Validates output matches expected patterns
- Tests with curated sample stored procedures

**Example**: `ConversionHistoryRepositoryIT.java`
- Tests JPA repository with in-memory H2 database
- Uses Spring's `@DataJpaTest` annotation
- Tests CRUD operations and custom queries

### 3. End-to-End Tests

**Purpose**: Test complete user workflows through REST API

**Example**: `ConversionControllerE2ETest.java`
- Uses REST Assured for API testing
- Tests full request/response cycle
- Validates HTTP status codes and response bodies
- Tests error handling and validation
- Uses real Spring Boot application context

**Key Test Cases**:
- Convert Oracle SQL via POST endpoint
- Retrieve conversion history
- Handle validation errors
- Test warning generation

### 4. MySQL Validation Tests (Testcontainers)

**Purpose**: Validate that converted SQL actually works in MySQL

**Example**: `MySQLValidationIT.java`
- Spins up real MySQL container using Testcontainers
- Executes converted SQL against MySQL
- Validates data types work correctly
- Tests procedure creation
- Validates function conversions (NOW(), etc.)

**Key Features**:
- Automatic MySQL container lifecycle management
- Real database validation
- Tests DDL and DML statements
- Validates data type compatibility

## Sample Test Resources

### Oracle Input Samples

Located in `grammar/src/test/resources/oracle-samples/`:

1. **simple_procedure.sql**
   - Basic Oracle procedure with NUMBER and SYSDATE
   - Tests fundamental conversions

2. **function_with_decode.sql**
   - Oracle function using DECODE
   - Tests complex function conversion

3. **complex_procedure.sql**
   - Multi-feature procedure with loops, variables, cursors
   - Tests comprehensive conversion capabilities

### Expected Outputs

Located alongside input samples with `_expected.sql` suffix:
- Provides expected MySQL syntax
- Used for validation in integration tests

## Code Coverage Configuration

### Jacoco Settings (in pom.xml)

```xml
<jacoco.line.coverage>0.80</jacoco.line.coverage>     <!-- 80% line coverage required -->
<jacoco.branch.coverage>0.75</jacoco.branch.coverage> <!-- 75% branch coverage required -->
```

### Coverage Reports

Generated at: `target/site/jacoco/index.html`

Includes:
- Line coverage
- Branch coverage
- Method coverage
- Class coverage
- Per-package breakdown

### Coverage Enforcement

Build will fail if coverage thresholds are not met:
```bash
mvn verify
# Fails if < 80% line coverage or < 75% branch coverage
```

## Testcontainers Configuration

### MySQL Container Setup

```java
@Container
private static final MySQLContainer<?> mysqlContainer = 
    new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
```

### Features:
- Automatic container startup/shutdown
- Isolated test environment
- Real MySQL database for validation
- No manual setup required

### Prerequisites:
- Docker must be installed and running
- Testcontainers will automatically pull MySQL image

## Best Practices

### Unit Tests
- ✅ Use mocks for external dependencies
- ✅ Test one component at a time
- ✅ Use descriptive test names with `@DisplayName`
- ✅ Follow Arrange-Act-Assert pattern
- ✅ Use parameterized tests for multiple inputs

### Integration Tests
- ✅ Use real dependencies where possible
- ✅ Test component interactions
- ✅ Suffix test classes with `IT` or `IntegrationTest`
- ✅ Use `@SpringBootTest` for full context tests
- ✅ Clean up resources in `@AfterEach`

### E2E Tests
- ✅ Test complete user workflows
- ✅ Use REST Assured for API testing
- ✅ Validate both success and error scenarios
- ✅ Test with realistic data
- ✅ Use random ports to avoid conflicts

### Testcontainers Tests
- ✅ Use for database validation
- ✅ Test actual SQL execution
- ✅ Validate data types and syntax
- ✅ Clean up containers properly
- ✅ Use `@Testcontainers` annotation

## CI/CD Integration

The test suite is designed to run in CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Run Tests
  run: mvn clean verify

- name: Upload Coverage Report
  uses: codecov/codecov-action@v3
  with:
    files: ./target/site/jacoco/jacoco.xml
```

## Troubleshooting

### Tests Failing Due to Coverage
```bash
# Check current coverage
mvn jacoco:report
open target/site/jacoco/index.html

# Identify uncovered lines and add tests
```

### Testcontainers Not Starting
```bash
# Ensure Docker is running
docker ps

# Check Docker permissions
docker run hello-world

# Update Testcontainers
mvn dependency:purge-local-repository -DmanualInclude=org.testcontainers
```

### Integration Tests Timeout
```bash
# Increase timeout in test
@Test(timeout = 10000)

# Or use JUnit 5 timeout
@Test
@Timeout(10)
void testMethod() { ... }
```

## Performance

- Unit tests: ~5 seconds
- Integration tests: ~15 seconds
- E2E tests: ~20 seconds
- Testcontainers tests: ~30 seconds (includes container startup)
- Total: ~70 seconds for full test suite

## Continuous Improvement

### Adding New Tests

1. Identify test category (unit/integration/e2e)
2. Follow existing patterns and naming conventions
3. Add test resources if needed
4. Ensure tests are deterministic
5. Run full suite before committing

### Maintaining Coverage

- Review coverage reports regularly
- Add tests for new features immediately
- Don't remove tests without replacement
- Focus on critical paths first

## References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [REST Assured Documentation](https://rest-assured.io/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Jacoco Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
