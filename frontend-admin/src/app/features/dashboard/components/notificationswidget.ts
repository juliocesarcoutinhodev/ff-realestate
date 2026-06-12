import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { RatingModule } from 'primeng/rating';
import { FormsModule } from '@angular/forms';
import { PendingTestimonial } from '@/app/core/models';

export interface ReviewEvent {
    id: string;
    status: 'APPROVED' | 'REJECTED';
}

@Component({
    standalone: true,
    selector: 'app-notifications-widget',
    imports: [TableModule, ButtonModule, RatingModule, FormsModule, DatePipe, RouterLink],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-4">
                <span class="font-semibold text-xl">Depoimentos Pendentes</span>
                <a routerLink="/testimonials" class="text-primary text-sm font-medium hover:underline cursor-pointer">Ver todos</a>
            </div>

            <p-table [value]="testimonials()" responsiveLayout="scroll">
                <ng-template #header>
                    <tr>
                        <th>Cliente</th>
                        <th>Avaliação</th>
                        <th>Trecho</th>
                        <th>Data</th>
                        <th>Ações</th>
                    </tr>
                </ng-template>
                <ng-template #body let-t>
                    <tr>
                        <td style="min-width:8rem" class="font-medium">{{ t.clientName }}</td>
                        <td style="min-width:8rem">
                            <p-rating [ngModel]="t.rating" [readonly]="true" />
                        </td>
                        <td style="min-width:12rem" class="text-muted-color text-sm">
                            {{ t.text.length > 80 ? t.text.slice(0, 80) + '...' : t.text }}
                        </td>
                        <td style="min-width:7rem">{{ t.createdAt | date: 'dd/MM/yyyy' }}</td>
                        <td style="min-width:10rem">
                            <div class="flex gap-1">
                                <p-button label="Aprovar" icon="pi pi-check" size="small" severity="success" [text]="true" (onClick)="review.emit({ id: t.id, status: 'APPROVED' })" />
                                <p-button label="Rejeitar" icon="pi pi-times" size="small" severity="danger" [text]="true" (onClick)="review.emit({ id: t.id, status: 'REJECTED' })" />
                            </div>
                        </td>
                    </tr>
                </ng-template>
                <ng-template #emptymessage>
                    <tr>
                        <td colspan="5" class="text-center py-8">
                            <div class="flex flex-col items-center gap-2 text-muted-color">
                                <i class="pi pi-check-circle text-3xl text-green-400"></i>
                                <span class="text-sm">Nenhum depoimento aguardando aprovação.</span>
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>
        </div>
    `
})
export class NotificationsWidget {
    readonly testimonials = input<PendingTestimonial[]>([]);
    readonly review = output<ReviewEvent>();
}
