import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

@Component({
  selector: 'app-not-found',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<main></main>`,
})
export default class NotFoundComponent implements OnInit {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  ngOnInit(): void {
    this.title.setTitle('Página não encontrada | Fabrício Faceroli');
    this.meta.updateTag({ name: 'robots', content: 'noindex, nofollow' });
  }
}
