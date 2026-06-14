import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-contact-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './contact-section.html',
})
export class ContactSectionComponent {
  readonly settings = input<SiteSettings | null>(null);

  readonly whatsappUrl = computed(() => {
    const s = this.settings();
    if (!s?.whatsapp) return null;
    const phone = s.whatsapp.replace(/\D/g, '');
    const message = encodeURIComponent(s.whatsappMessage ?? 'Olá! Gostaria de saber mais sobre os imóveis.');
    return `https://wa.me/${phone}?text=${message}`;
  });
}
