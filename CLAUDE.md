# Claude Code Instructions

You are a Senior Java Backend Engineer. Every implementation must follow **Clean Architecture** with **Hexagonal (Ports & Adapters)** pattern as a **Spring Modulith modular monolith**.

## Base package

```
br.com.fabriciofaceroli
```

## Project structure

```text
br.com.fabriciofaceroli
│
├── {domain}/                        → one top-level package per business domain (Spring Modulith module)
│   ├── domain/
│   │   └── model/                   → pure Java records — zero frameworks, zero annotations
│   ├── application/
│   │   ├── usecase/                 → orchestrates domain operations, owns @Transactional
│   │   └── port/
│   │       ├── in/                  → input ports: interfaces implemented by use cases
│   │       └── out/                 → output ports: interfaces implemented by infrastructure adapters
│   ├── infrastructure/
│   │   └── persistence/
│   │       ├── entity/              → JPA entities (framework-specific, never exposed to domain)
│   │       ├── repository/          → Spring Data JPA interfaces
│   │       └── adapter/             → output port implementations (repository adapters)
│   └── adapter/
│       └── in/
│           └── web/
│               ├── controller/      → REST controllers (thin, max 10 lines per method)
│               ├── dto/             → request/response DTOs (Lombok + Jakarta Validation)
│               └── mapper/          → MapStruct mappers (DTO ↔ domain model)
│
├── infrastructure/                  → global infrastructure adapters (Spring Modulith module)
│   ├── security/                    → JWT, cookies, Spring Security
│   │   ├── JwtProperties            → @ConfigurationProperties lives here, next to its consumer
│   │   ├── CookieProperties         → @ConfigurationProperties lives here, next to its consumer
│   │   ├── JwtService
│   │   ├── CookieService
│   │   ├── JwtAuthenticationFilter
│   │   └── SecurityConfig
│   ├── minio/                       → MinIO storage adapter
│   │   ├── MinioProperties          → @ConfigurationProperties lives here, next to its consumer
│   │   ├── MinioConfig
│   │   └── MinioService
│   └── configuration/               → cross-cutting infrastructure config (CORS, OpenAPI, etc.)
│       ├── CorsConfig
│       ├── OpenApiConfig
│       └── EnvFileLogger
│
└── shared/                          → framework-agnostic cross-cutting concerns ONLY
    ├── exception/                   → custom exception types + global handler
    │   ├── BusinessException
    │   ├── ResourceNotFoundException
    │   └── GlobalExceptionHandler
    └── response/                    → API response/error wrappers
        ├── ApiResponse
        └── ErrorResponse
```

## Architectural rules — non-negotiable

### Dependency Rule (Clean Architecture)
Dependencies flow **inward only**:
```
adapter/web → application/port/in → domain
infrastructure/adapter → application/port/out → domain
```
- Domain knows nothing about Spring, JPA, HTTP, or any framework
- Use cases depend only on port interfaces — never on infrastructure classes directly
- Controllers depend only on input port interfaces — never on use cases directly

### `shared/` — framework-agnostic only
`shared/` is a cross-cutting utility layer. It must contain **only framework-agnostic code**:
- Custom exception types (`extends RuntimeException`)
- `@RestControllerAdvice` for global exception handling (only allowed Spring annotation here)
- API response record wrappers (`ApiResponse`, `ErrorResponse`)

**Never put in `shared/`:**
- `@Configuration`, `@Service`, `@Component`, `@Bean` classes
- `@ConfigurationProperties` records
- Spring Security, JWT, cookie, or any infrastructure logic

### `@ConfigurationProperties` placement
Every `@ConfigurationProperties` record **lives in the same `infrastructure` sub-package as the beans that consume it**:

| Record | Location |
|---|---|
| `JwtProperties`, `CookieProperties` | `infrastructure/security/` |
| `MinioProperties` | `infrastructure/minio/` |
| Future DB-specific config | `{domain}/infrastructure/persistence/` |

