import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, PageResponse, Testimonial, TestimonialFilters } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class TestimonialService extends BaseService {
    findAll(filters?: TestimonialFilters): Observable<ApiResponse<PageResponse<Testimonial>>> {
        const params = filters ? this.buildParams(filters) : undefined;
        return this.http.get<ApiResponse<PageResponse<Testimonial>>>(`${this.apiUrl}/admin/testimonials`, { params });
    }

    review(id: string, status: 'APPROVED' | 'REJECTED'): Observable<ApiResponse<Testimonial>> {
        return this.http.patch<ApiResponse<Testimonial>>(`${this.apiUrl}/admin/testimonials/${id}/status`, { status });
    }

    delete(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/admin/testimonials/${id}`);
    }
}
