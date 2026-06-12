import { Component, DestroyRef, inject, input, model, output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { RatingModule } from 'primeng/rating';
import { Tag } from 'primeng/tag';
import { Testimonial } from '@/app/core/models';
import { TestimonialService } from '../services/testimonial.service';

@Component({
    selector: 'app-testimonial-detail-dialog',
    standalone: true,
    imports: [DatePipe, FormsModule, ButtonModule, Dialog, RatingModule, Tag],
    template: `
        <p-dialog
            [visible]="visible()"
            (visibleChange)="visible.set($event)"
            header="Depoimento"
            [modal]="true"
            [draggable]="false"
            [resizable]="false"
            [style]="{ width: '600px' }"
        >
            @if (testimonial()) {
                <!-- Cabeçalho do conteúdo: nome, estrelas e data -->
                <div class="flex flex-col gap-1 mb-4">
                    <span class="font-semibold text-lg">{{ testimonial()!.clientName }}</span>
                    <div class="flex items-center gap-3">
                        <p-rating [ngModel]="testimonial()!.rating" [readonly]="true" />
                        <span class="text-muted-color text-sm">{{ testimonial()!.createdAt | date: 'dd/MM/yyyy' }}</span>
                    </div>
                    @if (testimonial()!.property) {
                        <span class="text-sm text-muted-color">
                            Imóvel: <span class="font-medium text-color">{{ testimonial()!.property!.title }}</span>
                        </span>
                    }
                </div>

                <p class="text-sm leading-relaxed whitespace-pre-line border-t border-surface-200 pt-4">
                    {{ testimonial()!.text }}
                </p>
            }

            <ng-template #footer>
                @if (testimonial()?.status === 'PENDING') {
                    <div class="flex justify-between w-full">
                        <p-button
                            label="Rejeitar"
                            icon="pi pi-times"
                            severity="danger"
                            [loading]="loading()"
                            [disabled]="loading()"
                            (onClick)="review('REJECTED')"
                        />
                        <p-button
                            label="Aprovar"
                            icon="pi pi-check"
                            severity="success"
                            [loading]="loading()"
                            [disabled]="loading()"
                            (onClick)="review('APPROVED')"
                        />
                    </div>
                } @else if (testimonial()) {
                    <div class="flex justify-end w-full">
                        <p-tag
                            [value]="statusLabel(testimonial()!.status)"
                            [severity]="statusSeverity(testimonial()!.status)"
                        />
                    </div>
                }
            </ng-template>
        </p-dialog>
    `
})
export class TestimonialDetailDialogComponent {
    visible = model(false);
    testimonial = input<Testimonial | null>(null);
    reviewed = output<Testimonial>();

    readonly loading = signal(false);

    private readonly testimonialService = inject(TestimonialService);
    private readonly destroyRef = inject(DestroyRef);

    review(status: 'APPROVED' | 'REJECTED'): void {
        const t = this.testimonial();
        if (!t) return;

        this.loading.set(true);
        this.testimonialService.review(t.id, status)
            .pipe(
                finalize(() => this.loading.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => {
                    this.visible.set(false);
                    this.reviewed.emit({ ...t, status: r.data.status });
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
}
