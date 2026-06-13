import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

@Component({
  selector: 'app-property-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<main></main>`,
})
export default class PropertyListComponent implements OnInit {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  ngOnInit(): void {
    this.title.setTitle('Imóveis | Fabrício Faceroli');
    this.meta.updateTag({
      name: 'description',
      content: 'Explore o catálogo completo de imóveis para compra e locação com Fabrício Faceroli.',
    });
  }
}
