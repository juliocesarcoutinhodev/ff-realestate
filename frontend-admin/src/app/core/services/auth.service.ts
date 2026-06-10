import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { ApiResponse, AuthUser } from '@/app/core/models';
import { skipAuthRedirect } from '@/app/core/interceptors/error.interceptor';
import { BaseService } from './base.service';

@Injectable({ providedIn: 'root' })
export class AuthService extends BaseService {
    me(): Observable<boolean> {
        return this.http.get<ApiResponse<AuthUser>>(`${this.apiUrl}/auth/me`, { context: skipAuthRedirect() }).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }

    login(email: string, password: string): Observable<ApiResponse<AuthUser>> {
        return this.http.post<ApiResponse<AuthUser>>(`${this.apiUrl}/auth/login`, { email, password });
    }
}
