# JavaDoc Guide

## Overview

This guide provides standards and best practices for writing JavaDoc documentation for public APIs in this project.

## Why JavaDoc?

- Generates HTML documentation from source code comments
- Keeps documentation close to code
- IDE integration for inline help
- Standard format recognized by all Java tools
- Enables code completion and parameter hints

## JavaDoc Format

### Basic Structure

```java
/**
 * Brief description of the class or method (first sentence).
 * <p>
 * More detailed description can follow after a paragraph tag.
 * This can span multiple lines and include multiple paragraphs.
 * </p>
 *
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType description of when this exception is thrown
 * @see RelatedClass
 * @since 1.0
 * @author Author Name
 */
```

### Complete Example

```java
package com.project.grammar.api;

/**
 * Parser for grammar definitions.
 * <p>
 * This class provides functionality to parse grammar definition strings
 * into Abstract Syntax Trees (AST). It supports the BNF-like grammar
 * syntax defined in the Grammar DSL specification.
 * </p>
 * <p>
 * The parser performs syntax validation and can optionally generate
 * detailed parse information for debugging purposes.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * GrammarParser parser = new GrammarParser();
 * String grammarText = "rule: 'example';";
 * AST ast = parser.parse(grammarText);
 * }</pre>
 * </p>
 *
 * @author Project Team
 * @version 1.0
 * @since 1.0
 * @see AST
 * @see ParserOptions
 */
public class GrammarParser {
    
    /**
     * Parses a grammar definition string into an Abstract Syntax Tree.
     * <p>
     * This method analyzes the grammar text according to the Grammar DSL
     * syntax rules and constructs a tree representation of the grammar
     * structure. The resulting AST can be used for grammar validation,
     * transformation, or code generation.
     * </p>
     *
     * @param grammarText the grammar definition to parse; must not be null
     * @return an AST representing the parsed grammar structure
     * @throws IllegalArgumentException if grammarText is null or empty
     * @throws GrammarException if the grammar contains syntax errors
     * @see AST
     * @see #parse(String, ParserOptions)
     */
    public AST parse(String grammarText) throws GrammarException {
        return parse(grammarText, ParserOptions.defaults());
    }
    
    /**
     * Parses a grammar definition with custom parsing options.
     * <p>
     * This overloaded method allows fine-grained control over the parsing
     * process through the {@link ParserOptions} parameter. Options include
     * maximum parse depth, caching behavior, and error recovery strategies.
     * </p>
     * <p>
     * Example with options:
     * <pre>{@code
     * ParserOptions options = ParserOptions.builder()
     *     .maxDepth(100)
     *     .cacheEnabled(true)
     *     .build();
     * AST ast = parser.parse(grammarText, options);
     * }</pre>
     * </p>
     *
     * @param grammarText the grammar definition to parse; must not be null
     * @param options parsing configuration options; must not be null
     * @return an AST representing the parsed grammar structure
     * @throws IllegalArgumentException if any parameter is null
     * @throws GrammarException if the grammar contains syntax errors
     * @throws ParseDepthExceededException if parsing exceeds maximum depth
     * @see ParserOptions
     * @see AST
     */
    public AST parse(String grammarText, ParserOptions options) 
            throws GrammarException {
        // Implementation
    }
}
```

## JavaDoc Tags

### Required Tags

#### Classes and Interfaces

```java
/**
 * Brief description.
 *
 * @author Author Name
 * @version 1.0
 * @since 1.0
 */
```

#### Methods

```java
/**
 * Brief description.
 *
 * @param paramName parameter description
 * @return return value description
 * @throws ExceptionType when exception is thrown
 */
```

### Optional But Recommended Tags

#### @see - Cross References

```java
/**
 * Validates grammar syntax.
 *
 * @param grammar the grammar to validate
 * @return validation result
 * @see GrammarParser#parse(String)
 * @see ValidationResult
 */
```

#### @deprecated - Deprecation Notice

```java
/**
 * Old method kept for backwards compatibility.
 *
 * @deprecated As of version 2.0, replaced by {@link #newMethod()}
 * @see #newMethod()
 */
@Deprecated
public void oldMethod() {
    // Implementation
}
```

#### @since - Version Introduction

