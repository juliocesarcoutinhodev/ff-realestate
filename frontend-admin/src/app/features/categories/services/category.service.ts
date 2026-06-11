import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, Category, CategoryForm } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class CategoryService extends BaseService {
    findAll(): Observable<ApiResponse<Category[]>> {
        return this.http.get<ApiResponse<Category[]>>(`${this.apiUrl}/categories`);
    }

    findBySlug(slug: string): Observable<ApiResponse<Category>> {
        return this.http.get<ApiResponse<Category>>(`${this.apiUrl}/categories/${slug}`);
    }

    create(data: CategoryForm): Observable<ApiResponse<Category>> {
        return this.http.post<ApiResponse<Category>>(`${this.apiUrl}/categories`, data);
    }

    update(id: string, data: CategoryForm): Observable<ApiResponse<Category>> {
        return this.http.put<ApiResponse<Category>>(`${this.apiUrl}/categories/${id}`, data);
    }

    delete(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/categories/${id}`);
    }
}
