# Grammar Customization Guide

## Overview

This guide explains how to define, customize, and work with grammar definitions in the system. The grammar engine provides a flexible DSL (Domain-Specific Language) for defining parsing rules.

## Grammar Definition Language

### Basic Syntax

Grammar definitions use a declarative syntax:

```
rule_name: pattern | pattern2 | pattern3;
```

### Example Grammar

```
// Simple expression grammar
expression: term (('+' | '-') term)*;
term: factor (('*' | '/') factor)*;
factor: number | '(' expression ')';
number: [0-9]+;
```

## Grammar Elements

### 1. Rules

Rules are the building blocks of a grammar.

#### Simple Rule

```
identifier: [a-zA-Z_][a-zA-Z0-9_]*;
```

#### Rule with Alternatives

```
operator: '+' | '-' | '*' | '/';
```

#### Rule with Sequences

```
assignment: identifier '=' expression ';';
```

### 2. Terminals

Terminals are literal values or patterns.

#### String Literals

```
keyword: 'if' | 'else' | 'while';
```

#### Character Classes

```
digit: [0-9];
letter: [a-zA-Z];
alphanumeric: [a-zA-Z0-9];
```

#### Regular Expressions

```
email: /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/;
```

### 3. Operators

#### Sequence (implicit)

```
rule: element1 element2 element3;
```

#### Alternative (|)

```
rule: option1 | option2 | option3;
```

#### Optional (?)

```
rule: required_element optional_element?;
```

#### Zero or More (*)

```
rule: element*;
```

#### One or More (+)

```
rule: element+;
```

#### Grouping (())

```
rule: (group1 | group2) common_suffix;
```

### 4. Special Elements

#### Whitespace Handling

```
@whitespace: [ \t\r\n]+;  // Define what counts as whitespace
@skip: whitespace;         // Skip whitespace automatically
```

#### Comments

```
@comment: '//' [^\n]* '\n';
@skip: comment;
```

#### Case Sensitivity

```
@case_sensitive: false;  // Case-insensitive matching
```

## Complete Example: JSON Grammar

```
// JSON Grammar Definition

@whitespace: [ \t\r\n]+;
@skip: whitespace;

// Root rule
json: value;

// Value types
value: object 
     | array 
     | string 
     | number 
     | 'true' 
     | 'false' 
     | 'null';

// Object
object: '{' members? '}';
members: pair (',' pair)*;
pair: string ':' value;

// Array
array: '[' elements? ']';
elements: value (',' value)*;

// String
string: '"' characters '"';
characters: character*;
character: [^"\\] | '\\' escape;
escape: '"' | '\\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' 
      | 'u' [0-9a-fA-F]{4};

// Number
number: integer fraction? exponent?;
integer: '-'? ('0' | [1-9] [0-9]*);
fraction: '.' [0-9]+;
exponent: [eE] [+-]? [0-9]+;
```

## Programmatic Grammar Definition

### Using the Grammar Builder API

```java
import com.project.grammar.api.*;

public class GrammarExample {
    
    public static Grammar createExpressionGrammar() {
        GrammarBuilder builder = new GrammarBuilder();
        
        // Define rules
        builder.rule("expression")
               .sequence("term")
               .zeroOrMore(
                   builder.sequence()
                          .alternative("+", "-")
                          .element("term")
               );
        
        builder.rule("term")
               .sequence("factor")
               .zeroOrMore(
                   builder.sequence()
                          .alternative("*", "/")
                          .element("factor")
               );
        
        builder.rule("factor")
               .alternative(
                   builder.element("number"),
                   builder.sequence("(", "expression", ")")
               );
        
        builder.rule("number")
               .pattern("[0-9]+");
        
        return builder.build();
    }
}
```

### Loading Grammar from File

```java
public class GrammarLoader {
    
    public static Grammar loadGrammar(String filePath) {
        GrammarParser parser = new GrammarParser();
        
        try (Reader reader = new FileReader(filePath)) {
            return parser.parse(reader);
        } catch (IOException | GrammarException e) {
            throw new RuntimeException("Failed to load grammar", e);
        }
    }
}
```

## Parsing with Grammar

### Basic Parsing

```java
public class ParserExample {
    
    public static void main(String[] args) {
        // Load grammar
        Grammar grammar = loadGrammar("expression.grammar");
        
        // Create parser
        Parser parser = new Parser(grammar);
        
        // Parse input
        String input = "2 + 3 * 4";
        AST ast = parser.parse(input);
        
        // Work with AST
        System.out.println("Parsed: " + ast.toTreeString());
    }
}
```

### Parsing with Options

```java
public class AdvancedParserExample {
    
    public static AST parseWithOptions(String input) {
        Grammar grammar = loadGrammar("grammar.txt");
        
        ParserOptions options = ParserOptions.builder()
            .maxDepth(100)
            .cacheEnabled(true)
            .errorRecovery(true)
            .validateOnly(false)
            .includeWhitespace(false)
            .build();
        
        Parser parser = new Parser(grammar, options);
        return parser.parse(input);
    }
}
```

## Custom Grammar Rules

### Creating a Custom Rule

