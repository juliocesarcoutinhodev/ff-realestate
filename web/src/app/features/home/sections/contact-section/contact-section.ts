import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-contact-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<section aria-label="Contato"></section>`,
})
export class ContactSectionComponent {
  readonly settings = input<SiteSettings | null>(null);
}
