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

| Arquivo | Ambiente | `apiUrl` | `siteUrl` |
|---|---|---|---|
| `environment.ts` | desenvolvimento | `http://localhost:8080/api/v1` | `http://localhost:4200` |
| `environment.production.ts` | produção | `https://api.fabriciofaceroli.com.br/api/v1` | `https://fabriciofaceroli.com.br` |

O `angular.json` usa `fileReplacements` para trocar o environment automaticamente no build de produção. O `siteUrl` é usado para gerar canonical URLs corretas em SSR.

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
| `/properties/:slug` | `RenderMode.Server` | Conteúdo dinâmico por slug |
| `**` (demais) | `RenderMode.Prerender` | Geração estática em build |

---

## Rotas

| Rota | Componente | Lazy? |
|---|---|---|
| `/` | `HomeComponent` | ✅ |
| `/properties` | `PropertyListComponent` | ✅ |
| `/properties/:slug` | `PropertyDetailComponent` | ✅ |
| `**` | `NotFoundComponent` | ✅ |

Todas as rotas são filhas de `SiteLayoutComponent` (eager), que renderiza header, footer e WhatsApp FAB.

---

## Features implementadas

### Fundação (STORY-00 a STORY-04)

| Story | O que foi entregue |
|---|---|
| STORY-00 | Estrutura Angular 21 SSR, design system `@theme` com paleta luxo, fontes Cormorant Garamond + DM Sans, `postcss.config.json` para Tailwind v4 |
| STORY-01 | Models TypeScript: `Property`, `PropertyPhoto`, `Category`, `Testimonial`, `SiteSettings`, `ApiResponse<T>`, `PageResponse<T>` |
| STORY-02 | `PropertyService`, `CategoryService`, `TestimonialService`, `SiteSettingsService` com signals reativos |
| STORY-03 | `HeaderComponent` (desktop + menu mobile), `FooterComponent`, `WhatsappFabComponent`, `SiteLayoutComponent` |
| STORY-04 | Routing com lazy loading, SSR render modes, `withComponentInputBinding()`, `withInMemoryScrolling()`, `withViewTransitions()` |

### EPIC-01 · Home Page (STORY-00 a STORY-06)

| Story | O que foi entregue |
|---|---|
| STORY-00 | `HomeComponent` com SSR: `forkJoin` de settings + imóveis em destaque + depoimentos, meta tags dinâmicas (title, description, og:image) |
| STORY-01 | `HeroSectionComponent` — hero fullscreen com imagem de fundo, headline, subtítulo e CTA em dourado |
| STORY-02 | `FeaturedPropertiesComponent` — grid 3 colunas com skeleton `animate-pulse`, link "Ver todos os imóveis" |
| STORY-03 | `PropertyCardComponent` (shared) — card com imagem `aspect-[4/3]`, badges de tipo e categoria, specs, preço e link para detalhe |
| STORY-04 | `TestimonialsSectionComponent` — carrossel de depoimentos aprovados |
| STORY-05 | `AboutSectionComponent` — seção sobre o corretor com foto e texto das configurações do site |
| STORY-06 | `ContactSectionComponent` — dados de contato e link WhatsApp |

### EPIC-02 · Property Listing (STORY-00 a STORY-04)

| Story | O que foi entregue |
|---|---|
| STORY-00 | `PropertyListComponent` com 7 signals (`properties`, `totalElements`, `totalPages`, `currentPage`, `loading`, `loadingMore`, `selectedDealType`, `selectedCategorySlug`, `selectedCity`), filtros sincronizados com query params (`?type`, `?category`, `?city`), `shareReplay(1)` para cache de categorias |
| STORY-01 | `FilterBarComponent` — desktop: barra `border-y` com chips de tipo, dropdowns de categoria e cidade, contador; mobile: botão "Filtrar" + bottom sheet com filtros pendentes, "Aplicar filtros" e overlay com ESC via `host` |
| STORY-02 | Grid responsivo (`grid-cols-1/2/3 gap-8`) usando `PropertyCardComponent`, skeleton `aspect-[4/3] bg-surface rounded-sm animate-pulse`, estado vazio com "Limpar filtros" |
| STORY-03 | Botão "Carregar mais imóveis" com `loadingMore` spinner, estilo `text-xs uppercase tracking-widest`, contador "Exibindo X de Y imóveis", acumulação via `properties.update()` |
| STORY-04 | Header com `pt-40 pb-16`, linha dourada + label "Portfólio", canonical URL via `DOCUMENT`, JSON-LD `ItemList` injetado no `<head>` com cleanup no destroy, `siteUrl` adicionado ao environment |

