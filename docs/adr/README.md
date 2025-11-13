# Architecture Decision Records (ADRs)

## Overview

This directory contains Architecture Decision Records (ADRs) for significant architectural decisions made in the project.

## What is an ADR?

An Architecture Decision Record (ADR) is a document that captures an important architectural decision made along with its context and consequences.

## ADR Format

Each ADR follows this structure:

1. **Title**: Short noun phrase
2. **Status**: Proposed, Accepted, Deprecated, Superseded
3. **Context**: What is the issue we're seeing that is motivating this decision?
4. **Decision**: What is the change that we're proposing/doing?
5. **Consequences**: What becomes easier or more difficult to do because of this change?

## ADR Index

- [ADR-001: Use Java for Backend Development](./001-java-backend.md)
- [ADR-002: Grammar Definition DSL](./002-grammar-dsl.md)
- [ADR-003: Mapping Rule Engine Architecture](./003-mapping-engine.md)
- [ADR-004: Extension Plugin System](./004-extension-system.md)
- [ADR-005: REST API Design](./005-rest-api-design.md)
- [ADR-006: Frontend Framework Selection](./006-frontend-framework.md)
- [ADR-007: Database Choice](./007-database-choice.md)
- [ADR-008: Caching Strategy](./008-caching-strategy.md)
- [ADR-009: Testing Strategy](./009-testing-strategy.md)
- [ADR-010: Security and Authentication](./010-security-authentication.md)

## Creating a New ADR

1. Copy the [template](./template.md)
2. Name it with the next number: `NNN-title-with-dashes.md`
3. Fill in the sections
4. Submit for review
5. Update this index

## ADR Lifecycle

1. **Proposed**: Under discussion
2. **Accepted**: Decision has been made and is active
3. **Deprecated**: No longer recommended but still supported
4. **Superseded**: Replaced by a newer ADR
