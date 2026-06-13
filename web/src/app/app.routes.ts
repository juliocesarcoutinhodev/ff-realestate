import { Routes } from '@angular/router';

import { SiteLayoutComponent } from './shared/components/site-layout/site-layout';

export const routes: Routes = [
  {
    path: '',
    component: SiteLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home.component'),
      },
      {
        path: 'properties',
        loadComponent: () => import('./features/properties/property-list/property-list.component'),
      },
      {
        path: 'properties/:slug',
        loadComponent: () =>
          import('./features/properties/property-detail/property-detail.component'),
      },
      {
        path: '**',
        loadComponent: () => import('./features/not-found/not-found.component'),
      },
    ],
  },
];
