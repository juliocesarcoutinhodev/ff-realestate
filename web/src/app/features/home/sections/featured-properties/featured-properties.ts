import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Property } from '../../../../core/models/property.model';
import { PropertyCardComponent } from '../../../../shared/components/property-card/property-card';

@Component({
  selector: 'app-featured-properties',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, PropertyCardComponent],
  templateUrl: './featured-properties.html',
})
export class FeaturedPropertiesComponent {
  readonly properties = input<Property[]>([]);

  protected readonly skeletonItems = [0, 1, 2];
}