```java
/**
 * New feature added in version 1.5.
 *
 * @since 1.5
 */
public void newFeature() {
    // Implementation
}
```

#### {@link} - Inline Links

```java
/**
 * This method uses {@link GrammarParser} to parse the input.
 * See also {@link AST} for the return type.
 */
```

#### {@code} - Code Formatting

```java
/**
 * Returns the rule name, for example {@code "expression"}.
 *
 * @return the rule name
 */
```

#### {@literal} - Literal Text

```java
/**
 * Special characters {@literal <, >, &} are displayed literally.
 */
```

## Best Practices

### 1. First Sentence is Summary

The first sentence (up to the first period) becomes the summary in JavaDoc overviews.

```java
/**
 * Parses a grammar definition into an AST. This sentence is the summary
 * that appears in method lists.
 * <p>
 * Additional details follow in separate paragraphs.
 * </p>
 */
```

### 2. Be Specific About Parameters

```java
// Bad
/**
 * @param input the input
 */

// Good
/**
 * @param input the grammar definition string to parse; must not be null
 *              or empty, and must conform to the Grammar DSL syntax
 */
```

### 3. Document Exceptions

```java
/**
 * Parses the grammar definition.
 *
 * @param grammarText the grammar to parse
 * @return parsed AST
 * @throws IllegalArgumentException if grammarText is null or empty
 * @throws GrammarException if the grammar contains syntax errors
 * @throws IOException if reading the grammar fails
 */
```

### 4. Include Code Examples

```java
/**
 * Creates a new grammar rule.
 * <p>
 * Example:
 * <pre>{@code
 * GrammarRule rule = GrammarRule.builder()
 *     .name("identifier")
 *     .pattern("[a-zA-Z_][a-zA-Z0-9_]*")
 *     .build();
 * }</pre>
 * </p>
 */
```

### 5. Use Paragraphs

```java
/**
 * Brief description in first sentence.
 * <p>
 * First paragraph with more details.
 * </p>
 * <p>
 * Second paragraph with additional information.
 * </p>
 */
```

### 6. Document Return Values Clearly

```java
// Bad
/**
 * @return the result
 */

// Good
/**
 * @return an AST representing the parsed grammar, or an empty AST if
 *         the input was empty; never returns null
 */
```

### 7. Document Null Handling

```java
/**
 * Retrieves a grammar by ID.
 *
 * @param id the grammar ID to look up; must not be null
 * @return the grammar with the specified ID, or {@code null} if not found
 * @throws IllegalArgumentException if id is null
 */
```

### 8. Thread Safety

```java
/**
 * Thread-safe parser implementation.
 * <p>
 * This class is thread-safe and can be shared across multiple threads.
 * Parse operations are independent and do not share mutable state.
 * </p>
 */
```

### 9. Immutability

```java
/**
 * Immutable representation of a grammar rule.
 * <p>
 * Once created, instances of this class cannot be modified. All methods
 * return new instances rather than modifying the current instance.
 * </p>
 */
```

### 10. Generic Types

```java
/**
 * Generic mapping function.
 *
 * @param <I> the input type
 * @param <O> the output type
 */
public interface MappingFunction<I, O> {
    
    /**
     * Applies the transformation to the input value.
     *
     * @param input the input value of type {@code I}
     * @return the transformed output value of type {@code O}
     */
    O apply(I input);
}
```

## What to Document

### Always Document

- Public classes and interfaces
- Public and protected methods
- Public constructors
- Public constants
- Package-level classes (package-info.java)

### Consider Documenting

- Protected fields (if part of API)
- Package-private classes (if used by extensions)
- Complex private methods (for maintainers)

### Don't Need to Document

- Private fields (unless complex)
- Simple getters/setters (if obvious)
- Overridden methods (if behavior unchanged)

## Package Documentation

Create `package-info.java` in each package:

```java
/**
 * Grammar parsing and validation components.
 * <p>
 * This package contains classes for parsing grammar definitions according
 * to the Grammar DSL syntax. The main entry point is {@link GrammarParser},
 * which produces {@link AST} representations of grammar definitions.
 * </p>
 * <p>
 * Key classes:
 * </p>
 * <ul>
 *   <li>{@link GrammarParser} - Main parser implementation</li>
 *   <li>{@link AST} - Abstract Syntax Tree representation</li>
 *   <li>{@link GrammarValidator} - Grammar validation</li>
 *   <li>{@link ParserOptions} - Parser configuration</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * GrammarParser parser = new GrammarParser();
 * AST ast = parser.parse("rule: 'example';");
 * }</pre>
 * </p>
 *
 * @since 1.0
 * @see com.project.grammar.api.GrammarParser
 */
package com.project.grammar.api;
```

