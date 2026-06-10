# FF Corretor — Admin Panel

Painel administrativo do sistema FF Corretor, desenvolvido em Angular 21 com o template [Sakai NG](https://github.com/primefaces/sakai-ng) (PrimeNG). Consome a API REST da aplicação backend `ff-realestate`.

## Stack

- **Angular 21** (standalone components)
- **PrimeNG 21** — biblioteca de componentes UI
- **PrimeIcons 7**
- **Tailwind CSS 4** + `tailwindcss-primeui`
- **Chart.js 4** — gráficos do dashboard
- **RxJS 7**

## Pré-requisitos

- Node.js 22+
- npm ou pnpm
- Backend `ff-realestate` rodando em `http://localhost:8080`

## Instalação

```bash
# instalar dependências
npm install
# ou
pnpm install
```

## Desenvolvimento

```bash
npm start
# equivale a: ng serve
```

Acesse `http://localhost:4200`. O servidor recarrega automaticamente ao salvar arquivos.

## Build de produção

```bash
npm run build
# equivale a: ng build --configuration=production
# troca automaticamente environment.ts → environment.production.ts
```

Os artefatos são gerados em `dist/ff-realestate-admin/`.

## Testes unitários

```bash
npm test
# equivale a: ng test (Karma + Jasmine)
```

## Formatação de código

```bash
npm run format
# Prettier — formata todos os arquivos .ts, .html, .js
```

## Environments

| Arquivo | Ambiente | API base URL |
|---|---|---|
| `src/environments/environment.ts` | desenvolvimento | `http://localhost:8080/api/v1` |
| `src/environments/environment.production.ts` | produção | `https://api.fabriciofaceroli.com.br/api/v1` |

O `angular.json` está configurado com `fileReplacements` para trocar o environment automaticamente no build de produção.

## Estrutura de pastas

```text
src/
├── environments/
│   ├── environment.ts               → dev
│   └── environment.production.ts   → prod
└── app/
    ├── layout/                      → topbar, sidebar, menu, footer (Sakai NG)
    ├── core/
    │   ├── interceptors/            → HTTP interceptors (auth, error)
    │   ├── guards/                  → route guards
    │   ├── services/                → serviços globais (ex: product.service — exemplo Sakai)
    │   └── models/                  → interfaces e tipos globais
    ├── shared/
    │   ├── components/              → componentes reutilizáveis (ex: notfound)
    │   └── pipes/                   → pipes customizados
    └── features/
        ├── auth/                    → login, acesso negado, erro
        ├── dashboard/               → painel principal com widgets de exemplo (Sakai NG)
        ├── properties/              → gestão de imóveis
        ├── categories/              → gestão de categorias
        ├── photos/                  → gestão de fotos
        └── testimonials/            → gestão de depoimentos
```

## Recursos adicionais

- [Angular CLI](https://angular.dev/tools/cli)
- [PrimeNG](https://primeng.org)
- [Sakai NG template](https://github.com/primefaces/sakai-ng)
