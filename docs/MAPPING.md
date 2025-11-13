# Mapping Rules Guide

## Overview

This guide explains how to define and use mapping rules for data transformation. The mapping engine provides a powerful and flexible system for transforming data between different formats and structures.

## Mapping Rule Basics

### Simple Field Mapping

```yaml
# mapping-rules.yml
mappings:
  - name: user_mapping
    description: Maps API user to database user
    rules:
      - source: firstName
        target: first_name
      
      - source: lastName
        target: last_name
      
      - source: email
        target: email_address
```

### Mapping with Transformation

```yaml
mappings:
  - name: user_mapping_with_transform
    rules:
      - source: firstName
        target: first_name
        transform: uppercase
      
      - source: email
        target: email_address
        transform: lowercase
```

## Transformation Functions

### Built-in Functions

#### String Functions

```yaml
rules:
  # Convert to uppercase
  - source: name
    target: NAME
    transform: uppercase
  
  # Convert to lowercase
  - source: email
    target: email
    transform: lowercase
  
  # Trim whitespace
  - source: description
    target: description
    transform: trim
  
  # Substring
  - source: text
    target: short_text
    transform:
      function: substring
      params:
        start: 0
        end: 10
  
  # Replace
  - source: phone
    target: formatted_phone
    transform:
      function: replace
      params:
        pattern: "[^0-9]"
        replacement: ""
  
  # Concatenate
  - target: full_name
    transform:
      function: concat
      params:
        values:
          - source: firstName
          - " "
          - source: lastName
```

#### Numeric Functions

```yaml
rules:
  # Round
  - source: price
    target: rounded_price
    transform:
      function: round
      params:
        decimals: 2
  
  # Math operations
  - source: quantity
    target: total
    transform:
      function: multiply
      params:
        multiplier: 10
  
  # Format currency
  - source: amount
    target: formatted_amount
    transform:
      function: currency
      params:
        symbol: "$"
        locale: "en_US"
```

#### Date Functions

```yaml
rules:
  # Parse date
  - source: dateString
    target: date
    transform:
      function: parseDate
      params:
        format: "yyyy-MM-dd"
  
  # Format date
  - source: timestamp
    target: formatted_date
    transform:
      function: formatDate
      params:
        format: "dd/MM/yyyy HH:mm:ss"
  
  # Add days
  - source: startDate
    target: endDate
    transform:
      function: addDays
      params:
        days: 7
  
  # Current timestamp
  - target: created_at
    transform: currentTimestamp
```

#### Collection Functions

```yaml
rules:
  # First element
  - source: items
    target: first_item
    transform: first
  
  # Last element
  - source: items
    target: last_item
    transform: last
  
  # Join array
  - source: tags
    target: tag_string
    transform:
      function: join
      params:
        separator: ", "
  
  # Split string
  - source: csv_data
    target: items
    transform:
      function: split
      params:
        separator: ","
  
  # Map over collection
  - source: users
    target: user_names
    transform:
      function: map
      params:
        field: name
```

### Custom Functions

#### Defining Custom Functions

```yaml
# Define custom functions
functions:
  - name: fullName
    description: Combines first and last name
    implementation: com.example.mapping.FullNameFunction
  
  - name: ageFromBirthdate
    description: Calculates age from birthdate
    implementation: com.example.mapping.AgeCalculator
```

#### Implementing Custom Functions

```java
package com.example.mapping;

import com.project.mapping.api.*;

public class FullNameFunction implements MappingFunction<Map<String, Object>, String> {
    
    @Override
    public String getName() {
        return "fullName";
    }
    
    @Override
    public String apply(Map<String, Object> input, MappingContext context) 
            throws MappingException {
        String firstName = (String) input.get("firstName");
        String lastName = (String) input.get("lastName");
        String middleName = (String) input.get("middleName");
        
        if (firstName == null || lastName == null) {
            throw new MappingException("firstName and lastName are required");
        }
        
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        
        return firstName + " " + lastName;
    }
    
    @Override
    public Class<Map<String, Object>> getInputType() {
        return (Class) Map.class;
    }
    
    @Override
    public Class<String> getOutputType() {
        return String.class;
    }
}
```

## Conditional Mapping

### Simple Conditions

```yaml
rules:
  - source: status
    target: status_code
    condition:
      field: type
      equals: "active"
    transform: uppercase
```

### Complex Conditions

