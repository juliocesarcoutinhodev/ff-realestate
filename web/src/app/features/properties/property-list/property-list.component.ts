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
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize, shareReplay, switchMap } from 'rxjs/operators';

import { environment } from '../../../../environments/environment';

import { Category } from '../../../core/models/category.model';
import { Property, PropertyFilterSelection } from '../../../core/models/property.model';
import { CategoryService } from '../../../core/services/category.service';
import { PropertyService } from '../../../core/services/property.service';
import { FilterBarComponent } from '../filter-bar/filter-bar.component';
import { PropertyCardComponent } from '../../../shared/components/property-card/property-card';

@Component({
  selector: 'app-property-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FilterBarComponent, PropertyCardComponent],
  templateUrl: './property-list.component.html',
})
export default class PropertyListComponent implements OnInit {
  private readonly document = inject(DOCUMENT);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly propertyService = inject(PropertyService);
  private readonly categoryService = inject(CategoryService);
  private readonly destroyRef = inject(DestroyRef);

  private readonly categories$ = this.categoryService.findAll().pipe(shareReplay(1));

  readonly properties = signal<Property[]>([]);
  readonly categories = signal<Category[]>([]);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly currentPage = signal(0);
  readonly loading = signal(true);
  readonly loadingMore = signal(false);
  readonly selectedDealType = signal<'SALE' | 'RENT' | null>(null);
  readonly selectedCategorySlug = signal<string | null>(null);
  readonly selectedCity = signal<string | null>(null);

  readonly cities = computed(() =>
    [...new Set(this.properties().map(p => p.city).filter(Boolean))].sort(),
  );

  protected readonly skeletonItems = [0, 1, 2, 3, 4, 5];

  ngOnInit(): void {
    this.setMetaTags();
    this.setupReactiveLoading();
    this.saveFiltersOnLeave();
  }

  onFiltersChanged(selection: PropertyFilterSelection): void {
    const queryParams: Record<string, string> = {};
    if (selection.dealType) queryParams['type'] = selection.dealType;
    if (selection.categorySlug) queryParams['category'] = selection.categorySlug;
    if (selection.city) queryParams['city'] = selection.city;
    this.router.navigate(['/properties'], { queryParams });
  }

  loadMore(): void {
    const nextPage = this.currentPage() + 1;
    const params = this.route.snapshot.queryParams;
    const categoryId = params['category']
      ? this.categories().find(c => c.slug === params['category'])?.id
      : undefined;

    this.loadingMore.set(true);
    this.propertyService
      .findAll({
        page: nextPage,
        size: 12,
        dealType: params['type'] ?? undefined,
        categoryId,
        city: params['city'] ?? undefined,
      })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loadingMore.set(false)),
      )
      .subscribe(response => {
        this.properties.update(prev => [...prev, ...response.data.content]);
        this.currentPage.set(nextPage);
      });
  }

  private setupReactiveLoading(): void {
    this.route.queryParams
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        switchMap(params => {
          this.selectedDealType.set(params['type'] ?? null);
          this.selectedCategorySlug.set(params['category'] ?? null);
          this.selectedCity.set(params['city'] ?? null);
          this.loading.set(true);
          this.currentPage.set(0);

          return this.categories$.pipe(
            switchMap(catResponse => {
              this.categories.set(catResponse.data);
              const categoryId = params['category']
                ? catResponse.data.find((c: Category) => c.slug === params['category'])?.id
                : undefined;

              return this.propertyService.findAll({
                page: 0,
                size: 12,
                dealType: params['type'] ?? undefined,
                categoryId,
                city: params['city'] ?? undefined,
              });
            }),
          );
        }),
      )
      .subscribe(response => {
        this.properties.set(response.data.content);
        this.totalElements.set(response.data.totalElements);
        this.totalPages.set(response.data.totalPages);
        this.loading.set(false);
      });
  }

  private saveFiltersOnLeave(): void {
    this.destroyRef.onDestroy(() => {
      const params: Record<string, string> = {};
      if (this.selectedDealType()) params['type'] = this.selectedDealType()!;
      if (this.selectedCategorySlug()) params['category'] = this.selectedCategorySlug()!;
      if (this.selectedCity()) params['city'] = this.selectedCity()!;
      this.propertyService.savePreviousFilters(params);
    });
  }

  private setMetaTags(): void {
    this.title.setTitle('Imóveis · Fabrício Faceroli');
    this.meta.updateTag({
      name: 'description',
      content: 'Portfólio de imóveis à venda e para locação com Fabrício Faceroli Corretor.',
    });

    this.addCanonical();
    this.addJsonLd();

    this.destroyRef.onDestroy(() => {
      this.document.querySelector('link[rel="canonical"]')?.remove();
      this.document.getElementById('property-list-ld')?.remove();
    });
  }

  private addCanonical(): void {
    const canonicalUrl = `${environment.siteUrl}/properties`;
    let link = this.document.querySelector<HTMLLinkElement>('link[rel="canonical"]');
    if (!link) {
      link = this.document.createElement('link');
      link.setAttribute('rel', 'canonical');
      this.document.head.appendChild(link);
    }
    link.setAttribute('href', canonicalUrl);
  }

  private addJsonLd(): void {
    this.document.getElementById('property-list-ld')?.remove();
    const script = this.document.createElement('script');
    script.id = 'property-list-ld';
    script.type = 'application/ld+json';
    script.text = JSON.stringify({
      '@context': 'https://schema.org',
      '@type': 'ItemList',
      'name': 'Imóveis Fabrício Faceroli',
    });
    this.document.head.appendChild(script);
  }
}
