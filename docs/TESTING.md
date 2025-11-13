# Testing Strategy and Guide

## Overview

This document outlines the testing strategy, best practices, and guidelines for testing the application at all levels.

## Testing Philosophy

Our testing approach follows these principles:

1. **Test Pyramid**: More unit tests, fewer integration tests, minimal E2E tests
2. **Test Behavior, Not Implementation**: Focus on what the code does, not how
3. **Fast Feedback**: Tests should run quickly
4. **Maintainable**: Tests should be easy to understand and maintain
5. **Reliable**: Tests should be deterministic and not flaky
6. **Comprehensive**: Critical paths should have 100% coverage

## Test Types

### 1. Unit Tests

Test individual components, functions, or classes in isolation.

**Coverage Target**: 80% minimum, 100% for critical paths

#### Java Unit Test Example

```java
package com.project.grammar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Grammar Parser Tests")
class GrammarParserTest {
    
    private GrammarParser parser;
    private ParserOptions options;
    
    @BeforeEach
    void setUp() {
        options = ParserOptions.builder()
            .maxDepth(100)
            .cacheEnabled(false)
            .build();
        parser = new GrammarParser(options);
    }
    
    @Test
    @DisplayName("Should parse valid grammar definition")
    void testParseValidGrammar() {
        // Arrange
        String grammar = "rule: 'test';";
        
        // Act
        AST result = parser.parse(grammar);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRules().size());
        assertEquals("rule", result.getRules().get(0).getName());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid grammar")
    void testParseInvalidGrammar() {
        // Arrange
        String invalidGrammar = "rule: [invalid";
        
        // Act & Assert
        assertThrows(GrammarException.class, () -> {
            parser.parse(invalidGrammar);
        });
    }
    
    @Test
    @DisplayName("Should handle null input")
    void testParseNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(null);
        });
    }
    
    @Test
    @DisplayName("Should respect max depth option")
    void testMaxDepthRespected() {
        // Arrange
        String deepGrammar = createDeeplyNestedGrammar(150);
        
        // Act & Assert
        GrammarException exception = assertThrows(
            GrammarException.class,
            () -> parser.parse(deepGrammar)
        );
        assertTrue(exception.getMessage().contains("max depth"));
    }
}
```

#### Mapping Function Unit Test

```java
package com.project.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class TitleCaseFunctionTest {
    
    private final TitleCaseFunction function = new TitleCaseFunction();
    private final MappingContext context = mock(MappingContext.class);
    
    @ParameterizedTest
    @CsvSource({
        "'hello world', 'Hello World'",
        "'HELLO WORLD', 'Hello World'",
        "'hELLo WoRLd', 'Hello World'",
        "'hello', 'Hello'",
        "'', ''"
    })
    void testTitleCase(String input, String expected) {
        String result = function.apply(input, context);
        assertEquals(expected, result);
    }
    
    @Test
    void testNullInput() {
        assertNull(function.apply(null, context));
    }
    
    @Test
    void testGetName() {
        assertEquals("titleCase", function.getName());
    }
    
    @Test
    void testInputType() {
        assertEquals(String.class, function.getInputType());
    }
    
    @Test
    void testOutputType() {
        assertEquals(String.class, function.getOutputType());
    }
}
```

### 2. Integration Tests

Test interactions between multiple components.

**Coverage Target**: Critical integration points

#### API Integration Test

```java
package com.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GrammarAPIIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testParseGrammar() throws Exception {
        String grammarJson = """
            {
                "grammar": "rule: 'test';",
                "options": {
                    "validateOnly": false
                }
            }
            """;
        
        mockMvc.perform(post("/api/v1/grammar/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(grammarJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.ast").exists())
                .andExpect(jsonPath("$.data.ast.rules").isArray())
                .andExpect(jsonPath("$.data.ast.rules", hasSize(1)));
    }
    
    @Test
    void testCreateAndRetrieveGrammar() throws Exception {
        // Create grammar
        String createJson = """
            {
                "name": "Test Grammar",
                "description": "A test grammar",
                "definition": "rule: 'test';"
            }
            """;
        
        String createResponse = mockMvc.perform(post("/api/v1/grammar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String grammarId = JsonPath.read(createResponse, "$.data.id");
        
        // Retrieve grammar
        mockMvc.perform(get("/api/v1/grammar/" + grammarId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(grammarId))
                .andExpect(jsonPath("$.data.name").value("Test Grammar"));
    }
    
    @Test
    void testValidationFailure() throws Exception {
        String invalidJson = """
            {
                "grammar": "invalid [[ syntax"
            }
            """;
        
        mockMvc.perform(post("/api/v1/grammar/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error.code").exists());
    }
}
```

