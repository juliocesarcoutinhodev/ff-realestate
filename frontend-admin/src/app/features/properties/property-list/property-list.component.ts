import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { map, switchMap, tap } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ImageModule } from 'primeng/image';
import { Paginator, PaginatorState } from 'primeng/paginator';
import { Select } from 'primeng/select';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Category, PageResponse, Property, PropertyFilters } from '@/app/core/models';
import { CategoryService } from '@/app/features/categories/services/category.service';
import { PropertyService } from '../services/property.service';

@Component({
    selector: 'app-property-list',
    standalone: true,
    imports: [CurrencyPipe, FormsModule, TableModule, ButtonModule, TooltipModule, SkeletonModule, Select, Tag, ImageModule, Paginator, ConfirmDialog],
    providers: [ConfirmationService],
    template: `
        <p-confirmdialog />

        <div class="card">
            <div class="flex items-center justify-between mb-6">
                <span class="text-xl font-semibold">Imóveis</span>
                <p-button label="Novo Imóvel" icon="pi pi-plus" (onClick)="navigateToCreate()" />
            </div>

            <div class="flex flex-wrap gap-3 mb-4">
                <p-select [options]="statusOptions" [ngModel]="statusFilter()" (ngModelChange)="onFilterChange({ status: $event })" placeholder="Todos os status" [showClear]="true" styleClass="w-48" />
                <p-select [options]="dealTypeOptions" [ngModel]="dealTypeFilter()" (ngModelChange)="onFilterChange({ dealType: $event })" placeholder="Tipo de negócio" [showClear]="true" styleClass="w-52" />
                <p-select [options]="categoryOptions()" [ngModel]="categoryFilter()" (ngModelChange)="onFilterChange({ categoryId: $event })" placeholder="Categoria" [showClear]="true" styleClass="w-52" />
            </div>

            <p-table [value]="properties()?.content ?? []" [loading]="loading()" responsiveLayout="scroll">
                <ng-template #loadingbody>
                    @for (_ of skeletonRows; track _) {
                        <tr>
                            <td><p-skeleton width="3rem" height="2.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton width="5rem" height="1.5rem" /></td>
                            <td>
                                <div class="flex justify-center gap-2">
                                    <p-skeleton width="2rem" height="2rem" borderRadius="50%" />
                                    <p-skeleton width="2rem" height="2rem" borderRadius="50%" />
                                    <p-skeleton width="2rem" height="2rem" borderRadius="50%" />
                                </div>
                            </td>
                        </tr>
                    }
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th style="width:4rem">Foto</th>
                        <th style="min-width:14rem">Título</th>
                        <th style="min-width:10rem">Categoria</th>
                        <th style="min-width:8rem">Tipo</th>
                        <th style="min-width:10rem">Preço</th>
                        <th style="min-width:8rem">Status</th>
                        <th style="width:8rem" class="text-center">Ações</th>
                    </tr>
                </ng-template>

                <ng-template #body let-property>
                    <tr class="cursor-pointer hover:bg-surface-50" (click)="navigateToEdit(property.id)">
                        <td>
                            @if (property.coverPhoto) {
                                <p-image [src]="property.coverPhoto" [alt]="property.title" width="48" [preview]="true" (click)="$event.stopPropagation()" />
                            } @else {
                                <div class="w-12 h-8 bg-surface-200 dark:bg-surface-700 rounded flex items-center justify-center">
                                    <i class="pi pi-image text-muted-color text-sm"></i>
                                </div>
                            }
                        </td>
                        <td class="font-medium">{{ property.title }}</td>
                        <td class="text-muted-color text-sm">{{ categoryMap().get(property.categoryId) ?? '—' }}</td>
                        <td>{{ property.dealType === 'SALE' ? 'Venda' : 'Aluguel' }}</td>
                        <td>{{ property.price | currency: 'BRL' : 'symbol' : '1.0-0' }}</td>
                        <td>
                            <p-tag [value]="property.status === 'ACTIVE' ? 'Ativo' : 'Inativo'" [severity]="property.status === 'ACTIVE' ? 'success' : 'secondary'" />
                        </td>
                        <td>
                            <div class="flex gap-1 justify-center">
                                <p-button icon="pi pi-pencil" [text]="true" severity="secondary" size="small" pTooltip="Editar" tooltipPosition="top" (onClick)="navigateToEdit(property.id, $event)" />
                                <p-button
                                    [icon]="property.status === 'ACTIVE' ? 'pi pi-ban' : 'pi pi-check-circle'"
                                    [text]="true"
                                    [severity]="property.status === 'ACTIVE' ? 'warn' : 'success'"
                                    size="small"
                                    [pTooltip]="property.status === 'ACTIVE' ? 'Inativar' : 'Ativar'"
                                    tooltipPosition="top"
                                    (onClick)="confirmToggleStatus(property, $event)"
                                />
                                <p-button icon="pi pi-trash" [text]="true" severity="danger" size="small" pTooltip="Excluir" tooltipPosition="top" (onClick)="confirmDelete(property, $event)" />
                            </div>
                        </td>
                    </tr>
                </ng-template>

                <ng-template #emptymessage>
                    <tr>
                        <td colspan="7" class="text-center py-12">
                            <div class="flex flex-col items-center gap-3 text-muted-color">
                                <i class="pi pi-home text-4xl"></i>
                                <span>Nenhum imóvel encontrado.</span>
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>

            @if ((properties()?.totalElements ?? 0) > 0) {
                <p-paginator [first]="currentPage() * pageSize" [rows]="pageSize" [totalRecords]="properties()?.totalElements ?? 0" (onPageChange)="onPageChange($event)" styleClass="mt-2" />
            }
        </div>
    `
})
export class PropertyListComponent {
    private readonly propertyService = inject(PropertyService);
    private readonly categoryService = inject(CategoryService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly messageService = inject(MessageService);
    private readonly router = inject(Router);
    private readonly destroyRef = inject(DestroyRef);

    readonly pageSize = 12;
    readonly skeletonRows = Array.from({ length: 12 }, (_, i) => i);

    readonly properties = signal<PageResponse<Property> | null>(null);
    readonly loading = signal(true);
    readonly categories = signal<Category[]>([]);

    readonly statusFilter = signal<'ACTIVE' | 'INACTIVE' | undefined>(undefined);
    readonly dealTypeFilter = signal<'SALE' | 'RENT' | undefined>(undefined);
    readonly categoryFilter = signal<string | undefined>(undefined);
    readonly currentPage = signal(0);

    readonly categoryMap = computed(() => new Map(this.categories().map((c) => [c.id, c.name])));
    readonly categoryOptions = computed(() => this.categories().map((c) => ({ label: c.name, value: c.id })));

    readonly statusOptions = [
        { label: 'Ativo', value: 'ACTIVE' },
        { label: 'Inativo', value: 'INACTIVE' }
    ];

    readonly dealTypeOptions = [
        { label: 'Venda', value: 'SALE' },
        { label: 'Aluguel', value: 'RENT' }
    ];

    private readonly activeFilters = computed<PropertyFilters>(() => ({
        status: this.statusFilter(),
        dealType: this.dealTypeFilter(),
        categoryId: this.categoryFilter(),
        page: this.currentPage(),
        size: this.pageSize
    }));

    constructor() {
        this.loadCategories();

        toObservable(this.activeFilters)
            .pipe(
                tap(() => this.loading.set(true)),
                switchMap((filters) => this.propertyService.findAll(filters).pipe(map((r) => r.data))),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (data) => {
                    this.properties.set(data);
                    this.loading.set(false);
                }
            });
    }

