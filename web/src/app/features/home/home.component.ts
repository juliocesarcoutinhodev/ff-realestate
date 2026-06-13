import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<main></main>`,
})
export default class HomeComponent implements OnInit {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  ngOnInit(): void {
    this.title.setTitle('Fabrício Faceroli - Corretor de Imóveis');
    this.meta.updateTag({
      name: 'description',
      content: 'Encontre os melhores imóveis com Fabrício Faceroli, corretor especializado em imóveis de alto padrão.',
    });
  }
}
