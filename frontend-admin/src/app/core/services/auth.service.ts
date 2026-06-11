import { Injectable, computed, signal } from '@angular/core';
import { HttpContext } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiResponse, AuthUser } from '@/app/core/models';
import { SKIP_401_REDIRECT, SKIP_ERROR_TOAST, skipAuthRedirect } from '@/app/core/interceptors/error.interceptor';
import { BaseService } from './base.service';

const silentContext = (): HttpContext =>
    new HttpContext().set(SKIP_401_REDIRECT, true).set(SKIP_ERROR_TOAST, true);

@Injectable({ providedIn: 'root' })
export class AuthService extends BaseService {
    readonly currentUser = signal<AuthUser | null>(null);
    readonly isAuthenticated = computed(() => this.currentUser() !== null);

    login(email: string, password: string): Observable<ApiResponse<AuthUser>> {
        return this.http.post<ApiResponse<AuthUser>>(`${this.apiUrl}/auth/login`, { email, password }, { context: skipAuthRedirect() });
    }

    logout(): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/auth/logout`, {}).pipe(tap(() => this.setCurrentUser(null)));
    }

    me(): Observable<ApiResponse<AuthUser>> {
        return this.http.get<ApiResponse<AuthUser>>(`${this.apiUrl}/auth/me`, { context: silentContext() });
    }

    refresh(): Observable<ApiResponse<AuthUser>> {
        return this.http.post<ApiResponse<AuthUser>>(`${this.apiUrl}/auth/refresh`, {}, { context: silentContext() });
    }

    setCurrentUser(user: AuthUser | null): void {
        this.currentUser.set(user);
    }
}
