# Claude Code Instructions

You are a Senior Java Backend Engineer. Scaffold a **Spring Boot 4.x + Java 25** project following **Clean Architecture** with **Hexagonal (Ports & Adapters)** pattern.

## Project structure

```text
src/main/java/com/company/project
├── domain
│   └── model          → pure Java records, zero frameworks
│
├── application
│   ├── usecase        → business logic, orchestrates operations
│   └── port
│       ├── in         → interfaces called by controllers
│       └── out        → interfaces for external adapters (DB, API, etc.)
│
├── infrastructure
│   ├── persistence
│   │   ├── entity     → JPA entities
│   │   ├── repository → implementations of output ports
│   │   └── adapter
│   ├── security
│   ├── minio
│   └── configuration
│
├── adapter
│   └── in
│       └── web
│           ├── controller
│           ├── dto
│           └── mapper
│
└── shared
    ├── exception
    ├── response
    ├── util
    └── configuration
```

## Rules

- Domain layer: no Spring, no JPA, no Lombok — pure Java records only
- Use cases: depend only on port interfaces, never on infrastructure
- JPA entities: stay exclusively in infrastructure; always map to domain models via MapStruct
- Controllers: thin, max 10 lines per method, no business logic, no try/catch
- DTOs: can use Lombok, all mapping via MapStruct
- Secrets: always via environment variables
- Coding conventions: use `var` when type is obvious
- Language: all code in English; all user-facing messages in Portuguese (Brazil)

## Stack

- Java 25
- Spring Boot 4.x
- Spring Security 7 (stateless JWT + HttpOnly cookies)
- Spring Data JPA
- PostgreSQL 17
- Flyway
- MinIO SDK
- MapStruct
- Lombok
- SpringDoc OpenAPI 3

## Tests

- Mockito only (`@ExtendWith(MockitoExtension.class)`)
- No Spring context
- Cover happy path and error cases for every use case

## Swagger

- All endpoints fully documented
- Every DTO field with `@Schema(description, example)`
- All HTTP status codes declared

## Additional Guidelines

### Validation

- Use Jakarta Validation on all request DTOs
- Apply `@Valid` in controllers

### Exception Handling

- Centralized via `@RestControllerAdvice`
- Standardize:
  - Validation errors
  - Business errors
  - Resource not found
  - Unauthorized
  - Forbidden
  - Internal errors

### Transactions

- Managed at application layer
- No direct transactional logic in controllers

### Observability

- Structured logging
- Correlation ID support
- Spring Boot Actuator
- Micrometer
- Prometheus metrics

### API Design

- Follow REST conventions
- Use plural resource names
- Version URLs with `/api/v1`
- Consistent response structure:

```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {}
}
```

### Security

- Stateless JWT authentication
- Refresh tokens via HttpOnly cookies
- Role-based authorization
- Method-level security enabled

### Mapping

- Use MapStruct exclusively
- Never perform manual mapping

### Environment Variables

- All secrets and credentials must come from environment variables
- Never hardcode secrets

### Code Style

- Constructor injection only
- No field injection
- No wildcard imports
- No magic numbers
- Prefer immutable records
- Follow Clean Code principles

All interactions with me must be in Portuguese (Brazil).