```yaml
rules:
  - source: discount
    target: final_discount
    condition:
      and:
        - field: memberType
          equals: "premium"
        - field: orderTotal
          greaterThan: 100
    transform:
      function: multiply
      params:
        multiplier: 1.5
```

### Condition Operators

```yaml
# Equals
condition:
  field: status
  equals: "active"

# Not equals
condition:
  field: type
  notEquals: "archived"

# Greater than
condition:
  field: age
  greaterThan: 18

# Less than
condition:
  field: quantity
  lessThan: 100

# In list
condition:
  field: country
  in: ["US", "CA", "MX"]

# Regular expression
condition:
  field: email
  matches: ".*@example\\.com$"

# Exists
condition:
  field: optional_field
  exists: true

# Type check
condition:
  field: value
  type: "string"
```

### Logical Operators

```yaml
# AND
condition:
  and:
    - field: status
      equals: "active"
    - field: verified
      equals: true

# OR
condition:
  or:
    - field: type
      equals: "admin"
    - field: type
      equals: "moderator"

# NOT
condition:
  not:
    field: deleted
    equals: true

# Nested conditions
condition:
  and:
    - field: status
      equals: "active"
    - or:
        - field: role
          equals: "admin"
        - field: permissions
          contains: "write"
```

## Nested Object Mapping

### Simple Nested Mapping

```yaml
rules:
  # Source nested field
  - source: user.profile.name
    target: userName
  
  # Target nested field
  - source: email
    target: contact.email
  
  # Both nested
  - source: address.street
    target: shipping.address.street
```

### Complex Nested Mapping

```yaml
mappings:
  - name: order_mapping
    rules:
      # Map nested object
      - source: customer
        target: buyer
        mapping:
          rules:
            - source: name
              target: full_name
            - source: email
              target: email_address
      
      # Map array of objects
      - source: items
        target: order_items
        mapping:
          rules:
            - source: productId
              target: product_id
            - source: quantity
              target: qty
            - source: price
              target: unit_price
```

## Array Mapping

### Mapping Arrays

```yaml
rules:
  # Map each element
  - source: numbers
    target: doubled_numbers
    transform:
      function: mapArray
      params:
        function: multiply
        multiplier: 2
  
  # Filter array
  - source: users
    target: active_users
    transform:
      function: filter
      params:
        condition:
          field: status
          equals: "active"
  
  # Transform array
  - source: products
    target: product_names
    transform:
      function: pluck
      params:
        field: name
```

### Array Aggregation

```yaml
rules:
  # Sum
  - source: prices
    target: total_price
    transform: sum
  
  # Average
  - source: ratings
    target: average_rating
    transform: average
  
  # Count
  - source: items
    target: item_count
    transform: count
  
  # Min/Max
  - source: values
    target: max_value
    transform: max
```

## Default Values

### Static Defaults

```yaml
rules:
  - source: optional_field
    target: field_with_default
    default: "N/A"
  
  - source: count
    target: quantity
    default: 0
```

### Dynamic Defaults

```yaml
rules:
  # Use current timestamp if not provided
  - source: created_at
    target: created_timestamp
    default:
      function: currentTimestamp
  
  # Generate UUID if not provided
  - source: id
    target: unique_id
    default:
      function: uuid
```

## Multi-Source Mapping

### Combining Multiple Sources

```yaml
rules:
  - target: full_address
    sources:
      - street
      - city
      - state
      - zip
    transform:
      function: template
      params:
        template: "{0}, {1}, {2} {3}"
  
  - target: total_price
    sources:
      - base_price
      - tax
      - shipping
    transform: sum
```

## Template Mapping

### Using Templates

```yaml
rules:
  - target: message
    transform:
      function: template
      params:
        template: "Hello {{firstName}} {{lastName}}, your order #{{orderId}} is ready!"
        sources:
          firstName: user.firstName
          lastName: user.lastName
          orderId: order.id
```

## Programmatic Mapping

### Using the Mapping API

```java
import com.project.mapping.api.*;

public class MappingExample {
    
    public static void main(String[] args) {
        // Create mapping engine
        MappingEngine engine = new MappingEngine();
        
        // Load mapping rules
        MappingRules rules = MappingRules.fromFile("mapping-rules.yml");
        
        // Input data
        Map<String, Object> input = new HashMap<>();
        input.put("firstName", "John");
        input.put("lastName", "Doe");
        input.put("email", "JOHN.DOE@EXAMPLE.COM");
        
        // Execute mapping
        Map<String, Object> output = engine.execute(rules, input);
        
        // Output: {first_name=JOHN, last_name=DOE, email_address=john.doe@example.com}
        System.out.println(output);
    }
}
```

