import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const messageService = inject(MessageService);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            switch (error.status) {
                case 401:
                    router.navigate(['/auth/login']);
                    break;
                case 403:
                    router.navigate(['/403']);
                    break;
                case 404:
                    messageService.add({
                        severity: 'error',
                        summary: 'Erro',
                        detail: 'Recurso não encontrado.'
                    });
                    break;
                case 409: {
                    const body = error.error as { message?: string };

                    messageService.add({
                        severity: 'error',
                        summary: 'Conflito',
                        detail: body?.message ?? 'Conflito ao processar a requisição.'
                    });
                    break;
                }
                case 500:
                    messageService.add({
                        severity: 'error',
                        summary: 'Erro',
                        detail: 'Erro interno. Tente novamente mais tarde.'
                    });
                    break;
            }

            return throwError(() => error);
        })
    );
};
