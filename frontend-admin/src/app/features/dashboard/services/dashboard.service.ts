import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, DashboardSummary } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class DashboardService extends BaseService {
    getSummary(): Observable<ApiResponse<DashboardSummary>> {
        return this.http.get<ApiResponse<DashboardSummary>>(`${this.apiUrl}/admin/dashboard`);
    }

    reviewTestimonial(id: string, status: 'APPROVED' | 'REJECTED'): Observable<void> {
        return this.http.patch<void>(`${this.apiUrl}/admin/testimonials/${id}/status`, { status });
    }
}
