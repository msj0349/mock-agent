# Contributing Guide

Thank you for your interest in contributing to this project! This guide will help you get started.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Requirements](#testing-requirements)
- [Documentation](#documentation)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please be respectful and constructive in all interactions.

### Expected Behavior

- Use welcoming and inclusive language
- Be respectful of differing viewpoints and experiences
- Gracefully accept constructive criticism
- Focus on what is best for the community
- Show empathy towards other community members

### Unacceptable Behavior

- Harassment, discrimination, or offensive comments
- Trolling or insulting/derogatory comments
- Public or private harassment
- Publishing others' private information without permission
- Other conduct which could reasonably be considered inappropriate

## Getting Started

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR-USERNAME/project-name.git
   cd project-name
   ```
3. Add the upstream repository:
   ```bash
   git remote add upstream https://github.com/original-org/project-name.git
   ```

### Set Up Development Environment

1. Install prerequisites (see README.md)
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run tests to verify setup:
   ```bash
   mvn test
   ```

## Development Workflow

### 1. Create a Branch

Create a feature branch from `main`:

```bash
git checkout main
git pull upstream main
git checkout -b feature/your-feature-name
```

Branch naming conventions:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions or modifications

### 2. Make Changes

- Write clean, maintainable code
- Follow the coding standards (see below)
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Check code coverage
mvn test jacoco:report
```

### 4. Commit Your Changes

```bash
git add .
git commit -m "type: brief description"
```

See [Commit Message Guidelines](#commit-message-guidelines) for details.

### 5. Keep Your Branch Updated

```bash
git fetch upstream
git rebase upstream/main
```

### 6. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub.

## Coding Standards

### Java Code Style

#### General Principles

1. **Readability**: Code should be self-documenting
2. **Simplicity**: Prefer simple solutions over complex ones
3. **Consistency**: Follow existing patterns in the codebase
4. **SOLID Principles**: Apply SOLID design principles

#### Naming Conventions

```java
// Classes: PascalCase
public class GrammarParser { }

// Interfaces: PascalCase (no 'I' prefix)
public interface MappingRule { }

// Methods: camelCase
public void parseGrammar() { }

// Variables: camelCase
private String grammarDefinition;

// Constants: UPPER_SNAKE_CASE
public static final int MAX_DEPTH = 100;

// Packages: lowercase
package com.project.grammar.parser;
```

#### Code Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Maximum 120 characters
- **Braces**: Opening brace on same line (K&R style)

```java
public class Example {
    public void method() {
        if (condition) {
            // code
        } else {
            // code
        }
    }
}
```

#### JavaDoc

All public APIs must have JavaDoc:

```java
/**
 * Parses a grammar definition and returns an abstract syntax tree.
 *
 * @param grammarText the grammar definition to parse
 * @param options parsing options to configure behavior
 * @return an AST representing the parsed grammar
 * @throws GrammarException if the grammar is invalid
 */
public AST parse(String grammarText, ParserOptions options) throws GrammarException {
    // implementation
}
```

#### Best Practices

1. **Use meaningful variable names**:
   ```java
   // Good
   String customerEmail = getCustomerEmail();
   
   // Bad
   String s = getCE();
   ```

2. **Keep methods small**: Aim for methods under 20 lines
3. **Single Responsibility**: Each class/method should have one clear purpose
4. **Avoid nested conditionals**: Refactor deep nesting
5. **Use Optional**: Prefer `Optional<T>` over null returns
6. **Stream API**: Use Java Streams for collection operations
7. **Exception Handling**: Catch specific exceptions, not generic `Exception`

### Frontend Code Style

#### JavaScript/TypeScript

- **Indentation**: 2 spaces
- **Quotes**: Single quotes for strings
- **Semicolons**: Always use semicolons
- **Naming**: camelCase for variables/functions, PascalCase for components/classes

```javascript
// Good
const parseGrammar = (text) => {
  return parser.parse(text);
};

// Component names: PascalCase
const GrammarEditor = () => {
  // implementation
};
```

### Configuration Files

- **YAML**: 2 spaces indentation
- **JSON**: 2 spaces indentation
- **Properties**: Standard format

## Commit Message Guidelines

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no logic change)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks, dependencies

### Examples

```
feat(grammar): add support for custom operators

Implement custom operator definitions in grammar rules.
This allows users to define domain-specific operators.

Closes #123
```

```
fix(mapping): resolve null pointer in rule evaluation

Add null checks before accessing rule properties.

Fixes #456
```

```
docs: update API usage examples in README
```

## Pull Request Process

### Before Submitting

- [ ] Code follows the style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated and passing
- [ ] No new warnings introduced
- [ ] Branch is up-to-date with main

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe how you tested the changes

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
```

### Review Process

1. At least one approval required
2. All CI checks must pass
3. Conflicts must be resolved
4. Address all review comments
5. Squash commits before merge (if requested)

### After Merge

- Delete your feature branch
- Update your local repository:
  ```bash
  git checkout main
  git pull upstream main
  ```

## Testing Requirements

### Test Coverage

- Minimum 80% code coverage for new code
- 100% coverage for critical paths
- All public APIs must have tests

### Test Types

#### Unit Tests

```java
@Test
public void testParseValidGrammar() {
    String grammar = "rule: 'test';";
    AST result = parser.parse(grammar);
    assertNotNull(result);
    assertEquals("test", result.getRule().getValue());
}
```

#### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
public class GrammarAPIIntegrationTest {
    @Test
    public void testGrammarEndpoint() throws Exception {
        mockMvc.perform(post("/api/grammar/parse")
                .content(grammarJson))
                .andExpect(status().isOk());
    }
}
```

#### Test Best Practices

1. **Test Naming**: Use descriptive names
   ```java
   testParseGrammar_WithValidInput_ReturnsAST()
   ```
2. **Arrange-Act-Assert**: Structure tests clearly
3. **Test One Thing**: Each test should verify one behavior
4. **Use Test Fixtures**: Reuse test data
5. **Mock External Dependencies**: Isolate unit tests

### Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=GrammarParserTest

# Specific test method
mvn test -Dtest=GrammarParserTest#testParseValidGrammar

# With coverage
mvn test jacoco:report
```

## Documentation

### What to Document

1. **Public APIs**: All public classes, methods, and interfaces
2. **Configuration**: New configuration options
3. **Architecture**: Significant structural changes
4. **Examples**: Usage examples for new features
5. **ADRs**: Architecture Decision Records for major decisions

### Documentation Locations

- **JavaDoc**: In-code documentation
- **README.md**: Project overview and getting started
- **docs/**: Detailed documentation
- **docs/adr/**: Architecture Decision Records
- **CHANGELOG.md**: Version history

### Writing Good Documentation

1. **Be Clear**: Use simple, direct language
2. **Be Complete**: Cover all parameters, return values, exceptions
3. **Provide Examples**: Include code examples
4. **Keep Updated**: Update docs when code changes
5. **Bilingual**: Provide English and Chinese where appropriate

## Questions?

If you have questions:

1. Check existing documentation
2. Search through GitHub Issues
3. Ask in GitHub Discussions
4. Contact maintainers

Thank you for contributing!
