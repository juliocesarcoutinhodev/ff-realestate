import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-about-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './about-section.html',
})
export class AboutSectionComponent {
  readonly settings = input<SiteSettings | null>(null);

  protected readonly stats = [
    { value: '15+', label: 'Anos de mercado' },
    { value: '240+', label: 'Imóveis vendidos' },
    { value: '100%', label: 'Atendimento exclusivo' },
  ] as const;
}
