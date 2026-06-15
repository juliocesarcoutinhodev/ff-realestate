import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { Category } from '../../../core/models/category.model';
import { PropertyFilterSelection } from '../../../core/models/property.model';

interface DealTypeOption {
  value: 'SALE' | 'RENT' | null;
  label: string;
}

@Component({
  selector: 'app-filter-bar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './filter-bar.component.html',
  host: {
    '(document:keydown.escape)': 'handleEscape()',
  },
})
export class FilterBarComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly queryParams = toSignal(this.route.queryParams, { initialValue: {} });

  readonly categories = input<Category[]>([]);
  readonly cities = input<string[]>([]);
  readonly totalElements = input<number>(0);

  readonly filtersChanged = output<PropertyFilterSelection>();

  readonly selectedDealType = computed<'SALE' | 'RENT' | null>(
    () => this.queryParams()['type'] ?? null,
  );
  readonly selectedCategorySlug = computed<string | null>(
    () => this.queryParams()['category'] ?? null,
  );
  readonly selectedCity = computed<string | null>(() => this.queryParams()['city'] ?? null);

  readonly hasActiveFilters = computed(
    () =>
      !!this.selectedDealType() || !!this.selectedCategorySlug() || !!this.selectedCity(),
  );

  readonly isSheetOpen = signal(false);
  readonly pendingDealType = signal<'SALE' | 'RENT' | null>(null);
  readonly pendingCategorySlug = signal<string | null>(null);
  readonly pendingCity = signal<string | null>(null);

  protected readonly dealTypeOptions: DealTypeOption[] = [
    { value: null, label: 'Todos' },
    { value: 'SALE', label: 'Venda' },
    { value: 'RENT', label: 'Aluguel' },
  ];

  onDesktopDealTypeChange(value: 'SALE' | 'RENT' | null): void {
    this.filtersChanged.emit({
      dealType: value,
      categorySlug: this.selectedCategorySlug(),
      city: this.selectedCity(),
    });
  }

  onDesktopCategoryChange(slug: string | null): void {
    this.filtersChanged.emit({
      dealType: this.selectedDealType(),
      categorySlug: slug,
      city: this.selectedCity(),
    });
  }

  onDesktopCityChange(city: string | null): void {
    this.filtersChanged.emit({
      dealType: this.selectedDealType(),
      categorySlug: this.selectedCategorySlug(),
      city,
    });
  }

  openSheet(): void {
    this.pendingDealType.set(this.selectedDealType());
    this.pendingCategorySlug.set(this.selectedCategorySlug());
    this.pendingCity.set(this.selectedCity());
    this.isSheetOpen.set(true);
  }

  closeSheet(): void {
    this.isSheetOpen.set(false);
  }

  applySheet(): void {
    this.filtersChanged.emit({
      dealType: this.pendingDealType(),
      categorySlug: this.pendingCategorySlug(),
      city: this.pendingCity(),
    });
    this.isSheetOpen.set(false);
  }

  clearAndClose(): void {
    this.filtersChanged.emit({ dealType: null, categorySlug: null, city: null });
    this.isSheetOpen.set(false);
  }

  handleEscape(): void {
    if (this.isSheetOpen()) this.closeSheet();
  }

  protected chipClass(active: boolean): string {
    return active
      ? 'font-sans text-sm font-medium px-4 py-1.5 rounded-full bg-gradient-gold text-gold-foreground transition-all duration-200'
      : 'font-sans text-sm font-medium px-4 py-1.5 rounded-full border border-border text-muted hover:border-gold hover:text-foreground transition-all duration-200';
  }

  protected selectClass(): string {
    return 'font-sans text-sm bg-background border border-border text-foreground rounded-sm px-4 py-2 focus:outline-none focus:border-gold transition-colors duration-200 cursor-pointer';
  }
}
