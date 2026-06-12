import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseService } from '@/app/core/services/base.service';
import { ApiResponse, SiteSettings, SiteSettingsForm } from '@/app/core/models';

@Injectable({ providedIn: 'root' })
export class SiteSettingsService extends BaseService {
    get(): Observable<ApiResponse<SiteSettings>> {
        return this.http.get<ApiResponse<SiteSettings>>(`${this.apiUrl}/site-settings`);
    }

    update(data: SiteSettingsForm): Observable<ApiResponse<SiteSettings>> {
        return this.http.put<ApiResponse<SiteSettings>>(`${this.apiUrl}/site-settings`, data);
    }

    uploadBrokerPhoto(file: File): Observable<ApiResponse<{ url: string }>> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<ApiResponse<{ url: string }>>(`${this.apiUrl}/site-settings/broker-photo`, formData);
    }

    uploadHeroImage(file: File): Observable<ApiResponse<{ url: string }>> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<ApiResponse<{ url: string }>>(`${this.apiUrl}/site-settings/hero-image`, formData);
    }
}
