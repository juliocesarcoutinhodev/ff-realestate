import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse, SiteSettings } from '../models';

@Injectable({ providedIn: 'root' })
export class SiteSettingsService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/site-settings`;

  private readonly _settings = signal<SiteSettings | null>(null);
  readonly settings = this._settings.asReadonly();

  readonly whatsappUrl = computed(() => {
    const s = this._settings();
    if (!s?.whatsapp) return null;
    const phone = s.whatsapp.replace(/\D/g, '');
    const message = encodeURIComponent(s.whatsappMessage ?? 'Olá! Gostaria de saber mais sobre os imóveis.');
    return `https://wa.me/${phone}?text=${message}`;
  });

  get(): Observable<ApiResponse<SiteSettings>> {
    return this.http
      .get<ApiResponse<SiteSettings>>(this.apiUrl)
      .pipe(tap(response => this._settings.set(response.data)));
  }
}
