import { Routes } from '@angular/router';
import { AppLayout } from './app/layout/component/app.layout';
import { Notfound } from './app/shared/components/notfound/notfound';
import { authGuard } from './app/core/guards/auth.guard';

export const appRoutes: Routes = [
    {
        path: '',
        component: AppLayout,
        canActivate: [authGuard],
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            {
                path: 'dashboard',
                loadComponent: () => import('./app/features/dashboard/dashboard').then((m) => m.Dashboard)
            },
            {
                path: 'properties',
                children: [
                    { path: '', loadComponent: () => import('./app/features/properties/property-list/property-list.component').then((m) => m.PropertyListComponent) },
                    { path: 'new', loadComponent: () => import('./app/features/properties/property-form/property-form.component').then((m) => m.PropertyFormComponent) },
                    { path: ':id/edit', loadComponent: () => import('./app/features/properties/property-form/property-form.component').then((m) => m.PropertyFormComponent) }
                ]
            },
            {
                path: 'categories',
                loadComponent: () => import('./app/features/categories/category-list/category-list.component').then((m) => m.CategoryListComponent)
            },
            {
                path: 'testimonials',
                loadComponent: () => import('./app/features/testimonials/testimonial-list/testimonial-list.component').then((m) => m.TestimonialListComponent)
            }
        ]
    },
    {
        path: 'auth',
        children: [
            {
                path: 'login',
                loadComponent: () => import('./app/features/auth/login').then((m) => m.Login)
            },
            {
                path: 'access',
                loadComponent: () => import('./app/features/auth/access').then((m) => m.Access)
            },
            {
                path: 'error',
                loadComponent: () => import('./app/features/auth/error').then((m) => m.Error)
            }
        ]
    },
    {
        path: '403',
        loadComponent: () => import('./app/features/auth/access-denied/access-denied.component').then((m) => m.AccessDenied)
    },
    { path: 'notfound', component: Notfound },
    { path: '**', redirectTo: '/notfound' }
];
