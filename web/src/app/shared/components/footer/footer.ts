import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SiteSettingsService } from '../../../core/services/site-settings.service';

@Component({
  selector: 'app-footer',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './footer.html',
})
export class FooterComponent {
  private readonly siteSettingsService = inject(SiteSettingsService);

  protected readonly settings = this.siteSettingsService.settings;
  protected readonly whatsappUrl = this.siteSettingsService.whatsappUrl;
  protected readonly currentYear = new Date().getFullYear();
}