```java
package com.example.grammar;

import com.project.grammar.api.*;

/**
 * Custom grammar rule for IPv4 addresses.
 */
public class IPv4Rule implements GrammarRule {
    
    private static final String IPV4_PATTERN = 
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    
    @Override
    public String getName() {
        return "ipv4";
    }
    
    @Override
    public boolean matches(String input) {
        return input != null && input.matches(IPV4_PATTERN);
    }
    
    @Override
    public ASTNode parse(String input, ParserContext context) 
            throws GrammarException {
        if (!matches(input)) {
            throw new GrammarException("Invalid IPv4 address: " + input);
        }
        
        String[] parts = input.split("\\.");
        return new IPv4Node(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]),
            Integer.parseInt(parts[3])
        );
    }
}
```

### Registering Custom Rules

```java
public class GrammarConfiguration {
    
    public void configureCustomRules(GrammarEngine engine) {
        // Register custom rules
        engine.registerRule(new IPv4Rule());
        engine.registerRule(new EmailRule());
        engine.registerRule(new URLRule());
    }
}
```

### Using Custom Rules in Grammar

```
// Grammar file with custom rules
@import: "com.example.grammar.IPv4Rule";

network_config: ipv4 ':' port;
port: [0-9]{1,5};
```

## Grammar Validation

### Validating Grammar Definitions

```java
public class GrammarValidation {
    
    public static ValidationResult validateGrammar(Grammar grammar) {
        GrammarValidator validator = new GrammarValidator();
        return validator.validate(grammar);
    }
    
    public static void main(String[] args) {
        Grammar grammar = loadGrammar("my-grammar.txt");
        ValidationResult result = validateGrammar(grammar);
        
        if (!result.isValid()) {
            System.err.println("Grammar validation failed:");
            for (ValidationError error : result.getErrors()) {
                System.err.println("  - " + error.getMessage());
            }
        }
    }
}
```

### Common Validation Errors

1. **Undefined Rules**: Reference to non-existent rule
2. **Left Recursion**: Direct or indirect left recursion
3. **Ambiguity**: Multiple parse trees for same input
4. **Unreachable Rules**: Rules never referenced
5. **Infinite Loops**: Rules with zero-width matches

## Grammar Transformations

### AST Visitors

```java
public class ASTVisitor {
    
    public void visit(AST ast) {
        visitNode(ast.getRoot());
    }
    
    private void visitNode(ASTNode node) {
        // Pre-order processing
        processNode(node);
        
        // Visit children
        for (ASTNode child : node.getChildren()) {
            visitNode(child);
        }
        
        // Post-order processing
        postProcessNode(node);
    }
    
    protected void processNode(ASTNode node) {
        // Override in subclass
    }
    
    protected void postProcessNode(ASTNode node) {
        // Override in subclass
    }
}
```

### Example: Expression Evaluator

```java
public class ExpressionEvaluator extends ASTVisitor {
    
    private Stack<Double> stack = new Stack<>();
    
    public double evaluate(AST ast) {
        visit(ast);
        return stack.pop();
    }
    
    @Override
    protected void postProcessNode(ASTNode node) {
        switch (node.getType()) {
            case "number":
                stack.push(Double.parseDouble(node.getText()));
                break;
            case "addition":
                double b = stack.pop();
                double a = stack.pop();
                stack.push(a + b);
                break;
            case "multiplication":
                b = stack.pop();
                a = stack.pop();
                stack.push(a * b);
                break;
            // Handle other operators
        }
    }
}
```

## Performance Optimization

### 1. Caching

```java
ParserOptions options = ParserOptions.builder()
    .cacheEnabled(true)
    .cacheSize(1000)
    .build();
```

### 2. Memoization

```java
@Memoize
public ASTNode parseExpression(String input, int position) {
    // Parsing logic
}
```

### 3. Lazy Evaluation

```java
public class LazyGrammar {
    
    private Supplier<Grammar> grammarSupplier;
    private Grammar grammar;
    
    public Grammar getGrammar() {
        if (grammar == null) {
            grammar = grammarSupplier.get();
        }
        return grammar;
    }
}
```

## Error Handling

### Custom Error Messages

```java
public class CustomGrammarException extends GrammarException {
    
    private final int line;
    private final int column;
    
    public CustomGrammarException(String message, int line, int column) {
        super(String.format("%s at line %d, column %d", 
                            message, line, column));
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
}
```

### Error Recovery

```java
public class ErrorRecoveryParser extends Parser {
    
    @Override
    protected ASTNode handleError(GrammarException e, ParserContext context) {
        // Log error
        context.recordError(e);
        
        // Skip to next valid token
        skipToSynchronizationPoint(context);
        
        // Return error node
        return new ErrorNode(e.getMessage());
    }
}
```

## Best Practices

1. **Keep Rules Simple**: Break complex rules into smaller ones
2. **Avoid Left Recursion**: Use right recursion or iteration
3. **Use Whitespace Handling**: Define @skip rules for whitespace
4. **Document Grammar**: Add comments to explain complex rules
5. **Test Thoroughly**: Test with valid and invalid inputs
6. **Version Grammars**: Track grammar changes like code
7. **Optimize Carefully**: Profile before optimizing

## Examples

See the `examples/grammars/` directory for complete examples:

- `expression.grammar` - Mathematical expressions
- `json.grammar` - JSON parser
- `sql.grammar` - SQL query parser
- `markdown.grammar` - Markdown parser
