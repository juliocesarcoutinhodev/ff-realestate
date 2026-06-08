# FF Realestate — API

Backend da plataforma FF Realestate. API REST construída com Spring Boot 4 + Java 25, seguindo Clean Architecture com padrão Hexagonal (Ports & Adapters) em arquitetura de monolito modular (Spring Modulith).

---

## Stack

| Tecnologia | Versão |
|---|---|
| Java | 25 |
| Spring Boot | 4.x |
| PostgreSQL | 17 |
| MinIO | SDK 8.5 |
| Flyway | — |
| MapStruct | 1.6 |
| SpringDoc OpenAPI | 2.8 |

---

## Pré-requisitos

- Java 25
- Maven 3.9+ (ou use o `./mvnw` incluso)
- Docker + Docker Compose

---

## Rodando localmente

### 1. Infraestrutura (PostgreSQL + MinIO)

```bash
docker compose -f ../docker-compose.local.yml --env-file .env.local up -d
```

### 2. Variáveis de ambiente

Copie o arquivo de exemplo e preencha os valores:

```bash
cp .env.example .env.local
```

Os valores padrão do `.env.example` já funcionam para o ambiente local sem alteração.

### 3. Aplicação

Execute pela IDE (IntelliJ IDEA recomendado) ou via Maven:

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

---

## Variáveis de ambiente

| Variável | Descrição | Obrigatória |
|---|---|---|
| `DB_URL` | JDBC URL do PostgreSQL | Sim |
| `DB_USERNAME` | Usuário do banco | Sim |
| `DB_PASSWORD` | Senha do banco | Sim |
| `JWT_SECRET` | Chave HMAC256 (mín. 32 caracteres) | Sim |
| `JWT_EXPIRATION_MS` | Expiração do access token em ms | Não (padrão: 86400000) |
| `JWT_REFRESH_EXPIRATION_MS` | Expiração do refresh token em ms | Não (padrão: 604800000) |
| `MINIO_ENDPOINT` | URL do servidor MinIO | Sim |
| `MINIO_ACCESS_KEY` | Access key do MinIO | Sim |
| `MINIO_SECRET_KEY` | Secret key do MinIO | Sim |
| `MINIO_BUCKET` | Nome do bucket padrão | Não (padrão: ffrealestate) |
| `MINIO_PUBLIC_URL` | URL pública base para links de arquivos | Sim |
| `ALLOWED_ORIGINS` | Origens permitidas no CORS (separadas por vírgula) | Não (padrão: localhost:4200) |
| `COOKIE_DOMAIN` | Domínio dos cookies de autenticação | Não (padrão: localhost) |

Consulte `.env.example` para exemplos de valores por ambiente.

---

## Arquitetura

O projeto segue **Clean Architecture** com **Hexagonal Architecture (Ports & Adapters)**:

```
br.com.fabriciofaceroli
│
├── {domain}/               → módulo de domínio (auth, property, category, photo, testimonial)
│   ├── domain/model/       → records Java puros, sem frameworks
│   ├── application/
│   │   ├── usecase/        → lógica de negócio
│   │   └── port/in|out/    → interfaces de entrada e saída
│   ├── infrastructure/     → adapters de persistência (JPA)
│   └── adapter/in/web/     → controllers, DTOs, mappers
│
├── infrastructure/         → adapters globais de infraestrutura
│   ├── security/           → JWT, cookies, Spring Security
│   ├── minio/              → adapter de armazenamento de arquivos
│   └── configuration/      → CORS, OpenAPI, health check
│
└── shared/                 → utilitários agnósticos de framework
    ├── exception/          → tipos de exceção e handler global
    └── response/           → envelopes de resposta da API
```

**Regras de dependência:**
- `domain` não conhece nenhum framework
- `application` depende apenas de interfaces de porta
- `infrastructure` implementa as portas de saída
- `adapter/web` implementa as portas de entrada

---

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| GET | `/api/v1/health` | Não | Health check |
| POST | `/api/v1/auth/login` | Não | Autenticação |
| POST | `/api/v1/auth/register` | Sim | Criação de novo admin |
| GET | `/api/v1/properties` | Não | Lista imóveis |
| GET | `/api/v1/categories` | Não | Lista categorias |
| GET | `/api/v1/testimonials` | Não | Lista depoimentos |
| * | demais rotas | Sim | Bearer token |

Documentação completa: `http://localhost:8080/swagger-ui.html`

---

## Testes

```bash
./mvnw test
```

Testes unitários com Mockito (`@ExtendWith(MockitoExtension.class)`). Sem Spring context.

---

## Postman

Importe os arquivos em `doc/postman/`:

1. `ff-realestate.postman_collection.json` — collection completa
2. `local.postman_environment.json` — environment local
3. `prod.postman_environment.json` — template de produção

Selecione o environment **Local**, execute **Auth › Login** e o token é salvo automaticamente a partir do `Set-Cookie` para todos os demais requests.

Fluxo recomendado:

1. `Auth › Login` autentica e grava o JWT em cookie HttpOnly
2. `Auth › Register Admin User` usa o token salvo na collection para criar novos admins

---

## Banco de dados

Migrações gerenciadas pelo **Flyway** em `src/main/resources/db/migration/`.

Convenção de nomenclatura: `V{versão}__{descrição}.sql`

---

## Deploy

```bash
docker compose -f ../docker-compose.prod.yml --env-file .env up -d
```

Preencha `api/.env` com os valores de produção antes do deploy.
