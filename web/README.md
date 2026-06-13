# FF Corretor — Site Público

Site público do corretor Fabrício Faceroli, desenvolvido em Angular 21 com SSR (Server-Side Rendering). Consome a API REST da aplicação backend `ff-realestate`.

---

## Stack

| Tecnologia | Versão |
|---|---|
| Angular | 21.2 (standalone, SSR) |
| Angular SSR | 21.2 (`@angular/ssr`) |
| Tailwind CSS | 4.3 (`@tailwindcss/postcss`) |
| TypeScript | 5.9 |
| RxJS | 7.8 |

---

## Pré-requisitos

- Node.js 22+
- npm
- Backend `ff-realestate` rodando em `http://localhost:8080`

---

## Instalação e execução

```bash
# instalar dependências
npm install

# servidor de desenvolvimento (Vite + SSR)
npm start          # equivale a: ng serve
```

Acesse `http://localhost:4200`. O servidor recarrega automaticamente ao salvar arquivos.

```bash
# build de produção
npm run build      # ng build

# servir o build SSR localmente
npm run serve:ssr:web   # node dist/web/server/server.mjs

# testes unitários
npm test
```

Os artefatos de build são gerados em `dist/web/`.

---

## Environments

| Arquivo | Ambiente | API base URL |
|---|---|---|
| `src/environments/environment.ts` | desenvolvimento | `http://localhost:8080/api/v1` |
| `src/environments/environment.production.ts` | produção | `https://api.fabriciofaceroli.com.br/api/v1` |

O `angular.json` usa `fileReplacements` para trocar o environment automaticamente no build de produção.

---

## Design System

O design system é definido inteiramente em `src/styles.css` usando o bloco `@theme` do Tailwind v4.

### Paleta de cores

| Token | Valor | Uso |
|---|---|---|
| `--color-background` | `#0F1F1B` | Fundo da página |
| `--color-surface` | `#1E2E29` | Cards, footer |
| `--color-primary` | `#1A3C34` | Menu mobile, elementos de destaque |
| `--color-gold` | `#C9A84C` | Acentos, CTAs, links ativos |
| `--color-gold-foreground` | `#0F1F1B` | Texto sobre fundo dourado |
| `--color-foreground` | `#F0EDE6` | Texto principal |
| `--color-muted` | `#8A9E99` | Texto secundário |
| `--color-border` | `#263D37` | Bordas |

### Tipografia

| Token | Valor |
|---|---|
| `--font-serif` | Cormorant Garamond, Georgia, serif |
| `--font-sans` | DM Sans, system-ui, sans-serif |

As fontes são carregadas via Google Fonts no `src/index.html`.

---

## SSR e Render Modes

Configurado em `src/app/app.routes.server.ts`:

| Rota | Modo | Motivo |
|---|---|---|
| `imoveis/:slug` | `RenderMode.Server` | Conteúdo dinâmico por slug |
| `**` (demais) | `RenderMode.Prerender` | Geração estática em build |

---

## Rotas

| Rota | Componente | Lazy? |
|---|---|---|
| `/` | `HomeComponent` | ✅ |
| `/imoveis` | `PropertyListComponent` | ✅ |
| `/imoveis/:slug` | `PropertyDetailComponent` | ✅ |
| `**` | `NotFoundComponent` | ✅ |

Todas as rotas são filhas de `SiteLayoutComponent` (eager), que renderiza header, footer e WhatsApp FAB.

---

## Features implementadas

### Fundação (STORY-00 a STORY-04)

| Story | O que foi entregue |
|---|---|
| STORY-00 | Estrutura Angular 21 SSR, design system `@theme` com paleta luxo, fontes Cormorant Garamond + DM Sans, `postcss.config.json` para Tailwind v4 |
| STORY-01 | Models TypeScript: `Property`, `PropertyPhoto`, `Category`, `Testimonial`, `SiteSettings`, `ApiResponse<T>`, `PageResponse<T>` |
| STORY-02 | `PropertyService`, `CategoryService`, `TestimonialService`, `SiteSettingsService` com signals reativos (`signal()`, `computed()`) |
| STORY-03 | `HeaderComponent` (desktop + menu mobile com toggle), `FooterComponent` (3 colunas), `WhatsappFabComponent` (FAB fixo), `SiteLayoutComponent` |
| STORY-04 | Routing com lazy loading, SSR render modes, `withComponentInputBinding()`, `withInMemoryScrolling()` |

---

## Estrutura de pastas

```text
src/
├── environments/
│   ├── environment.ts                        → dev (localhost:8080)
│   └── environment.production.ts            → prod
├── styles.css                               → design system @theme + Tailwind import
└── app/
    ├── app.ts                               → AppComponent (apenas <router-outlet>)
    ├── app.config.ts                        → provideRouter, provideHttpClient, provideClientHydration
    ├── app.routes.ts                        → rotas com lazy loading
    ├── app.routes.server.ts                 → SSR render modes por rota
    ├── core/
    │   ├── models/
    │   │   ├── api-response.model.ts        → ApiResponse<T>, PageResponse<T>
    │   │   ├── property.model.ts            → Property, PropertyPhoto
    │   │   ├── category.model.ts            → Category
    │   │   ├── testimonial.model.ts         → Testimonial
    │   │   ├── site-settings.model.ts       → SiteSettings
    │   │   └── index.ts                     → barrel export
    │   └── services/
    │       ├── property.service.ts          → findAll(), findBySlug(), getFeatured()
    │       ├── category.service.ts          → findAll()
    │       ├── testimonial.service.ts       → findAll()
    │       └── site-settings.service.ts     → get(), signals: settings, whatsappUrl
    ├── shared/
    │   └── components/
    │       ├── site-layout/
    │       │   └── site-layout.ts           → shell: header + <router-outlet> + footer + fab
    │       ├── header/
    │       │   ├── header.ts                → desktop nav + menu mobile com toggle signal
    │       │   └── header.html
    │       ├── footer/
    │       │   ├── footer.ts                → currentYear, signals de settings
    │       │   └── footer.html              → 3 colunas: identidade, contato, navegação
    │       └── whatsapp-fab/
    │           └── whatsapp-fab.ts          → FAB fixo, visível apenas quando whatsappUrl()
    └── features/
        ├── home/
        │   └── home.component.ts            → meta tags SEO (a implementar: hero, destaques)
        ├── properties/
        │   ├── property-list/
        │   │   └── property-list.component.ts → (a implementar: listagem + filtros)
        │   └── property-detail/
        │       └── property-detail.component.ts → input slug via withComponentInputBinding
        └── not-found/
            └── not-found.component.ts       → 404 com robots: noindex
```

---

## Observações técnicas

### Tailwind CSS v4 com Angular

O Angular (`@angular/build`) reconhece **apenas** `postcss.config.json` e `.postcssrc.json` para configuração do PostCSS — arquivos `.mjs` ou `.js` são silenciosamente ignorados. Por isso o arquivo de configuração é `postcss.config.json` (não `.mjs`).

### Signals

Toda a gestão de estado usa a API de signals do Angular 21: `signal()`, `computed()`, `input()`, `effect()`. Não há `NgRx` nem `BehaviorSubject`.

### Lazy loading

Todos os componentes de página usam `export default class` e são carregados via `loadComponent: () => import(...)` para code splitting automático.

---

## Recursos adicionais

- [Angular CLI](https://angular.dev/tools/cli)
- [Angular SSR](https://angular.dev/guide/ssr)
- [Tailwind CSS v4](https://tailwindcss.com/docs)
