import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, PageResponse, Property, PropertyDetail, PropertyFilters, PropertyForm } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class PropertyService extends BaseService {
    findAll(filters?: PropertyFilters): Observable<ApiResponse<PageResponse<Property>>> {
        const params = filters ? this.buildParams(filters) : undefined;
        return this.http.get<ApiResponse<PageResponse<Property>>>(`${this.apiUrl}/admin/properties`, { params });
    }

    findById(id: string): Observable<ApiResponse<PropertyDetail>> {
        return this.http.get<ApiResponse<PropertyDetail>>(`${this.apiUrl}/admin/properties/${id}`);
    }

    create(data: PropertyForm): Observable<ApiResponse<Property>> {
        return this.http.post<ApiResponse<Property>>(`${this.apiUrl}/properties`, data);
    }

    update(id: string, data: PropertyForm): Observable<ApiResponse<Property>> {
        return this.http.put<ApiResponse<Property>>(`${this.apiUrl}/properties/${id}`, data);
    }

    toggleStatus(id: string, status: 'ACTIVE' | 'INACTIVE'): Observable<ApiResponse<Property>> {
        return this.http.patch<ApiResponse<Property>>(`${this.apiUrl}/properties/${id}/status`, { status });
    }

    delete(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/properties/${id}`);
    }
}
