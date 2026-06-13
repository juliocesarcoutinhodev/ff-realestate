import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { Testimonial } from '../../../../core/models/testimonial.model';

@Component({
  selector: 'app-testimonials-section',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<section aria-label="Depoimentos"></section>`,
})
export class TestimonialsSectionComponent {
  readonly testimonials = input<Testimonial[]>([]);
}