#### Database Integration Test

```java
package com.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GrammarRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private GrammarRepository grammarRepository;
    
    @Test
    void testSaveAndFindGrammar() {
        // Arrange
        GrammarEntity grammar = new GrammarEntity();
        grammar.setName("Test Grammar");
        grammar.setDefinition("rule: 'test';");
        
        // Act
        GrammarEntity saved = grammarRepository.save(grammar);
        entityManager.flush();
        
        GrammarEntity found = grammarRepository.findById(saved.getId())
                .orElse(null);
        
        // Assert
        assertNotNull(found);
        assertEquals("Test Grammar", found.getName());
        assertEquals("rule: 'test';", found.getDefinition());
    }
    
    @Test
    void testFindByName() {
        // Arrange
        GrammarEntity grammar = new GrammarEntity();
        grammar.setName("Unique Grammar");
        grammar.setDefinition("rule: 'unique';");
        entityManager.persist(grammar);
        entityManager.flush();
        
        // Act
        Optional<GrammarEntity> found = grammarRepository.findByName("Unique Grammar");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("rule: 'unique';", found.get().getDefinition());
    }
}
```

### 3. End-to-End (E2E) Tests

Test complete user workflows through the UI.

**Coverage Target**: Critical user journeys

#### Playwright E2E Test (TypeScript)

```typescript
// tests/e2e/grammar-workflow.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Grammar Workflow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
    await page.click('text=Login');
    await page.fill('input[name="username"]', 'testuser');
    await page.fill('input[name="password"]', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL(/.*\/dashboard/);
  });

  test('should create and test a grammar', async ({ page }) => {
    // Navigate to grammar editor
    await page.click('text=Grammars');
    await page.click('text=New Grammar');
    
    // Fill in grammar details
    await page.fill('input[name="name"]', 'Test Grammar');
    await page.fill('textarea[name="description"]', 'A test grammar definition');
    
    // Enter grammar definition
    const editor = page.locator('.grammar-editor');
    await editor.fill('rule: "test";');
    
    // Test grammar
    await page.click('button:has-text("Test")');
    await page.fill('input[name="test-input"]', 'test');
    await page.click('button:has-text("Parse")');
    
    // Verify result
    await expect(page.locator('.parse-result')).toContainText('Success');
    await expect(page.locator('.ast-view')).toBeVisible();
    
    // Save grammar
    await page.click('button:has-text("Save")');
    await expect(page.locator('.success-message')).toContainText('Grammar saved');
  });

  test('should validate invalid grammar', async ({ page }) => {
    await page.click('text=Grammars');
    await page.click('text=New Grammar');
    
    const editor = page.locator('.grammar-editor');
    await editor.fill('invalid [[ syntax');
    
    await page.click('button:has-text("Validate")');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('Invalid syntax');
  });
});
```

#### Cypress E2E Test

```typescript
// cypress/e2e/mapping-workflow.cy.ts
describe('Mapping Workflow', () => {
  beforeEach(() => {
    cy.login('testuser', 'password');
    cy.visit('/mappings');
  });

  it('should create a mapping rule', () => {
    cy.contains('New Mapping').click();
    
    cy.get('input[name="name"]').type('User Mapping');
    cy.get('textarea[name="description"]').type('Maps API user to database user');
    
    // Add mapping rules
    cy.contains('Add Rule').click();
    cy.get('[data-testid="source-field"]').type('firstName');
    cy.get('[data-testid="target-field"]').type('first_name');
    cy.get('[data-testid="transform"]').select('uppercase');
    
    // Test mapping
    cy.contains('Test').click();
    cy.get('[data-testid="test-input"]').type('{"firstName": "john"}');
    cy.contains('Execute').click();
    
    cy.get('[data-testid="test-output"]').should('contain', 'JOHN');
    
    // Save mapping
    cy.contains('Save').click();
    cy.contains('Mapping saved successfully').should('be.visible');
  });

  it('should execute existing mapping', () => {
    cy.get('[data-testid="mapping-list"]')
      .contains('User Mapping')
      .click();
    
    cy.contains('Execute').click();
    
    const inputData = {
      firstName: 'Jane',
      lastName: 'Doe',
      email: 'jane@example.com'
    };
    
    cy.get('[data-testid="input-data"]').type(JSON.stringify(inputData));
    cy.contains('Run').click();
    
    cy.get('[data-testid="output-data"]').should('contain', 'first_name');
    cy.get('[data-testid="output-data"]').should('contain', 'JANE');
  });
});
```

