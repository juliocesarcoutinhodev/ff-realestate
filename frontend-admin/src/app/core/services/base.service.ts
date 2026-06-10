import { inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@/environments/environment';

export abstract class BaseService {
    protected readonly http = inject(HttpClient);
    protected readonly apiUrl = environment.apiUrl;

    protected buildParams(filters: object): HttpParams {
        return Object.entries(filters).reduce((params, [key, value]) => {
            if (value !== null && value !== undefined) {
                return params.set(key, String(value));
            }

            return params;
        }, new HttpParams());
    }
}
