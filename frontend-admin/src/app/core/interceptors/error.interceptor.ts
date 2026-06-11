import { HttpContext, HttpContextToken, HttpErrorResponse, HttpEventType, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { catchError, tap, throwError } from 'rxjs';
import { ApiResponse } from '@/app/core/models/api-response.model';
import { ErrorResponse } from '@/app/core/models/error-response.model';

export const SKIP_401_REDIRECT = new HttpContextToken<boolean>(() => false);
export const SKIP_ERROR_TOAST = new HttpContextToken<boolean>(() => false);

export const skipAuthRedirect = (): HttpContext => new HttpContext().set(SKIP_401_REDIRECT, true);

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const messageService = inject(MessageService);

    return next(req).pipe(
        tap((event) => {
            if (event.type === HttpEventType.Response && event instanceof HttpResponse && req.method !== 'GET') {
                const body = event.body as ApiResponse<unknown> | null;

                if (body?.message) {
                    messageService.add({ severity: 'success', summary: 'Sucesso', detail: body.message });
                }
            }
        }),
        catchError((error) => {
            if (error instanceof HttpErrorResponse) {
                const body = error.error as ErrorResponse | null;
                const detail = body?.message ?? 'Erro inesperado. Tente novamente.';

                if (error.status === 401 && !req.context.get(SKIP_401_REDIRECT)) {
                    router.navigate(['/auth/login']);
                } else if (error.status === 403) {
                    router.navigate(['/403']);
                } else if (!req.context.get(SKIP_ERROR_TOAST)) {
                    messageService.add({ severity: 'error', summary: 'Erro', detail });
                }
            }

            return throwError(() => error);
        })
    );
};