### 4. Frontend Unit Tests

#### React Component Test

```typescript
// src/components/grammar/GrammarEditor.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { GrammarEditor } from './GrammarEditor';

describe('GrammarEditor', () => {
  const mockOnChange = jest.fn();
  const mockOnParse = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders editor with initial value', () => {
    render(
      <GrammarEditor
        value="rule: 'test';"
        onChange={mockOnChange}
        onParse={mockOnParse}
      />
    );

    expect(screen.getByRole('textbox')).toHaveValue("rule: 'test';");
  });

  it('calls onChange when text is edited', async () => {
    render(
      <GrammarEditor
        value=""
        onChange={mockOnChange}
        onParse={mockOnParse}
      />
    );

    const editor = screen.getByRole('textbox');
    await userEvent.type(editor, 'new text');

    expect(mockOnChange).toHaveBeenCalled();
  });

  it('calls onParse when parse button is clicked', async () => {
    render(
      <GrammarEditor
        value="rule: 'test';"
        onChange={mockOnChange}
        onParse={mockOnParse}
      />
    );

    const parseButton = screen.getByRole('button', { name: /parse/i });
    fireEvent.click(parseButton);

    await waitFor(() => {
      expect(mockOnParse).toHaveBeenCalledWith("rule: 'test';");
    });
  });

  it('displays parse result', () => {
    const parseResult = {
      success: true,
      ast: { rules: [{ name: 'rule', value: 'test' }] },
    };

    render(
      <GrammarEditor
        value="rule: 'test';"
        onChange={mockOnChange}
        onParse={mockOnParse}
        parseResult={parseResult}
      />
    );

    expect(screen.getByText(/success/i)).toBeInTheDocument();
    expect(screen.getByText(/rule/i)).toBeInTheDocument();
  });

  it('displays error message', () => {
    const parseResult = {
      success: false,
      error: 'Invalid syntax at line 1',
    };

    render(
      <GrammarEditor
        value="invalid syntax"
        onChange={mockOnChange}
        onParse={mockOnParse}
        parseResult={parseResult}
      />
    );

    expect(screen.getByText(/invalid syntax/i)).toBeInTheDocument();
  });
});
```

#### Service Test

```typescript
// src/services/grammarService.test.ts
import { grammarService } from './grammarService';
import { apiClient } from './apiClient';

jest.mock('./apiClient');

describe('grammarService', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('parse', () => {
    it('should call API with correct parameters', async () => {
      const mockResponse = {
        data: {
          status: 'success',
          data: { ast: {} },
        },
      };
      (apiClient.post as jest.Mock).mockResolvedValue(mockResponse);

      await grammarService.parse('rule: "test";');

      expect(apiClient.post).toHaveBeenCalledWith(
        '/api/v1/grammar/parse',
        { grammar: 'rule: "test";', options: undefined }
      );
    });

    it('should return parse result', async () => {
      const mockResult = { ast: { rules: [] } };
      (apiClient.post as jest.Mock).mockResolvedValue({
        data: mockResult,
      });

      const result = await grammarService.parse('rule: "test";');

      expect(result).toEqual(mockResult);
    });

    it('should handle errors', async () => {
      (apiClient.post as jest.Mock).mockRejectedValue(
        new Error('Network error')
      );

      await expect(grammarService.parse('rule: "test";')).rejects.toThrow(
        'Network error'
      );
    });
  });
});
```

### 5. Performance Tests

Test application performance under load.

#### JMeter Test Plan (XML)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan testname="Grammar API Load Test">
      <ThreadGroup testname="Users">
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
        <stringProp name="ThreadGroup.duration">60</stringProp>
      </ThreadGroup>
      <HTTPSamplerProxy testname="Parse Grammar">
        <stringProp name="HTTPSampler.domain">localhost</stringProp>
        <stringProp name="HTTPSampler.port">8080</stringProp>
        <stringProp name="HTTPSampler.path">/api/v1/grammar/parse</stringProp>
        <stringProp name="HTTPSampler.method">POST</stringProp>
      </HTTPSamplerProxy>
    </TestPlan>
  </hashTree>
