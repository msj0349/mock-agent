# Extension Development Guide

## Overview

This guide explains how to develop extensions for the system. Extensions allow you to customize and extend the functionality without modifying core code.

## Extension Points

The system provides several extension points:

### 1. Grammar Rule Extensions

Extend the grammar parser with custom rule types.

**Interface**: `GrammarRule`

```java
package com.project.grammar.api;

/**
 * Interface for custom grammar rules.
 * 
 * @author Project Team
 * @version 1.0
 */
public interface GrammarRule {
    
    /**
     * Gets the name of this grammar rule.
     *
     * @return the rule name
     */
    String getName();
    
    /**
     * Validates the rule against input text.
     *
     * @param input the input text to validate
     * @return true if the input matches this rule
     */
    boolean matches(String input);
    
    /**
     * Parses the input according to this rule.
     *
     * @param input the input to parse
     * @param context the parsing context
     * @return the parsed AST node
     * @throws GrammarException if parsing fails
     */
    ASTNode parse(String input, ParserContext context) throws GrammarException;
}
```

**Example Implementation**:

```java
package com.example.extension;

import com.project.grammar.api.*;

/**
 * Custom grammar rule for email addresses.
 */
public class EmailGrammarRule implements GrammarRule {
    
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    @Override
    public String getName() {
        return "email";
    }
    
    @Override
    public boolean matches(String input) {
        return input != null && input.matches(EMAIL_PATTERN);
    }
    
    @Override
    public ASTNode parse(String input, ParserContext context) 
            throws GrammarException {
        if (!matches(input)) {
            throw new GrammarException("Invalid email format: " + input);
        }
        return new EmailNode(input);
    }
}
```

### 2. Mapping Function Extensions

Add custom transformation functions for the mapping engine.

**Interface**: `MappingFunction`

```java
package com.project.mapping.api;

/**
 * Interface for custom mapping transformation functions.
 * 
 * @param <I> input type
 * @param <O> output type
 */
public interface MappingFunction<I, O> {
    
    /**
     * Gets the function name for use in mapping rules.
     *
     * @return the function name
     */
    String getName();
    
    /**
     * Applies the transformation to the input value.
     *
     * @param input the input value
     * @param context the mapping context
     * @return the transformed output value
     * @throws MappingException if transformation fails
     */
    O apply(I input, MappingContext context) throws MappingException;
    
    /**
     * Gets the expected input type.
     *
     * @return the input type class
     */
    Class<I> getInputType();
    
    /**
     * Gets the output type.
     *
     * @return the output type class
     */
    Class<O> getOutputType();
}
```

**Example Implementation**:

```java
package com.example.extension;

import com.project.mapping.api.*;

/**
 * Custom function to convert strings to title case.
 */
public class TitleCaseFunction implements MappingFunction<String, String> {
    
    @Override
    public String getName() {
        return "titleCase";
    }
    
    @Override
    public String apply(String input, MappingContext context) 
            throws MappingException {
        if (input == null) {
            return null;
        }
        
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    @Override
    public Class<String> getInputType() {
        return String.class;
    }
    
    @Override
    public Class<String> getOutputType() {
        return String.class;
    }
}
```

### 3. Validator Extensions

Create custom validators for data validation.

**Interface**: `Validator`

```java
package com.project.validation.api;

/**
 * Base class for custom validators.
 *
 * @param <T> the type being validated
 */
public abstract class Validator<T> {
    
    /**
     * Validates the given value.
     *
     * @param value the value to validate
     * @return validation result
     */
    public abstract ValidationResult validate(T value);
    
    /**
     * Gets the validator name.
     *
     * @return validator name
     */
    public abstract String getName();
}
```

**Example Implementation**:

```java
package com.example.extension;

import com.project.validation.api.*;

/**
 * Validator for phone numbers.
 */
public class PhoneNumberValidator extends Validator<String> {
    
    private static final String PHONE_PATTERN = 
        "^\\+?[1-9]\\d{1,14}$";
    
    @Override
    public String getName() {
        return "phoneNumber";
    }
    
    @Override
    public ValidationResult validate(String value) {
        if (value == null || value.isEmpty()) {
            return ValidationResult.error("Phone number cannot be empty");
        }
        
        if (!value.matches(PHONE_PATTERN)) {
            return ValidationResult.error(
                "Invalid phone number format. Expected E.164 format.");
        }
        
        return ValidationResult.success();
    }
}
```

### 4. Output Formatter Extensions

Create custom formatters for output data.

**Interface**: `OutputFormatter`