---

## Estrutura de pastas

```text
src/
├── environments/
│   ├── environment.ts                        → dev (apiUrl + siteUrl)
│   └── environment.production.ts            → prod (apiUrl + siteUrl)
├── styles.css                               → design system @theme + Tailwind + view transitions CSS
└── app/
    ├── app.ts                               → AppComponent (apenas <router-outlet>)
    ├── app.config.ts                        → provideRouter (withViewTransitions, withInMemoryScrolling), provideHttpClient, provideClientHydration
    ├── app.routes.ts                        → rotas com lazy loading
    ├── app.routes.server.ts                 → SSR render modes por rota
    ├── core/
    │   ├── models/
    │   │   ├── api-response.model.ts        → ApiResponse<T>, PageResponse<T>
    │   │   ├── property.model.ts            → Property, PropertyPhoto, PropertyFilterSelection
    │   │   ├── category.model.ts            → Category
    │   │   ├── testimonial.model.ts         → Testimonial
    │   │   ├── site-settings.model.ts       → SiteSettings
    │   │   └── index.ts                     → barrel export
    │   └── services/
    │       ├── property.service.ts          → findAll(filters?), findBySlug()
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
    │       │   ├── footer.ts
    │       │   └── footer.html              → 3 colunas: identidade, contato, navegação
    │       ├── property-card/
    │       │   ├── property-card.ts         → computed: location, dealTypeLabel, isRent
    │       │   └── property-card.html       → imagem, badges, specs, preço, CTA
    │       └── whatsapp-fab/
    │           └── whatsapp-fab.ts          → FAB fixo, visível apenas quando whatsappUrl()
    └── features/
        ├── home/
        │   ├── home.component.ts            → forkJoin SSR, meta tags dinâmicas
        │   └── sections/
        │       ├── hero-section/            → hero fullscreen com settings
        │       ├── featured-properties/     → grid 3 colunas + skeleton
        │       ├── testimonials-section/    → carrossel de depoimentos
        │       ├── about-section/           → seção sobre com foto
        │       └── contact-section/         → contato e WhatsApp
        ├── properties/
        │   ├── filter-bar/
        │   │   ├── filter-bar.component.ts  → input/output signals, desktop + mobile bottom sheet
        │   │   └── filter-bar.component.html
        │   ├── property-list/
        │   │   ├── property-list.component.ts  → 7 signals, query param sync, canonical, JSON-LD
        │   │   └── property-list.component.html
        │   └── property-detail/
        │       └── property-detail.component.ts → (a implementar — EPIC-03)
        └── not-found/
            └── not-found.component.ts       → 404 com robots: noindex
```

---

## Observações técnicas

### Tailwind CSS v4 com Angular

O Angular (`@angular/build`) reconhece **apenas** `postcss.config.json` e `.postcssrc.json` para configuração do PostCSS — arquivos `.mjs` ou `.js` são silenciosamente ignorados. Por isso o arquivo de configuração é `postcss.config.json` (não `.mjs`).

### Signals

Toda a gestão de estado usa a API de signals do Angular 21: `signal()`, `computed()`, `input()`, `output()`, `toSignal()`. Não há `NgRx` nem `BehaviorSubject`.

### Lazy loading

Todos os componentes de página usam `export default class` e são carregados via `loadComponent: () => import(...)` para code splitting automático.

### View Transitions

`withViewTransitions()` ativo no router com keyframes customizados em `styles.css` (`::view-transition-old/new(root)`). Produz um fade com leve deslocamento vertical entre rotas. Graceful fallback em browsers sem suporte (Firefox, Safari < 18).

### SEO

- Meta tags (`title`, `description`, `og:*`) via `Title` e `Meta` services do Angular
- Canonical URL via `DOCUMENT` injection (SSR-safe), usando `environment.siteUrl`
- JSON-LD Schema.org via `DOCUMENT` com `<script type="application/ld+json">` e cleanup em `destroyRef.onDestroy()`

---

## Recursos adicionais

- [Angular CLI](https://angular.dev/tools/cli)
- [Angular SSR](https://angular.dev/guide/ssr)
- [Tailwind CSS v4](https://tailwindcss.com/docs)
