import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PhotoGalleryComponent } from './photo-gallery/photo-gallery.component';
import { ContactSidebarComponent } from './contact-sidebar/contact-sidebar.component';
import { ContactBottomBarComponent } from './contact-bottom-bar/contact-bottom-bar.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Meta, Title } from '@angular/platform-browser';
import { finalize } from 'rxjs/operators';

import { Property } from '../../../core/models/property.model';
import { PropertyService } from '../../../core/services/property.service';
import { SiteSettingsService } from '../../../core/services/site-settings.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-property-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, PhotoGalleryComponent, ContactSidebarComponent, ContactBottomBarComponent],
  templateUrl: './property-detail.component.html',
})
export default class PropertyDetailComponent implements OnInit {
  private readonly document = inject(DOCUMENT);
  private readonly route = inject(ActivatedRoute);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);
  private readonly propertyService = inject(PropertyService);
  private readonly siteSettingsService = inject(SiteSettingsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly settings = this.siteSettingsService.settings;
  readonly previousFilters = this.propertyService.previousFilters;

  readonly property = signal<Property | null>(null);
  readonly loading = signal(true);
  readonly notFound = signal(false);

  readonly location = computed(() => {
    const p = this.property();
    if (!p) return '';
    return p.neighborhood ? `${p.neighborhood} · ${p.city}` : p.city;
  });

  ngOnInit(): void {
    if (!this.siteSettingsService.settings()) {
      this.siteSettingsService.get().pipe(takeUntilDestroyed(this.destroyRef)).subscribe();
    }

    const slug = this.route.snapshot.params['slug'] as string;
    if (!slug) {
      this.loading.set(false);
      this.notFound.set(true);
      return;
    }

    this.propertyService
      .findBySlug(slug)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: response => {
          this.property.set(response.data);
          this.setMetaTags(response.data);
        },
        error: () => this.notFound.set(true),
      });
  }

  private setMetaTags(prop: Property): void {
    const description = prop.description
      ? prop.description.slice(0, 160)
      : 'Consulte Fabrício Faceroli, corretor de imóveis.';

    this.title.setTitle(`${prop.title} · Fabrício Faceroli`);
    this.meta.updateTag({ name: 'description', content: description });
    this.meta.updateTag({ property: 'og:title', content: prop.title });
    this.meta.updateTag({ property: 'og:description', content: description });
    if (prop.coverPhoto) {
      this.meta.updateTag({ property: 'og:image', content: prop.coverPhoto });
    }

    this.addCanonical(prop.slug);
    this.addJsonLd(prop);

    this.destroyRef.onDestroy(() => {
      this.document.querySelector('link[rel="canonical"]')?.remove();
      this.document.getElementById('property-detail-ld')?.remove();
    });
  }

  private addCanonical(slug: string): void {
    const canonicalUrl = `${environment.siteUrl}/properties/${slug}`;
    let link = this.document.querySelector<HTMLLinkElement>('link[rel="canonical"]');
    if (!link) {
      link = this.document.createElement('link');
      link.setAttribute('rel', 'canonical');
      this.document.head.appendChild(link);
    }
    link.setAttribute('href', canonicalUrl);
  }

  private addJsonLd(prop: Property): void {
    this.document.getElementById('property-detail-ld')?.remove();
    const script = this.document.createElement('script');
    script.id = 'property-detail-ld';
    script.type = 'application/ld+json';
    script.text = JSON.stringify({
      '@context': 'https://schema.org',
      '@type': 'RealEstateListing',
      name: prop.title,
      description: prop.description ?? '',
      price: prop.price,
      image: prop.coverPhoto ?? '',
    });
    this.document.head.appendChild(script);
  }
}
