# FF Corretor — Admin Panel

Painel administrativo do sistema FF Corretor, desenvolvido em Angular com o template [Sakai NG](https://github.com/primefaces/sakai-ng) (PrimeNG). Consome a API REST da aplicação backend `ff-realestate`.

---

## Stack

| Tecnologia | Versão |
|---|---|
| Angular | 21 (standalone, zoneless) |
| PrimeNG | 21 (tema Aura) |
| PrimeIcons | 7 |
| Tailwind CSS | 4 + `tailwindcss-primeui` |
| RxJS | 7 |

---

## Pré-requisitos

- Node.js 22+
- pnpm (ou npm)
- Backend `ff-realestate` rodando em `http://localhost:8080`

---

## Instalação e execução

```bash
# instalar dependências
pnpm install

# servidor de desenvolvimento
npm start          # equivale a: ng serve
```

Acesse `http://localhost:4200`. O servidor recarrega automaticamente ao salvar arquivos.

```bash
# build de produção
npm run build      # ng build --configuration=production

# testes unitários
npm test

# formatação (Prettier)
npm run format
```

Os artefatos de build são gerados em `dist/ff-realestate-admin/`.

---

## Environments

| Arquivo | Ambiente | API base URL |
|---|---|---|
| `src/environments/environment.ts` | desenvolvimento | `http://localhost:8080/api/v1` |
| `src/environments/environment.production.ts` | produção | `https://api.fabriciofaceroli.com.br/api/v1` |

O `angular.json` usa `fileReplacements` para trocar o environment automaticamente no build de produção.

---

## Autenticação

Autenticação **stateless** com JWT em cookies HttpOnly. O backend emite dois cookies: `accessToken` (15 min) e `refresh_token` (7 dias).

### Interceptors

| Interceptor | Responsabilidade |
|---|---|
| `auth.interceptor.ts` | Injeta o cookie `accessToken` em todas as requisições via `withCredentials: true` |
| `error.interceptor.ts` | Exibe toast de erro para falhas HTTP; redireciona para `/auth/login` em 401 e para `/403` em 403 |

Os tokens `SKIP_401_REDIRECT` e `SKIP_ERROR_TOAST` (`HttpContextToken`) permitem suprimir o redirect e o toast em rotas específicas (ex: login, me, refresh).

### Guard

`auth.guard.ts` protege todas as rotas autenticadas com a seguinte lógica:

```
GET /auth/me → ok → prossegue
            → falhou → POST /auth/refresh → ok → prossegue
                                          → falhou → redireciona para /auth/login
```

### AuthService

`core/services/auth.service.ts` — signals reativos:

| Signal / Método | Descrição |
|---|---|
| `currentUser` | `Signal<AuthUser \| null>` — usuário autenticado ou null |
| `isAuthenticated` | `computed()` derivado de `currentUser` |
| `login(email, password)` | POST `/auth/login` |
| `logout()` | POST `/auth/logout` — limpa `currentUser` |
| `me()` | GET `/auth/me` — valida sessão e carrega usuário |
| `refresh()` | POST `/auth/refresh` — renova access token |

---

## Rotas

| Rota | Componente | Guard |
|---|---|---|
| `/auth/login` | `Login` | — |
| `/403` | `AccessDenied` | — |
| `**` (404) | `NotFound` | — |
| `/dashboard` | `Dashboard` | `authGuard` |
| `/properties` | `Properties` | `authGuard` |
| `/categories` | `CategoryListComponent` | `authGuard` |
| `/testimonials` | `Testimonials` | `authGuard` |

---

## Features implementadas

### EPIC-01 — Autenticação

| Story | O que foi feito |
|---|---|
| STORY-00 | `AuthService` com signals (`currentUser`, `isAuthenticated`) e métodos reativos |
| STORY-01 | Página de login com Reactive Forms, validação, toast de erro, "Lembrar-me" |
| STORY-02 | `authGuard` com fallback `me() → refresh() → /auth/login` |
| STORY-03 | Botão de logout na topbar com estado de carregamento |
| STORY-04 | Avatar com iniciais + nome do usuário autenticado na topbar |
| STORY-05 | Página 403 (acesso negado) em português com redirect para `/dashboard` |

### EPIC-03 — Categorias

| Story | O que foi feito |
|---|---|
| STORY-00 | `CategoryService` com `findAll()`, `findBySlug()`, `create()`, `update()`, `delete()` |
| STORY-01 | `CategoryListComponent`: tabela com colunas Nome / Slug / Descrição / Ações; busca local por nome via `computed()`; skeleton de carregamento (`#loadingbody`); modal visualizar (read-only) com botão "Editar"; exclusão com `p-confirmDialog` |
| STORY-02 | `CategoryModalComponent`: `visible = model(false)` para two-way binding; `@Input() category?` determina modo criar/editar; slug preview em tempo real via `toSignal(valueChanges)` + `computed()`; `effect()` popula o form ao abrir; form com `maxLength(100)` no nome e `maxLength(500)` na descrição; emite `(saved)` para o pai recarregar a lista |
| STORY-03 | Confirmação de exclusão via `p-confirmDialog`: mensagem "Deseja excluir a categoria '...'? Esta ação não pode ser desfeita."; remove o item do signal local sem recarregar; toast de sucesso via `MessageService` root; erros (incluindo 409 Conflict) tratados pelo `errorInterceptor` |

---

### EPIC-02 — Dashboard

| Story | O que foi feito |
|---|---|
| STORY-01 | `DashboardService` com `getSummary()` e `reviewTestimonial()`; interfaces `DashboardSummary`, `RecentProperty`, `PendingTestimonial` |
| STORY-02 | 4 stat cards (Imóveis Ativos, Inativos, Categorias, Pendentes) com `p-skeleton` durante carregamento e destaque amarelo no card de pendentes quando `> 0` |
| STORY-03 | Tabela de imóveis recentes: colunas Título, Tipo, Preço (BRL), Status (`p-tag`), Cadastrado em, Ações ("Ver") |
| STORY-04 | Tabela de depoimentos pendentes: colunas Cliente, Avaliação (`p-rating` readonly), Trecho (80 chars), Data, Ações (Aprovar / Rejeitar via PATCH); remove o item do signal após ação |

---

## Estrutura de pastas

```text
src/
├── environments/
│   ├── environment.ts                    → dev (localhost:8080)
│   └── environment.production.ts        → prod
└── app/
    ├── layout/
    │   └── component/
    │       ├── app.topbar.ts             → logout + avatar + nome do usuário
    │       ├── app.sidebar.ts
    │       ├── app.menu.ts
    │       └── app.footer.ts
    ├── core/
    │   ├── interceptors/
    │   │   ├── auth.interceptor.ts       → withCredentials em todas as requisições
    │   │   └── error.interceptor.ts      → toast de erro + redirect 401/403
    │   ├── guards/
    │   │   └── auth.guard.ts             → me() → refresh() → /auth/login
    │   ├── services/
    │   │   ├── base.service.ts           → HttpClient + apiUrl base
    │   │   └── auth.service.ts           → currentUser signal, login/logout/me/refresh
    │   └── models/
    │       ├── api-response.model.ts     → ApiResponse<T>, PageResponse<T>
    │       ├── auth.model.ts             → AuthUser
    │       ├── category.model.ts         → Category
    │       ├── dashboard.model.ts        → DashboardSummary, RecentProperty, PendingTestimonial
    │       ├── photo.model.ts            → Photo
    │       ├── property.model.ts         → Property
    │       └── testimonial.model.ts      → Testimonial
    ├── shared/
    │   └── components/
    │       └── notfound/                 → página 404 em português
    └── features/
        ├── auth/
        │   ├── login.ts                  → Reactive Forms, toast, "Lembrar-me"
        │   ├── access-denied/            → página 403 em português
        │   └── error.ts
        ├── dashboard/
        │   ├── services/
        │   │   └── dashboard.service.ts  → getSummary(), reviewTestimonial()
        │   ├── components/
        │   │   ├── statswidget.ts        → 4 cards com skeleton + destaque amarelo
        │   │   ├── recentsaleswidget.ts  → tabela de imóveis recentes
        │   │   └── notificationswidget.ts → tabela de depoimentos pendentes + aprovar/rejeitar
        │   └── dashboard.ts             → orquestra os widgets e gerencia o signal summary
        ├── properties/                   → gestão de imóveis (em desenvolvimento)
        ├── categories/
        │   ├── services/
        │   │   └── category.service.ts        → findAll(), create(), update(), delete()
        │   ├── category-list/
        │   │   └── category-list.component.ts → tabela + busca local + modal visualizar + exclusão com confirm
        │   └── category-modal/
        │       └── category-modal.component.ts → modal criar/editar + slug preview em tempo real
        ├── photos/                       → gestão de fotos (em desenvolvimento)
        └── testimonials/                 → gestão de depoimentos (em desenvolvimento)
```

---

## Recursos adicionais

- [Angular CLI](https://angular.dev/tools/cli)
- [PrimeNG](https://primeng.org)
- [Sakai NG template](https://github.com/primefaces/sakai-ng)
