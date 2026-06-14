import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { Testimonial } from '../../../../core/models/testimonial.model';

@Component({
  selector: 'app-testimonials-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './testimonials-section.html',
})
export class TestimonialsSectionComponent {
  readonly testimonials = input<Testimonial[]>([]);

  protected readonly starIndexes = [1, 2, 3, 4, 5];
}
