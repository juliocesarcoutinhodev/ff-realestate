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
| `JWT_ACCESS_TOKEN_EXPIRATION` | Expiração do access token em segundos | Não (padrão: 900) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Expiração do refresh token em segundos | Não (padrão: 604800) |
| `MINIO_ENDPOINT` | URL do servidor MinIO | Sim |
| `MINIO_ACCESS_KEY` | Access key do MinIO | Sim |
| `MINIO_SECRET_KEY` | Secret key do MinIO | Sim |
| `MINIO_BUCKET` | Nome do bucket padrão | Não (padrão: ffrealestate) |
| `MINIO_PUBLIC_URL` | URL pública base para links de arquivos | Sim |
| `ALLOWED_ORIGINS` | Origens permitidas no CORS (separadas por vírgula) | Não (padrão: localhost:4200) |
| `COOKIE_DOMAIN` | Domínio dos cookies de autenticação | Não (padrão: localhost) |
| `COOKIE_SECURE` | Se os cookies devem usar a flag Secure | Não (padrão: false) |
| `ADMIN_DEFAULT_EMAIL` | E-mail do admin padrão criado pela migration | Sim |
| `ADMIN_DEFAULT_PASSWORD` | Senha do admin padrão (usada apenas na migration seed) | Sim |

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

### System

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| GET | `/api/v1/health` | Não | Health check |

### Auth

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Não | Autentica e emite `accessToken` + `refresh_token` em cookies HttpOnly |
| POST | `/api/v1/auth/register` | ADMIN | Registra novo administrador |
| POST | `/api/v1/auth/refresh` | Não | Renova o access token via cookie `refresh_token` |
| GET | `/api/v1/auth/me` | Sim | Valida sessão e retorna dados do usuário autenticado |
| POST | `/api/v1/auth/logout` | Sim | Revoga o refresh token e limpa os cookies |

### Categories

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| GET | `/api/v1/categories` | Não | Lista todas as categorias ordenadas por nome |
| GET | `/api/v1/categories/{slug}` | Não | Busca categoria por slug |
| POST | `/api/v1/categories` | ADMIN | Cria nova categoria (slug gerado automaticamente) |
| PUT | `/api/v1/categories/{id}` | ADMIN | Atualiza nome e/ou descrição (slug regenerado se nome mudar) |
| DELETE | `/api/v1/categories/{id}` | ADMIN | Remove categoria (rejeita se houver imóveis vinculados) |

### Properties (público)

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| GET | `/api/v1/properties` | Não | Lista imóveis ativos (`status=ACTIVE`) com filtros e paginação |
| GET | `/api/v1/properties/{slug}` | Não | Retorna detalhes completos de um imóvel ativo pelo slug (inclui categoria e array de fotos) |

**Query params — listagem pública:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `categoryId` | UUID | Filtra por categoria |
| `dealType` | `SALE` \| `RENT` | Filtra por tipo de negócio |
| `featured` | boolean | Filtra apenas imóveis em destaque |
| `city` | string | Filtra por cidade (busca parcial, case-insensitive) |
| `page` | int | Número da página — base 0 (padrão: `0`) |
| `size` | int | Itens por página (padrão: `12`) |

### Properties (admin)

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/api/v1/properties` | ADMIN | Cria novo imóvel (slug gerado automaticamente a partir do título) |
| PUT | `/api/v1/properties/{id}` | ADMIN | Atualiza imóvel (slug regenerado apenas se o título mudar; status opcional) |
| GET | `/api/v1/admin/properties` | ADMIN | Lista todos os imóveis incluindo inativos; suporta filtro por `status` |

**Query params — listagem admin (inclui todos os da listagem pública, mais):**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `status` | `ACTIVE` \| `INACTIVE` | Filtra por status (sem parâmetro = retorna todos) |

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

Selecione o environment **Local**, execute **Auth › Login** e o token é salvo automaticamente para todos os demais requests.

Fluxo recomendado:

1. `Auth › Login` — emite dois cookies (`accessToken` 15 min + `refresh_token` 7 dias) e salva o JWT na variável `accessToken` da coleção
2. `Auth › Session Validation (me)` — verifica se o token ainda é válido
3. `Auth › Register Admin User` — usa o token salvo para criar novos admins
4. `Auth › Refresh Token` — renova o access token usando o cookie `refresh_token` (path `/api/v1/auth`)
5. `Auth › Logout` — invalida o refresh token no banco e limpa os cookies
6. `Categories › List Categories` — lista categorias (público)
7. `Categories › Create Category` — cria categoria com ADMIN autenticado (slug gerado automaticamente); salva `categoryId` automaticamente
8. `Categories › Get Category by Slug` — busca categoria pelo slug (público)
9. `Categories › Update Category` — atualiza nome/descrição (slug regenerado se nome mudar)
10. `Categories › Delete Category` — remove categoria (rejeita se houver imóveis vinculados)
11. `Properties › List Properties` — lista imóveis ativos com filtros opcionais (`categoryId`, `dealType`, `featured`, `city`) e paginação; salva `propertySlug` automaticamente
12. `Properties › Get Property by Slug` — retorna detalhes completos do primeiro imóvel listado (categoria aninhada + array de fotos)
13. `Admin › Properties › List All Properties` — lista todos os imóveis incluindo inativos; suporta filtro opcional `?status=ACTIVE|INACTIVE`; requer token ADMIN
14. `Admin › Properties › Create Property` — cria novo imóvel; slug gerado automaticamente a partir do título; requer token ADMIN; salva `propertyId` automaticamente
15. `Admin › Properties › Update Property` — atualiza imóvel pelo ID; slug regenerado apenas se o título mudar; `status` opcional (mantém o atual se omitido); requer token ADMIN

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
