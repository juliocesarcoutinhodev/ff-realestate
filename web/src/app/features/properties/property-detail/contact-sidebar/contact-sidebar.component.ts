import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

import { Property } from '../../../../core/models/property.model';
import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-contact-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CurrencyPipe],
  templateUrl: './contact-sidebar.component.html',
})
export class ContactSidebarComponent {
  readonly property = input.required<Property>();
  readonly settings = input<SiteSettings | null>(null);

  readonly priceLabel = computed(() =>
    this.property().dealType === 'RENT' ? 'Aluguel mensal' : 'Valor de venda',
  );

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
