import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

import { SiteSettingsService } from '../../../core/services/site-settings.service';

@Component({
  selector: 'app-header',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
})
export class HeaderComponent {
  private readonly siteSettingsService = inject(SiteSettingsService);

  protected readonly settings = this.siteSettingsService.settings;
  protected readonly whatsappUrl = this.siteSettingsService.whatsappUrl;
  protected readonly mobileMenuOpen = signal(false);

  protected toggleMobileMenu(): void {
    this.mobileMenuOpen.update(open => !open);
  }
}
