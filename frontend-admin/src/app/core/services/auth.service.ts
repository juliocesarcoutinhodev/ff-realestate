import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { ApiResponse } from '@/app/core/models/api-response.model';
import { UserProfile } from '@/app/core/models/user-profile.model';
import { BaseService } from './base.service';

@Injectable({ providedIn: 'root' })
export class AuthService extends BaseService {
    me(): Observable<boolean> {
        return this.http.get<ApiResponse<UserProfile>>(`${this.apiUrl}/auth/me`).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }
}
