# ADR-002: Grammar Definition DSL

## Status

Accepted

## Date

2024-01-01

## Context

We need a way for users to define custom grammars for parsing. The system must support:

- Easy-to-learn grammar syntax
- Expressive power for complex grammars
- Clear error messages
- Tool support (syntax highlighting, validation)
- Extensibility for custom rules

Users range from developers to domain experts, so the DSL must balance power with usability.

Key requirements:
- Define lexical rules (tokens)
- Define syntax rules (grammar structure)
- Support alternatives, sequences, repetition
- Handle whitespace and comments
- Allow custom rule types through extensions

## Decision

We will implement a **custom BNF-like DSL** for grammar definitions with the following syntax:

```
rule_name: pattern | alternative;
```

Key features:
- **BNF-style notation**: Familiar to developers
- **Regular expression support**: For terminal patterns
- **Operators**: `|` (alternative), `*` (zero or more), `+` (one or more), `?` (optional)
- **Grouping**: Parentheses for grouping
- **String literals**: Single or double quotes
- **Character classes**: Square brackets `[a-z]`
- **Comments**: `//` for single-line, `/* */` for multi-line
- **Directives**: `@` prefix for special directives (`@whitespace`, `@skip`, etc.)

Example:
```
@whitespace: [ \t\r\n]+;
@skip: whitespace;

expression: term (('+' | '-') term)*;
term: factor (('*' | '/') factor)*;
factor: number | '(' expression ')';
number: [0-9]+;
```

## Consequences

### Positive

- **Familiarity**: BNF notation is well-known and documented
- **Readability**: Clear, declarative syntax
- **Expressiveness**: Supports complex grammar patterns
- **Tooling**: Can build syntax highlighters, validators, auto-completion
- **Extensibility**: Directives allow adding features without syntax changes
- **Self-documenting**: Grammar definitions serve as documentation
- **Version Control Friendly**: Text-based format works well with Git

### Negative

- **Learning Curve**: Users unfamiliar with BNF need to learn notation
- **Parser Complexity**: Need to implement a parser for the DSL itself
- **Error Messages**: Must provide clear error messages for DSL syntax errors
- **Tooling Effort**: Need to build editor support from scratch
- **Ambiguity Handling**: Left recursion and ambiguity must be detected and reported

### Neutral

- **Not Standards-Compliant**: Custom DSL vs standard EBNF or ABNF
- **Custom Implementation**: We control evolution but must maintain parser

## Alternatives Considered

### Alternative 1: ANTLR Grammar Format

**Pros:**
- Industry standard
- Excellent tooling
- Proven and mature
- Large community

**Cons:**
- More complex syntax
- Steep learning curve
- Tight coupling to ANTLR
- Less flexibility for custom extensions

**Why not chosen:**
- Too complex for our target users
- Want more control over grammar processing
- Don't need full power of ANTLR

### Alternative 2: JSON/YAML Configuration

```json
{
  "rules": [
    {
      "name": "expression",
      "pattern": {
        "type": "sequence",
        "elements": ["term", {"repeat": "zeroOrMore", "of": "operation"}]
      }
    }
  ]
}
```

**Pros:**
- Easy to parse
- Familiar format
- Tool support exists

**Cons:**
- Verbose and unreadable
- Not intuitive for grammar definitions
- Difficult to express complex patterns
- Poor developer experience

**Why not chosen:**
- Terrible user experience for grammar definitions
- Not human-friendly
- Difficult to maintain

### Alternative 3: Programmatic API Only

```java
Grammar.builder()
    .rule("expression")
        .sequence("term")
        .zeroOrMore(alternatives("+", "-"), "term")
    .build();
```

**Pros:**
- Type-safe
- IDE support
- No parsing needed

**Cons:**
- Not declarative
- Verbose
- Difficult for non-programmers
- Poor for version control diffs
- No visual overview of grammar

**Why not chosen:**
- Poor usability for grammar authoring
- Want declarative, text-based format
- Need non-programmers to define grammars

### Alternative 4: Regular Expressions Only

**Pros:**
- Well-known
- Powerful for tokens
- No learning curve

**Cons:**
- Cannot express recursive structures
- Not suitable for complex grammars
- Difficult to compose
- Limited error messages

**Why not chosen:**
- Insufficient for hierarchical grammar structures
- Cannot handle nested constructs
- Need full grammar specification capability

## Related Decisions

- [ADR-001: Use Java for Backend Development](001-java-backend.md) - Java chosen to implement DSL parser
- [ADR-003: Mapping Rule Engine Architecture](003-mapping-engine.md) - Similar DSL approach for mapping rules
- [ADR-004: Extension Plugin System](004-extension-system.md) - Extensions can add custom grammar rule types

## References

- [BNF Notation](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form)
- [ANTLR Documentation](https://www.antlr.org/)
- [Parsing Expression Grammars](https://en.wikipedia.org/wiki/Parsing_expression_grammar)
- [Language Implementation Patterns (book)](https://pragprog.com/titles/tpdsl/)
