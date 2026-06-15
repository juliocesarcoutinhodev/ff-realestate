import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

import { Property } from '../../../../core/models/property.model';
import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-contact-bottom-bar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CurrencyPipe],
  templateUrl: './contact-bottom-bar.component.html',
})
export class ContactBottomBarComponent {
  readonly property = input.required<Property>();
  readonly settings = input<SiteSettings | null>(null);

  readonly isRent = computed(() => this.property().dealType === 'RENT');

  readonly whatsappUrl = computed(() => {
    const s = this.settings();
    if (!s?.whatsapp) return null;
    const phone = s.whatsapp.replace(/\D/g, '');
    const text = encodeURIComponent(
      `Olá Fabrício, tenho interesse no imóvel "${this.property().title}".`,
    );
    return `https://wa.me/${phone}?text=${text}`;
  });
}