</jmeterTestPlan>
```

#### Gatling Performance Test (Scala)

```scala
// src/test/scala/GrammarSimulation.scala
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GrammarSimulation extends Simulation {
  
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
  
  val parseScenario = scenario("Parse Grammar")
    .exec(
      http("Parse Request")
        .post("/api/v1/grammar/parse")
        .body(StringBody("""{"grammar": "rule: 'test';"}"""))
        .check(status.is(200))
        .check(jsonPath("$.status").is("success"))
    )
  
  setUp(
    parseScenario.inject(
      rampUsers(100) during (10 seconds),
      constantUsersPerSec(50) during (60 seconds)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.max.lt(1000),
     global.successfulRequests.percent.gt(95)
   )
}
```

## Test Coverage

### Measuring Coverage

#### Java (JaCoCo)

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run coverage:
```bash
mvn clean test jacoco:report
```

View report: `target/site/jacoco/index.html`

#### TypeScript (Istanbul/NYC)

```json
// package.json
{
  "scripts": {
    "test:coverage": "vitest run --coverage"
  },
  "devDependencies": {
    "@vitest/coverage-v8": "^0.34.0"
  }
}
```

Run coverage:
```bash
npm run test:coverage
```

## Test Data Management

### Test Fixtures

```java
// src/test/java/fixtures/GrammarFixtures.java
public class GrammarFixtures {
    
    public static Grammar simpleGrammar() {
        return Grammar.builder()
            .name("Simple Grammar")
            .definition("rule: 'test';")
            .build();
    }
    
    public static Grammar complexGrammar() {
        return Grammar.builder()
            .name("Complex Grammar")
            .definition("""
                expression: term (('+' | '-') term)*;
                term: factor (('*' | '/') factor)*;
                factor: number | '(' expression ')';
                number: [0-9]+;
                """)
            .build();
    }
}
```

### Test Data Builders

```java
public class GrammarBuilder {
    private String name = "Test Grammar";
    private String definition = "rule: 'test';";
    private String description = "Test description";
    
    public GrammarBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public GrammarBuilder withDefinition(String definition) {
        this.definition = definition;
        return this;
    }
    
    public Grammar build() {
        return new Grammar(name, definition, description);
    }
}

// Usage
Grammar grammar = new GrammarBuilder()
    .withName("Custom Grammar")
    .withDefinition("custom: 'rule';")
    .build();
```

## Mocking

### Mockito Examples

```java
// Mock dependencies
@Mock
private GrammarRepository repository;

@Mock
private GrammarValidator validator;

@InjectMocks
private GrammarService service;

@Test
void testServiceWithMocks() {
    // Setup mock behavior
    when(repository.findById("123"))
        .thenReturn(Optional.of(new Grammar()));
    when(validator.validate(any()))
        .thenReturn(ValidationResult.success());
    
    // Execute
    Grammar result = service.getGrammar("123");
    
    // Verify
    assertNotNull(result);
    verify(repository).findById("123");
    verify(validator).validate(any());
}
```

## Continuous Integration

### GitHub Actions Workflow

```yaml
# .github/workflows/test.yml
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      
      - name: Run tests
        run: mvn clean test
      
      - name: Generate coverage report
        run: mvn jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
```

## Best Practices

1. **Arrange-Act-Assert**: Structure tests clearly
2. **One Assertion Per Test**: Test one thing at a time
3. **Descriptive Names**: Use descriptive test names
4. **Fast Tests**: Keep tests fast
5. **Independent Tests**: Tests should not depend on each other
6. **Clean Up**: Clean up resources after tests
7. **Test Edge Cases**: Test boundary conditions
8. **Mock External Dependencies**: Isolate unit tests
9. **Use Test Data Builders**: Create test data easily
10. **Keep Tests Maintainable**: Refactor tests like production code

## Running Tests

### Backend

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=GrammarParserTest

# Run specific test method
mvn test -Dtest=GrammarParserTest#testParseValidGrammar

# Run integration tests
mvn verify

# Run with coverage
mvn clean test jacoco:report

# Skip tests
mvn clean install -DskipTests
```

### Frontend

```bash
# Run all tests
npm test

# Run in watch mode
npm test -- --watch

# Run with coverage
npm run test:coverage

# Run E2E tests
npm run test:e2e

# Run specific test file
npm test -- GrammarEditor.test.tsx
```

## Debugging Tests

### IntelliJ IDEA
- Right-click on test → Debug
- Set breakpoints in test code
- Use "Run with Coverage" option

### VS Code
- Install Java Test Runner extension
- Click debug icon next to test
- Set breakpoints in test code

### Browser DevTools (Frontend)
```bash
# Run tests in debug mode
npm test -- --inspect-brk
```

Then open `chrome://inspect` in Chrome.
