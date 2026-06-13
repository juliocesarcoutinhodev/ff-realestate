import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Meta, Title } from '@angular/platform-browser';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SiteSettings } from '../../core/models/site-settings.model';
import { Property } from '../../core/models/property.model';
import { Testimonial } from '../../core/models/testimonial.model';
import { SiteSettingsService } from '../../core/services/site-settings.service';
import { PropertyService } from '../../core/services/property.service';
import { TestimonialService } from '../../core/services/testimonial.service';
import { HeroSectionComponent } from './sections/hero-section/hero-section';
import { FeaturedPropertiesComponent } from './sections/featured-properties/featured-properties';
import { TestimonialsSectionComponent } from './sections/testimonials-section/testimonials-section';
import { AboutSectionComponent } from './sections/about-section/about-section';
import { ContactSectionComponent } from './sections/contact-section/contact-section';

@Component({
  selector: 'app-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    HeroSectionComponent,
    FeaturedPropertiesComponent,
    TestimonialsSectionComponent,
    AboutSectionComponent,
    ContactSectionComponent,
  ],
  template: `
    <main>
      <app-hero-section [settings]="settings()" />
      <app-featured-properties [properties]="featuredProperties()" />
      <app-testimonials-section [testimonials]="testimonials()" />
      <app-about-section [settings]="settings()" />
      <app-contact-section [settings]="settings()" />
    </main>
  `,
})
export default class HomeComponent implements OnInit {
  private readonly titleService = inject(Title);
  private readonly metaService = inject(Meta);
  private readonly siteSettingsService = inject(SiteSettingsService);
  private readonly propertyService = inject(PropertyService);
  private readonly testimonialService = inject(TestimonialService);
  private readonly destroyRef = inject(DestroyRef);

  readonly settings = signal<SiteSettings | null>(null);
  readonly featuredProperties = signal<Property[]>([]);
  readonly testimonials = signal<Testimonial[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    forkJoin({
      settings: this.siteSettingsService.get(),
      properties: this.propertyService.findAll({ featured: true, size: 3 }),
      testimonials: this.testimonialService.findAll(),
    })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe(({ settings, properties, testimonials }) => {
        this.settings.set(settings.data);
        this.featuredProperties.set(properties.data.content);
        this.testimonials.set(testimonials.data);
        this.updateMetaTags(settings.data);
      });
  }

  private updateMetaTags(settings: SiteSettings): void {
    const title = `${settings.brokerName} · Corretor de Imóveis`;
    this.titleService.setTitle(title);
    this.metaService.updateTag({ name: 'description', content: settings.metaDescription ?? '' });
    this.metaService.updateTag({ property: 'og:title', content: title });
    this.metaService.updateTag({ property: 'og:description', content: settings.metaDescription ?? '' });
    if (settings.heroImageUrl) {
      this.metaService.updateTag({ property: 'og:image', content: settings.heroImageUrl });
    }
  }
}