```java
package com.project.output.api;

/**
 * Interface for custom output formatters.
 *
 * @param <T> the type to format
 */
public interface OutputFormatter<T> {
    
    /**
     * Gets the format name (e.g., "json", "xml").
     *
     * @return format name
     */
    String getFormatName();
    
    /**
     * Formats the data.
     *
     * @param data the data to format
     * @param options formatting options
     * @return formatted string
     * @throws FormattingException if formatting fails
     */
    String format(T data, FormatOptions options) throws FormattingException;
    
    /**
     * Gets the content type for this format.
     *
     * @return content type (e.g., "application/json")
     */
    String getContentType();
}
```

**Example Implementation**:

```java
package com.example.extension;

import com.project.output.api.*;
import java.util.Map;

/**
 * Formatter for CSV output.
 */
public class CsvFormatter implements OutputFormatter<Map<String, Object>> {
    
    @Override
    public String getFormatName() {
        return "csv";
    }
    
    @Override
    public String format(Map<String, Object> data, FormatOptions options) 
            throws FormattingException {
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append(String.join(",", data.keySet())).append("\n");
        
        // Values
        csv.append(String.join(",", 
            data.values().stream()
                .map(Object::toString)
                .toArray(String[]::new)))
           .append("\n");
        
        return csv.toString();
    }
    
    @Override
    public String getContentType() {
        return "text/csv";
    }
}
```

## Creating an Extension

### Step 1: Set Up Project

Create a new Maven or Gradle project:

**Maven `pom.xml`**:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-extension</artifactId>
    <version>1.0.0</version>
    
    <dependencies>
        <dependency>
            <groupId>com.project</groupId>
            <artifactId>extension-api</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

### Step 2: Implement Extension

Implement one or more extension interfaces:

```java
package com.example.extension;

import com.project.extension.api.*;

@Extension(
    id = "my-custom-extension",
    name = "My Custom Extension",
    version = "1.0.0",
    description = "Adds custom functionality"
)
public class MyExtension implements ExtensionPoint {
    
    @Override
    public void initialize(ExtensionContext context) {
        // Initialization logic
        context.registerGrammarRule(new EmailGrammarRule());
        context.registerMappingFunction(new TitleCaseFunction());
        context.registerValidator(new PhoneNumberValidator());
    }
    
    @Override
    public void shutdown(ExtensionContext context) {
        // Cleanup logic
    }
}
```

### Step 3: Create Extension Descriptor

Create `src/main/resources/META-INF/extension.yml`:

```yaml
extension:
  id: my-custom-extension
  name: My Custom Extension
  version: 1.0.0
  author: Your Name
  description: Adds custom grammar rules and mapping functions
  
  requires:
    minCoreVersion: 1.0.0
    maxCoreVersion: 2.0.0
  
  provides:
    grammarRules:
      - com.example.extension.EmailGrammarRule
    mappingFunctions:
      - com.example.extension.TitleCaseFunction
    validators:
      - com.example.extension.PhoneNumberValidator
  
  dependencies:
    - extensionId: core-extensions
      version: 1.0.0
```

### Step 4: Package Extension

Build the extension JAR:

```bash
mvn clean package
```

The JAR should include:
- Compiled classes
- `META-INF/extension.yml`
- Dependencies (if not provided by core)

### Step 5: Deploy Extension

Deploy the extension:

1. **Classpath Deployment**: Add JAR to application classpath
   ```bash
   java -cp "app.jar:extensions/*" com.project.Application
   ```

2. **Extension Directory**: Place JAR in extensions directory
   ```
   /path/to/app/
   ├── app.jar
   └── extensions/
       └── my-extension-1.0.0.jar
   ```

3. **Dynamic Loading**: Upload via API
   ```bash
   curl -X POST http://localhost:8080/api/v1/extensions/upload \
     -F "file=@my-extension-1.0.0.jar"
   ```

## Extension Lifecycle

### Lifecycle Phases

1. **Discovery**: System scans for extensions
2. **Validation**: Validates extension descriptor and dependencies
3. **Loading**: Loads extension classes
4. **Initialization**: Calls `initialize()` method
5. **Active**: Extension is active and providing functionality
6. **Shutdown**: Calls `shutdown()` method when stopping

### Lifecycle Hooks

```java
public interface ExtensionLifecycle {
    
    /**
     * Called when extension is loaded.
     */
    default void onLoad(ExtensionContext context) {
        // Load configuration, resources
    }
    
    /**
     * Called when extension is initialized.
     */
    default void onInitialize(ExtensionContext context) {
        // Register components
    }
    
    /**
     * Called when extension starts.
     */
    default void onStart(ExtensionContext context) {
        // Start background tasks
    }
    
    /**
     * Called when extension stops.
     */
    default void onStop(ExtensionContext context) {
        // Stop background tasks
    }
    
    /**
     * Called when extension is unloaded.
     */
    default void onUnload(ExtensionContext context) {
        // Cleanup resources
    }
}
```

