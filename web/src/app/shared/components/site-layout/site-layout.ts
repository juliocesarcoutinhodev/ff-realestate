import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { HeaderComponent } from '../header/header';
import { FooterComponent } from '../footer/footer';
import { WhatsappFabComponent } from '../whatsapp-fab/whatsapp-fab';

@Component({
  selector: 'app-site-layout',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, HeaderComponent, FooterComponent, WhatsappFabComponent],
  host: { class: 'relative block min-h-screen' },
  template: `
    <app-header />
    <router-outlet />
    <app-footer />
    <app-whatsapp-fab />
  `,
})
export class SiteLayoutComponent {}
