import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, DashboardSummary } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class DashboardService extends BaseService {
    getSummary(): Observable<ApiResponse<DashboardSummary>> {
        return this.http.get<ApiResponse<DashboardSummary>>(`${this.apiUrl}/admin/dashboard`);
    }
}