## Extension Context

The `ExtensionContext` provides access to core functionality:

```java
public interface ExtensionContext {
    
    /**
     * Gets extension configuration.
     */
    Configuration getConfiguration();
    
    /**
     * Gets a service from the core application.
     */
    <T> T getService(Class<T> serviceClass);
    
    /**
     * Registers a component with the system.
     */
    void registerComponent(String name, Object component);
    
    /**
     * Gets the extension's data directory.
     */
    Path getDataDirectory();
    
    /**
     * Gets a logger for the extension.
     */
    Logger getLogger();
}
```

## Best Practices

### 1. Follow Naming Conventions

- **Extension IDs**: Use reverse domain notation (e.g., `com.example.my-extension`)
- **Function Names**: Use camelCase (e.g., `titleCase`)
- **Rule Names**: Use lowercase with hyphens (e.g., `email-address`)

### 2. Handle Errors Gracefully

```java
@Override
public String apply(String input, MappingContext context) {
    try {
        // Transformation logic
        return transform(input);
    } catch (Exception e) {
        // Log error
        context.getLogger().error("Transformation failed", e);
        // Return safe default or rethrow as MappingException
        throw new MappingException("Transformation failed: " + e.getMessage(), e);
    }
}
```

### 3. Document Your Extension

Provide comprehensive JavaDoc:

```java
/**
 * Converts a string to title case.
 * <p>
 * This function capitalizes the first letter of each word and converts
 * the remaining letters to lowercase.
 * </p>
 * <p>
 * Example:
 * <pre>
 * titleCase("hello world") → "Hello World"
 * titleCase("HELLO WORLD") → "Hello World"
 * </pre>
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class TitleCaseFunction implements MappingFunction<String, String> {
    // Implementation
}
```

### 4. Write Tests

```java
public class TitleCaseFunctionTest {
    
    private TitleCaseFunction function;
    private MappingContext context;
    
    @BeforeEach
    public void setUp() {
        function = new TitleCaseFunction();
        context = mock(MappingContext.class);
    }
    
    @Test
    public void testTitleCase_WithLowercase_ReturnsCapitalized() {
        String result = function.apply("hello world", context);
        assertEquals("Hello World", result);
    }
    
    @Test
    public void testTitleCase_WithNull_ReturnsNull() {
        String result = function.apply(null, context);
        assertNull(result);
    }
}
```

### 5. Version Your Extensions

Use semantic versioning (MAJOR.MINOR.PATCH):

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

### 6. Declare Dependencies

Explicitly declare dependencies in `extension.yml`:

```yaml
dependencies:
  - extensionId: core-extensions
    version: ">=1.0.0,<2.0.0"
  - extensionId: util-extensions
    version: "^1.2.0"
```

## Example: Complete Extension

Here's a complete example extension that adds JSON path support:

```java
package com.example.jsonpath;

import com.project.extension.api.*;
import com.project.mapping.api.*;
import com.jayway.jsonpath.JsonPath;

@Extension(
    id = "jsonpath-extension",
    name = "JSON Path Extension",
    version = "1.0.0"
)
public class JsonPathExtension implements ExtensionPoint {
    
    @Override
    public void initialize(ExtensionContext context) {
        context.registerMappingFunction(new JsonPathFunction());
    }
}

/**
 * Mapping function that extracts values from JSON using JSON Path.
 */
class JsonPathFunction implements MappingFunction<String, Object> {
    
    @Override
    public String getName() {
        return "jsonPath";
    }
    
    @Override
    public Object apply(String input, MappingContext context) 
            throws MappingException {
        try {
            String path = context.getParameter("path", String.class);
            if (path == null) {
                throw new MappingException("path parameter is required");
            }
            return JsonPath.read(input, path);
        } catch (Exception e) {
            throw new MappingException("JSON Path extraction failed", e);
        }
    }
    
    @Override
    public Class<String> getInputType() {
        return String.class;
    }
    
    @Override
    public Class<Object> getOutputType() {
        return Object.class;
    }
}
```

## Troubleshooting

### Extension Not Loading

1. Check extension descriptor is valid YAML
2. Verify `extension.yml` is in `META-INF/` directory
3. Check logs for error messages
4. Ensure dependencies are satisfied

### ClassNotFoundException

1. Ensure all dependencies are included in JAR
2. Check dependency versions match
3. Use Maven Shade Plugin to create fat JAR if needed

### Extension Conflicts

1. Check for duplicate extension IDs
2. Verify no naming conflicts for functions/rules
3. Review extension initialization order

## Resources

- Extension API JavaDoc: http://docs.example.com/extension-api
- Example Extensions: https://github.com/example/extensions
- Community Extensions: https://extensions.example.com