    navigateToCreate(): void {
        this.router.navigate(['/properties/new']);
    }

    navigateToEdit(id: string, event?: MouseEvent): void {
        event?.stopPropagation();
        this.router.navigate(['/properties', id, 'edit']);
    }

    onFilterChange(partial: Partial<PropertyFilters>): void {
        this.currentPage.set(0);
        if ('status' in partial) this.statusFilter.set(partial.status ?? undefined);
        if ('dealType' in partial) this.dealTypeFilter.set(partial.dealType ?? undefined);
        if ('categoryId' in partial) this.categoryFilter.set(partial.categoryId ?? undefined);
    }

    onPageChange(event: PaginatorState): void {
        this.currentPage.set(event.page ?? 0);
    }

    confirmToggleStatus(property: Property, event: MouseEvent): void {
        event.stopPropagation();
        const isActive = property.status === 'ACTIVE';
        this.confirmationService.confirm({
            message: isActive ? `Deseja inativar o imóvel "${property.title}"? Ele será removido do site público.` : `Deseja ativar o imóvel "${property.title}"? Ele voltará a aparecer no site público.`,
            header: isActive ? 'Inativar imóvel' : 'Ativar imóvel',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: isActive ? 'Inativar' : 'Ativar',
            rejectLabel: 'Cancelar',
            acceptButtonStyleClass: isActive ? 'p-button-warning' : 'p-button-success',
            accept: () => this.toggleStatus(property)
        });
    }

    confirmDelete(property: Property, event: MouseEvent): void {
        event.stopPropagation();
        this.confirmationService.confirm({
            message: `Deseja excluir permanentemente o imóvel "${property.title}"? Todas as fotos serão removidas e esta ação não pode ser desfeita.`,
            header: 'Confirmar exclusão',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Excluir',
            rejectLabel: 'Cancelar',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => this.deleteProperty(property.id)
        });
    }

    private loadCategories(): void {
        this.categoryService
            .findAll()
            .pipe(
                map((r) => r.data),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({ next: (data) => this.categories.set(data) });
    }

    private toggleStatus(property: Property): void {
        const newStatus = property.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        this.propertyService
            .toggleStatus(property.id, newStatus)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: () => {
                    this.properties.update((page) => (page ? { ...page, content: page.content.map((p) => (p.id === property.id ? { ...p, status: newStatus } : p)) } : page));
                }
            });
    }

    private deleteProperty(id: string): void {
        this.propertyService
            .delete(id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: () => {
                    this.properties.update((page) => (page ? { ...page, content: page.content.filter((p) => p.id !== id), totalElements: page.totalElements - 1 } : page));
                    this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Imóvel excluído com sucesso.' });
                }
            });
    }
}
