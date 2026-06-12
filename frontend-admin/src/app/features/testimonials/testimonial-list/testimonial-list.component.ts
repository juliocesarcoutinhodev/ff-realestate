import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { map, switchMap, tap } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Paginator, PaginatorState } from 'primeng/paginator';
import { RatingModule } from 'primeng/rating';
import { SelectButton } from 'primeng/selectbutton';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PageResponse, Testimonial, TestimonialFilters } from '@/app/core/models';
import { TestimonialService } from '../services/testimonial.service';
import { TestimonialDetailDialogComponent } from '../testimonial-detail-dialog/testimonial-detail-dialog.component';

@Component({
    selector: 'app-testimonial-list',
    standalone: true,
    imports: [
        DatePipe, FormsModule,
        TableModule, ButtonModule, TooltipModule, SkeletonModule,
        SelectButton, RatingModule, Tag, Paginator, ConfirmDialog,
        TestimonialDetailDialogComponent
    ],
    providers: [ConfirmationService],
    template: `
        <p-confirmdialog />

        <app-testimonial-detail-dialog
            [visible]="dialogVisible()"
            (visibleChange)="dialogVisible.set($event)"
            [testimonial]="selectedTestimonial()"
            (reviewed)="onReviewed($event)"
        />

        <div class="card">
            <div class="flex items-center justify-between mb-6">
                <span class="text-xl font-semibold">Depoimentos</span>
            </div>

            <div class="mb-4">
                <p-selectButton
                    [options]="statusOptions"
                    [ngModel]="statusFilter()"
                    (ngModelChange)="onStatusChange($event)"
                />
            </div>

            <p-table [value]="testimonials()?.content ?? []" [loading]="loading()" responsiveLayout="scroll">
                <ng-template #loadingbody>
                    @for (_ of skeletonRows; track _) {
                        <tr>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton width="7rem" height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton width="6rem" height="1.5rem" /></td>
                            <td><p-skeleton width="7rem" height="1.5rem" /></td>
                            <td>
                                <div class="flex justify-center">
                                    <p-skeleton width="2rem" height="2rem" borderRadius="50%" />
                                </div>
                            </td>
                        </tr>
                    }
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th style="min-width:10rem">Cliente</th>
                        <th style="width:8rem">Avaliação</th>
                        <th style="min-width:14rem">Trecho</th>
                        <th style="min-width:10rem">Imóvel</th>
                        <th style="width:9rem">Status</th>
                        <th style="width:10rem">Data</th>
                        <th style="width:5rem" class="text-center">Ações</th>
                    </tr>
                </ng-template>

                <ng-template #body let-testimonial>
                    <tr class="cursor-pointer hover:bg-surface-50" (click)="openDetail(testimonial)">
                        <td class="font-medium">{{ testimonial.clientName }}</td>
                        <td>
                            <p-rating [ngModel]="testimonial.rating" [readonly]="true" />
                        </td>
                        <td class="text-muted-color text-sm">{{ excerpt(testimonial.text) }}</td>
                        <td class="text-sm">{{ testimonial.property?.title ?? '—' }}</td>
                        <td>
                            <p-tag
                                [value]="statusLabel(testimonial.status)"
                                [severity]="statusSeverity(testimonial.status)"
                            />
                        </td>
                        <td class="text-sm text-muted-color">{{ testimonial.createdAt | date: 'dd/MM/yyyy' }}</td>
                        <td class="text-center" (click)="$event.stopPropagation()">
                            <p-button
                                icon="pi pi-trash"
                                [text]="true"
                                severity="danger"
                                size="small"
                                pTooltip="Excluir"
                                tooltipPosition="top"
                                (onClick)="confirmDelete(testimonial)"
                            />
                        </td>
                    </tr>
                </ng-template>

                <ng-template #emptymessage>
                    <tr>
                        <td colspan="7" class="text-center py-12">
                            <div class="flex flex-col items-center gap-3 text-muted-color">
                                <i class="pi pi-comment text-4xl"></i>
                                <span>Nenhum depoimento encontrado.</span>
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>

            @if ((testimonials()?.totalElements ?? 0) > 0) {
                <p-paginator
                    [first]="currentPage() * pageSize"
                    [rows]="pageSize"
                    [totalRecords]="testimonials()?.totalElements ?? 0"
                    (onPageChange)="onPageChange($event)"
                    styleClass="mt-2"
                />
            }
        </div>
    `
})
export class TestimonialListComponent {
    private readonly testimonialService = inject(TestimonialService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly messageService = inject(MessageService);
    private readonly destroyRef = inject(DestroyRef);

    readonly pageSize = 10;
    readonly skeletonRows = Array.from({ length: 10 }, (_, i) => i);

    readonly testimonials = signal<PageResponse<Testimonial> | null>(null);
    readonly loading = signal(true);
    readonly statusFilter = signal<'PENDING' | 'APPROVED' | 'REJECTED' | null>('PENDING');
    readonly currentPage = signal(0);

    readonly selectedTestimonial = signal<Testimonial | null>(null);
    readonly dialogVisible = signal(false);

    readonly statusOptions = [
        { label: 'Todos', value: null },
        { label: 'Pendentes', value: 'PENDING' },
        { label: 'Aprovados', value: 'APPROVED' },
        { label: 'Rejeitados', value: 'REJECTED' }
    ];

    private readonly activeFilters = computed<TestimonialFilters>(() => ({
        status: this.statusFilter() ?? undefined,
        page: this.currentPage(),
        size: this.pageSize
    }));

    constructor() {
        toObservable(this.activeFilters)
            .pipe(
                tap(() => this.loading.set(true)),
                switchMap((filters) =>
                    this.testimonialService.findAll(filters).pipe(map((r) => r.data))
                ),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (data) => {
                    this.testimonials.set(data ?? null);
                    this.loading.set(false);
                }
            });
    }

