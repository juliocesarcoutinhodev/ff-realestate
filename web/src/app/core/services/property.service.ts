import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse, Property } from '../models';

export interface PropertyFilters {
  page?: number;
  size?: number;
  dealType?: 'SALE' | 'RENT';
  categorySlug?: string;
  city?: string;
  featured?: boolean;
}

@Injectable({ providedIn: 'root' })
export class PropertyService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/properties`;

  findAll(filters?: PropertyFilters): Observable<ApiResponse<PageResponse<Property>>> {
    let params = new HttpParams();
    if (filters?.page !== undefined) params = params.set('page', filters.page);
    if (filters?.size !== undefined) params = params.set('size', filters.size);
    if (filters?.dealType) params = params.set('dealType', filters.dealType);
    if (filters?.categorySlug) params = params.set('categorySlug', filters.categorySlug);
    if (filters?.city) params = params.set('city', filters.city);
    if (filters?.featured !== undefined) params = params.set('featured', filters.featured);
    return this.http.get<ApiResponse<PageResponse<Property>>>(this.apiUrl, { params });
  }

  findBySlug(slug: string): Observable<ApiResponse<Property>> {
    return this.http.get<ApiResponse<Property>>(`${this.apiUrl}/${slug}`);
  }
}
