import { Injectable } from '@angular/core';
import { HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, PhotoOrder, PropertyPhoto } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class PhotoService extends BaseService {
    findAll(propertyId: string): Observable<ApiResponse<PropertyPhoto[]>> {
        return this.http.get<ApiResponse<PropertyPhoto[]>>(`${this.apiUrl}/properties/${propertyId}/photos`);
    }

    upload(propertyId: string, files: File[]): Observable<HttpEvent<ApiResponse<PropertyPhoto[]>>> {
        const formData = new FormData();
        files.forEach((file) => formData.append('files', file));
        const req = new HttpRequest('POST', `${this.apiUrl}/properties/${propertyId}/photos`, formData, {
            reportProgress: true
        });
        return this.http.request<ApiResponse<PropertyPhoto[]>>(req);
    }

    setCover(propertyId: string, photoId: string): Observable<ApiResponse<PropertyPhoto[]>> {
        return this.http.patch<ApiResponse<PropertyPhoto[]>>(`${this.apiUrl}/properties/${propertyId}/photos/${photoId}/cover`, null);
    }

    reorder(propertyId: string, order: PhotoOrder[]): Observable<ApiResponse<PropertyPhoto[]>> {
        return this.http.patch<ApiResponse<PropertyPhoto[]>>(`${this.apiUrl}/properties/${propertyId}/photos/order`, order);
    }

    delete(propertyId: string, photoId: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/properties/${propertyId}/photos/${photoId}`);
    }
}
