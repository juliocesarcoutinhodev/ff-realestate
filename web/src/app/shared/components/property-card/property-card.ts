import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { Property } from '../../../core/models/property.model';

@Component({
  selector: 'app-property-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './property-card.html',
})
export class PropertyCardComponent {
  readonly property = input.required<Property>();

  readonly location = computed(() => {
    const p = this.property();
    return p.neighborhood ? `${p.neighborhood}, ${p.city}` : p.city;
  });

  readonly dealTypeLabel = computed(() =>
    this.property().dealType === 'SALE' ? 'Venda' : 'Aluguel',
  );

  readonly isRent = computed(() => this.property().dealType === 'RENT');
}
