# ff-realestate

Website e painel administrativo para **Fabrício Faceroli Corretor de Imóveis**.

## Estrutura do repositório

```
ff-realestate/
├── api/              # Spring Boot 4.x — REST API
├── web/              # Angular 21 — Site público (SSR)
├── admin/            # Angular 21 — Painel administrativo (SPA)
└── docker-compose.yml
```

## Stack

| Camada | Tecnologia |
|--------|------------|
| API | Java 25 + Spring Boot 4.x |
| Banco de dados | PostgreSQL 17 |
| Armazenamento | MinIO |
| Migrations | Flyway |
| Site público | Angular 21 + Tailwind CSS + SSR |
| Painel admin | Angular 21 + PrimeNG (Sakai) |
| Proxy reverso | Nginx |

## Pré-requisitos

- Docker e Docker Compose
- Java 25
- Node.js 22+
- Maven 3.9+

## Como rodar localmente

**1. Clone o repositório**
```bash
git clone https://github.com/seu-usuario/ff-realestate.git
cd ff-realestate
```

**2. Configure as variáveis de ambiente**
```bash
cp api/.env.example api/.env
# Edite api/.env com suas configurações locais
```

**3. Suba a infraestrutura**
```bash
docker compose -f docker-compose.dev.yml up -d
```

**4. Rode a API**
```bash
cd api
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**5. Rode o site público**
```bash
cd web
npm install
ng serve
```

**6. Rode o painel admin**
```bash
cd admin
npm install
ng serve
```

## Serviços locais

| Serviço | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Site público | http://localhost:4200 |
| Painel admin | http://localhost:4300 |
| MinIO Console | http://localhost:9001 |

## Variáveis de ambiente

Todas as configurações sensíveis são gerenciadas via variáveis de ambiente.
Consulte `api/.env.example` para a lista completa.

## Licença

Projeto privado — todos os direitos reservados.
