# FF Real Estate — Admin Panel

Painel administrativo do sistema FF Real Estate, desenvolvido em Angular 21 com o template [Sakai NG](https://github.com/primefaces/sakai-ng) (PrimeNG). Consome a API REST da aplicação backend `ff-realestate`.

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

## Estrutura de pastas relevante

```text
src/
├── app/
│   ├── layout/          → componentes de layout (topbar, sidebar, menu, footer)
│   ├── pages/
│   │   ├── auth/        → login, acesso negado, erro
│   │   ├── dashboard/   → widgets do painel principal
│   │   └── ...          → demais módulos administrativos
│   ├── app.routes.ts
│   └── app.config.ts
└── assets/
    └── layout/          → estilos SCSS do template Sakai
```

## Recursos adicionais

- [Angular CLI](https://angular.dev/tools/cli)
- [PrimeNG](https://primeng.org)
- [Sakai NG template](https://github.com/primefaces/sakai-ng)
