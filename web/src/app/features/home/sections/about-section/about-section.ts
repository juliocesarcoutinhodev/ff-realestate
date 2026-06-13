import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-about-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<section aria-label="Sobre o corretor"></section>`,
})
export class AboutSectionComponent {
  readonly settings = input<SiteSettings | null>(null);
}
