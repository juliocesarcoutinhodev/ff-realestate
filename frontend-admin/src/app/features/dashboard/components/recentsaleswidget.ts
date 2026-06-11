import { Component, inject, input } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { RecentProperty } from '@/app/core/models';

@Component({
    standalone: true,
    selector: 'app-recent-sales-widget',
    imports: [TableModule, TagModule, ButtonModule, CurrencyPipe, DatePipe, RouterLink],
    template: `
        <div class="card mb-8!">
            <div class="flex items-center justify-between mb-4">
                <span class="font-semibold text-xl">Imóveis Recentes</span>
                <a routerLink="/properties" class="text-primary text-sm font-medium hover:underline cursor-pointer">Ver todos</a>
            </div>

            <p-table [value]="properties()" responsiveLayout="scroll">
                <ng-template #header>
                    <tr>
                        <th>Título</th>
                        <th>Tipo</th>
                        <th>Preço</th>
                        <th>Status</th>
                        <th>Cadastrado em</th>
                        <th>Ações</th>
                    </tr>
                </ng-template>
                <ng-template #body let-p>
                    <tr>
                        <td style="min-width:10rem">{{ p.title }}</td>
                        <td style="min-width:6rem">{{ p.dealType === 'SALE' ? 'Venda' : 'Aluguel' }}</td>
                        <td style="min-width:8rem">{{ p.price | currency:'BRL':'symbol':'1.0-0' }}</td>
                        <td style="min-width:6rem">
                            <p-tag
                                [value]="p.status === 'ACTIVE' ? 'Ativo' : 'Inativo'"
                                [severity]="p.status === 'ACTIVE' ? 'success' : 'secondary'"
                            />
                        </td>
                        <td style="min-width:8rem">{{ p.createdAt | date:'dd/MM/yyyy' }}</td>
                        <td style="min-width:5rem">
                            <p-button
                                label="Ver"
                                icon="pi pi-eye"
                                size="small"
                                [text]="true"
                                severity="secondary"
                                (onClick)="router.navigate(['/properties', p.id, 'edit'])"
                            />
                        </td>
                    </tr>
                </ng-template>
                <ng-template #emptymessage>
                    <tr>
                        <td colspan="6" class="text-center text-muted-color py-6">Nenhum imóvel cadastrado ainda.</td>
                    </tr>
                </ng-template>
            </p-table>
        </div>
    `
})
export class RecentSalesWidget {
    readonly router = inject(Router);
    readonly properties = input<RecentProperty[]>([]);
}
