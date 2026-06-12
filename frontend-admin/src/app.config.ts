import { registerLocaleData } from '@angular/common';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import localePtBr from '@angular/common/locales/pt';
import { authInterceptor } from '@/app/core/interceptors/auth.interceptor';
import { errorInterceptor } from '@/app/core/interceptors/error.interceptor';
import { ApplicationConfig, LOCALE_ID, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding, withEnabledBlockingInitialNavigation, withInMemoryScrolling, withRouterConfig } from '@angular/router';
import Aura from '@primeuix/themes/aura';
import { providePrimeNG } from 'primeng/config';
import { MessageService } from 'primeng/api';
import { appRoutes } from './app.routes';

registerLocaleData(localePtBr);

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(
            appRoutes,
            withInMemoryScrolling({ anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' }),
            withEnabledBlockingInitialNavigation(),
            withComponentInputBinding(),
            withRouterConfig({ paramsInheritanceStrategy: 'always' })
        ),
        provideHttpClient(withFetch(), withInterceptors([authInterceptor, errorInterceptor])),
        MessageService,
        { provide: LOCALE_ID, useValue: 'pt-BR' },
        provideZonelessChangeDetection(),
        providePrimeNG({ theme: { preset: Aura, options: { darkModeSelector: '.app-dark' } } })
    ]
};