## Generating JavaDoc

### Maven

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.5.0</version>
    <configuration>
        <show>public</show>
        <doctitle>Project Name API Documentation</doctitle>
        <bottom>Copyright © 2024 Project Name. All Rights Reserved.</bottom>
    </configuration>
</plugin>
```

Generate with:
```bash
mvn javadoc:javadoc
```

Output: `target/site/apidocs/index.html`

### Gradle

```gradle
javadoc {
    options.windowTitle = 'Project Name API Documentation'
    options.memberLevel = JavadocMemberLevel.PUBLIC
    options.author = true
    options.use = true
}
```

Generate with:
```bash
./gradlew javadoc
```

Output: `build/docs/javadoc/index.html`

## Common Mistakes to Avoid

### 1. Repeating Method Name

```java
// Bad
/**
 * Gets the name.
 *
 * @return the name
 */
public String getName();

// Better (if truly just a getter, might not need JavaDoc)
// Or provide more context:
/**
 * Returns the unique identifier name of this grammar rule.
 * The name must be unique within a grammar definition.
 *
 * @return the rule name; never null
 */
public String getName();
```

### 2. Incomplete @throws Documentation

```java
// Bad
/**
 * @throws Exception if error
 */

// Good
/**
 * @throws IllegalArgumentException if the grammar text is null or empty
 * @throws GrammarException if the grammar contains syntax errors at
 *                           any level of nesting
 * @throws IOException if unable to read from the grammar source
 */
```

### 3. Missing Null Handling

```java
// Bad
/**
 * @param input the input
 */

// Good
/**
 * @param input the input value; may be null, in which case the method
 *              returns null
 */
```

### 4. Vague Descriptions

```java
// Bad
/**
 * Does something with the data.
 */

// Good
/**
 * Transforms the input data according to the configured mapping rules,
 * applying all registered transformation functions in sequence.
 */
```

## HTML in JavaDoc

You can use HTML tags:

```java
/**
 * Process flow:
 * <ol>
 *   <li>Validate input</li>
 *   <li>Parse grammar</li>
 *   <li>Build AST</li>
 *   <li>Return result</li>
 * </ol>
 * <p>
 * <strong>Note:</strong> This method is <em>thread-safe</em>.
 * </p>
 */
```

## Custom JavaDoc Tags

Define custom tags in build configuration:

```xml
<configuration>
    <tags>
        <tag>
            <name>apiNote</name>
            <placement>a</placement>
            <head>API Note:</head>
        </tag>
    </tags>
</configuration>
```

Use in code:

```java
/**
 * Parse method.
 *
 * @apiNote This method caches results for improved performance.
 */
```

## Bilingual Documentation

For Chinese documentation:

```java
/**
 * Parses a grammar definition into an Abstract Syntax Tree.
 * 将语法定义解析为抽象语法树。
 * <p>
 * This method analyzes the grammar text and constructs a tree representation.
 * 此方法分析语法文本并构建树形表示。
 * </p>
 *
 * @param grammarText the grammar definition to parse; must not be null
 *                    语法定义文本；不能为 null
 * @return an AST representing the parsed grammar
 *         表示已解析语法的 AST
 * @throws GrammarException if the grammar contains syntax errors
 *                           如果语法包含语法错误
 */
```

## Tools and Linting

### Checkstyle

Enforce JavaDoc presence:

```xml
<module name="JavadocMethod">
    <property name="scope" value="public"/>
</module>
<module name="JavadocType">
    <property name="scope" value="public"/>
</module>
```

### IntelliJ IDEA

- Settings → Editor → Inspections → Java → Javadoc
- Enable warnings for missing or incomplete JavaDoc

## References

- [Oracle JavaDoc Guide](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [How to Write Doc Comments for JavaDoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [JavaDoc Tags](https://docs.oracle.com/en/java/javase/11/docs/specs/doc-comment-spec.html)