### Building Rules Programmatically

```java
public class MappingRuleBuilder {
    
    public static MappingRules buildUserMapping() {
        return MappingRules.builder()
            .name("user_mapping")
            .description("Maps API user to database user")
            .rule(rule -> rule
                .source("firstName")
                .target("first_name")
                .transform("uppercase")
            )
            .rule(rule -> rule
                .source("lastName")
                .target("last_name")
                .transform("uppercase")
            )
            .rule(rule -> rule
                .source("email")
                .target("email_address")
                .transform("lowercase")
            )
            .build();
    }
}
```

### Custom Mapping Context

```java
public class CustomMappingContext extends MappingContext {
    
    private final User currentUser;
    private final Map<String, Object> metadata;
    
    public CustomMappingContext(User currentUser) {
        this.currentUser = currentUser;
        this.metadata = new HashMap<>();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
}
```

## Validation

### Validation Rules

```yaml
mappings:
  - name: validated_mapping
    rules:
      - source: email
        target: email_address
        validation:
          - type: email
            message: "Invalid email format"
      
      - source: age
        target: age
        validation:
          - type: range
            min: 0
            max: 150
            message: "Age must be between 0 and 150"
      
      - source: phone
        target: phone_number
        validation:
          - type: pattern
            pattern: "^\\+?[1-9]\\d{1,14}$"
            message: "Invalid phone number"
```

### Custom Validators

```java
public class EmailValidator implements MappingValidator {
    
    @Override
    public ValidationResult validate(Object value, MappingContext context) {
        if (value == null) {
            return ValidationResult.success();
        }
        
        String email = value.toString();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ValidationResult.error("Invalid email format: " + email);
        }
        
        return ValidationResult.success();
    }
}
```

## Error Handling

### Handling Mapping Errors

```java
public class ErrorHandlingExample {
    
    public static Map<String, Object> safeMapping(
            MappingEngine engine,
            MappingRules rules,
            Map<String, Object> input) {
        
        try {
            return engine.execute(rules, input);
        } catch (MappingException e) {
            // Log error
            logger.error("Mapping failed", e);
            
            // Return partial results if available
            if (e.hasPartialResults()) {
                return e.getPartialResults();
            }
            
            // Return empty map
            return Collections.emptyMap();
        }
    }
}
```

### Error Recovery Strategies

```yaml
mappings:
  - name: resilient_mapping
    errorHandling:
      strategy: continue  # continue, stop, or partial
      onError: log       # log, ignore, or throw
    
    rules:
      - source: field1
        target: target1
        onError:
          strategy: skip
          default: null
      
      - source: field2
        target: target2
        onError:
          strategy: retry
          maxRetries: 3
```

## Performance Optimization

### Caching

```java
// Enable caching
MappingEngine engine = MappingEngine.builder()
    .cacheEnabled(true)
    .cacheSize(1000)
    .cacheTTL(Duration.ofMinutes(30))
    .build();
```

### Parallel Execution

```java
// Enable parallel mapping for large datasets
MappingEngine engine = MappingEngine.builder()
    .parallelEnabled(true)
    .threadPoolSize(10)
    .build();
```

### Lazy Evaluation

```yaml
rules:
  - source: expensive_computation
    target: result
    lazy: true  # Only evaluate if target is accessed
```

## Best Practices

1. **Keep Mappings Simple**: Break complex mappings into smaller, reusable pieces
2. **Use Descriptive Names**: Name rules and functions clearly
3. **Document Mappings**: Add descriptions to complex mappings
4. **Validate Input**: Use validation rules to ensure data quality
5. **Handle Errors Gracefully**: Define error handling strategies
6. **Test Thoroughly**: Test with various input scenarios
7. **Version Mappings**: Track mapping changes like code
8. **Optimize Carefully**: Profile before optimizing
9. **Use Types**: Leverage type checking where possible
10. **Reuse Functions**: Create reusable custom functions

## Examples

See the `examples/mappings/` directory for complete examples:

- `user-mapping.yml` - User data transformation
- `order-mapping.yml` - E-commerce order mapping
- `api-to-db-mapping.yml` - API to database mapping
- `etl-mapping.yml` - ETL data transformation
