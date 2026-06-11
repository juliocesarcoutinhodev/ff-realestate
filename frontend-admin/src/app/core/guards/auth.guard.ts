import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '@/app/core/services/auth.service';

export const authGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.me().pipe(
        map((response) => {
            authService.setCurrentUser(response.data);
            return true;
        }),
        catchError(() =>
            authService.refresh().pipe(
                map((response) => {
                    authService.setCurrentUser(response.data);
                    return true;
                }),
                catchError(() => {
                    authService.setCurrentUser(null);
                    return of(router.createUrlTree(['/auth/login']));
                })
            )
        )
    );
};
