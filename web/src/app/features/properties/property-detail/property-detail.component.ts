import { ChangeDetectionStrategy, Component, effect, inject, input } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

@Component({
  selector: 'app-property-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<main></main>`,
})
export default class PropertyDetailComponent {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  readonly slug = input<string>();

  constructor() {
    effect(() => {
      const slug = this.slug();
      if (!slug) return;
      this.title.setTitle('Imóvel | Fabrício Faceroli');
      this.meta.updateTag({
        name: 'description',
        content: 'Detalhes completos do imóvel. Consulte Fabrício Faceroli, corretor de imóveis.',
      });
    });
  }
}
