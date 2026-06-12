import { Injectable } from '@angular/core';
import { HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, ZipCodeResponse } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class ZipCodeService extends BaseService {
    findByCode(code: string, context?: HttpContext): Observable<ApiResponse<ZipCodeResponse>> {
        return this.http.get<ApiResponse<ZipCodeResponse>>(`${this.apiUrl}/zip/${code}`, { context });
    }
}