    openDetail(testimonial: Testimonial): void {
        this.selectedTestimonial.set(testimonial);
        this.dialogVisible.set(true);
    }

    onReviewed(updated: Testimonial): void {
        const filter = this.statusFilter();
        this.testimonials.update((page) => {
            if (!page) return null;
            const stillMatches = filter === null || filter === updated.status;
            const content = stillMatches
                ? page.content.map((t) => (t.id === updated.id ? updated : t))
                : page.content.filter((t) => t.id !== updated.id);
            return { ...page, content, totalElements: stillMatches ? page.totalElements : page.totalElements - 1 };
        });
    }

    onStatusChange(value: 'PENDING' | 'APPROVED' | 'REJECTED' | null): void {
        this.currentPage.set(0);
        this.statusFilter.set(value);
    }

    onPageChange(event: PaginatorState): void {
        this.currentPage.set(event.page ?? 0);
    }

    confirmDelete(testimonial: Testimonial): void {
        this.confirmationService.confirm({
            message: `Deseja excluir o depoimento de "${testimonial.clientName}"? Esta ação não pode ser desfeita.`,
            header: 'Excluir depoimento',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Excluir',
            rejectLabel: 'Cancelar',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => this.deleteTestimonial(testimonial)
        });
    }

    private deleteTestimonial(testimonial: Testimonial): void {
        this.testimonialService
            .delete(testimonial.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: () => {
                    this.testimonials.update((page) =>
                        page
                            ? { ...page, content: page.content.filter((t) => t.id !== testimonial.id), totalElements: page.totalElements - 1 }
                            : null
                    );
                    this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Depoimento excluído com sucesso.' });
                }
            });
    }

    statusLabel(status: string): string {
        const map: Record<string, string> = { PENDING: 'Pendente', APPROVED: 'Aprovado', REJECTED: 'Rejeitado' };
        return map[status] ?? status;
    }

    statusSeverity(status: string): 'warn' | 'success' | 'danger' | 'info' {
        const map: Record<string, 'warn' | 'success' | 'danger'> = {
            PENDING: 'warn', APPROVED: 'success', REJECTED: 'danger'
        };
        return map[status] ?? 'info';
    }

    excerpt(text: string): string {
        return text.length > 80 ? text.substring(0, 80) + '...' : text;
    }
}