Never place `@ConfigurationProperties` in `shared/`.

### Spring Modulith module boundaries
- `shared/` is declared `@ApplicationModule(type = OPEN)` — all sub-packages are accessible to every module
- `infrastructure/` sub-packages are internal — future cross-module access goes through output ports
- Domain modules expose their API only via input port interfaces (no direct bean injection across modules)

### Domain layer
- Pure Java `record` types only
- Zero framework annotations (`@Entity`, `@Column`, `@JsonProperty` — all forbidden)
- No Lombok — records are already immutable and concise

### Application layer
- Use cases implement input port interfaces
- Depend only on output port interfaces (never on JPA repositories or infrastructure directly)
- Own `@Transactional` — no transactions in controllers or infrastructure adapters
- No try/catch — exceptions propagate to `GlobalExceptionHandler`

### Infrastructure layer
- JPA entities stay exclusively here — always mapped to/from domain models via MapStruct
- Output port adapters implement the interfaces defined in `application/port/out/`
- All `@ConfigurationProperties` records live here, next to their consumers

### Adapter layer (web)
- Controllers are thin: delegate immediately to input port, return `ApiResponse`
- Max 10 lines per method — no business logic, no try/catch
- All DTOs validated with Jakarta Validation (`@Valid` in controller)
- All mapping via MapStruct — never manual

## Stack

- Java 25
- Spring Boot 4.x
- Spring Security 7 (stateless JWT + HttpOnly cookies)
- Spring Data JPA
- PostgreSQL 17
- Flyway
- MinIO SDK
- MapStruct
- Lombok (DTOs and infrastructure only — never in domain)
- SpringDoc OpenAPI 3
- Spring Modulith

## Tests

- **Unit tests only** — `@ExtendWith(MockitoExtension.class)`, no Spring context (`@SpringBootTest` is forbidden)
- `src/test/resources/logback-test.xml` silences application logs — stack traces during error scenarios are noise, not signal
- Cover happy path **and** every error case for each use case
- Test class name mirrors the class under test: `MinioService` → `MinioServiceTest`

## Swagger

- All endpoints fully documented
- Every DTO field annotated with `@Schema(description = "...", example = "...")`
- All HTTP status codes declared with `@ApiResponse`

## Additional guidelines

### Validation
- Jakarta Validation on all request DTOs
- `@Valid` on every controller method parameter

### Exception handling
- Centralized in `GlobalExceptionHandler` (`@RestControllerAdvice` in `shared/exception/`)
- Handles: validation errors, business errors, not found, conflict, unauthorized, forbidden, internal
- No try/catch in controllers or use cases — let exceptions bubble up

### Transactions
- `@Transactional` at use case level only
- Never in controllers, never in infrastructure adapters

### Observability
- Structured logging with `@Slf4j`
- Correlation ID support
- Spring Boot Actuator + Micrometer + Prometheus

### API design
- REST conventions, plural resource names
- Version prefix: `/api/v1`
- Consistent response envelope:

```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {}
}
```

### Security
- Stateless JWT in `Authorization` header
- Refresh token via HttpOnly cookie (`accessToken`)
- Role-based authorization with method-level security (`@PreAuthorize`)

### Mapping
- MapStruct exclusively — zero manual mapping anywhere

### Environment variables
- All secrets via environment variables — never hardcoded
- `@ConfigurationProperties` with `@Validated` for startup-time validation
- Three env files: `.env.local` (dev values), `.env` (prod template, empty), `.env.example` (documented)
- Every new env var must be added to **all three files** simultaneously

### Code style
- Constructor injection only — no `@Autowired`, no field injection
- No wildcard imports
- No magic numbers — named constants only
- `var` when the type is obvious from the right-hand side
- Immutable records preferred over mutable classes in domain and config

---

All interactions must be in **Portuguese (Brazil)**.
