import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SiteSettings } from '../../../../core/models/site-settings.model';

@Component({
  selector: 'app-hero-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './hero-section.html',
})
export class HeroSectionComponent {
  readonly settings = input<SiteSettings | null>(null);

  readonly backgroundImage = computed(() => {
    const url = this.settings()?.heroImageUrl;
    return url ? `url(${url})` : null;
  });

  readonly heroTagLine = computed(() => this.settings()?.heroTitle ?? 'Curadoria de Alto Padrão');

  readonly firstName = computed(() => {
    const name = this.settings()?.brokerName ?? '';
    const lastSpace = name.lastIndexOf(' ');
    return lastSpace >= 0 ? name.slice(0, lastSpace) : name;
  });

  readonly lastName = computed(() => {
    const name = this.settings()?.brokerName ?? '';
    const lastSpace = name.lastIndexOf(' ');
    return lastSpace >= 0 ? name.slice(lastSpace + 1) : '';
  });
}
