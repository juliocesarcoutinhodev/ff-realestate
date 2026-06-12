import { Component, computed, input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';
import { DashboardSummary } from '@/app/core/models';

@Component({
    standalone: true,
    selector: 'app-stats-widget',
    imports: [SkeletonModule],
    template: `
        @if (loading()) {
            @for (_ of [1, 2, 3, 4]; track _) {
                <div class="col-span-12 lg:col-span-6 xl:col-span-3">
                    <div class="card mb-0">
                        <div class="flex justify-between mb-4">
                            <div class="flex-1">
                                <p-skeleton width="8rem" height="1rem" styleClass="mb-4" />
                                <p-skeleton width="4rem" height="1.75rem" />
                            </div>
                            <p-skeleton shape="circle" size="2.5rem" />
                        </div>
                        <p-skeleton width="10rem" height="0.75rem" />
                    </div>
                </div>
            }
        } @else {
            <div class="col-span-12 lg:col-span-6 xl:col-span-3">
                <div class="card mb-0">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">Imóveis Ativos</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">
                                {{ summary()?.totalActiveProperties ?? 0 }}
                            </div>
                        </div>
                        <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border" style="width:2.5rem;height:2.5rem">
                            <i class="pi pi-home text-blue-500 text-xl!"></i>
                        </div>
                    </div>
                    <span class="text-muted-color text-sm">Publicados e visíveis no site</span>
                </div>
            </div>

            <div class="col-span-12 lg:col-span-6 xl:col-span-3">
                <div class="card mb-0">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">Imóveis Inativos</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">
                                {{ summary()?.totalInactiveProperties ?? 0 }}
                            </div>
                        </div>
                        <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border" style="width:2.5rem;height:2.5rem">
                            <i class="pi pi-ban text-orange-500 text-xl!"></i>
                        </div>
                    </div>
                    <span class="text-muted-color text-sm">Ocultos do catálogo público</span>
                </div>
            </div>

            <div class="col-span-12 lg:col-span-6 xl:col-span-3">
                <div class="card mb-0">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">Categorias</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">
                                {{ summary()?.totalCategories ?? 0 }}
                            </div>
                        </div>
                        <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border" style="width:2.5rem;height:2.5rem">
                            <i class="pi pi-tags text-cyan-500 text-xl!"></i>
                        </div>
                    </div>
                    <span class="text-muted-color text-sm">Tipos de imóvel cadastrados</span>
                </div>
            </div>

            <div class="col-span-12 lg:col-span-6 xl:col-span-3">
                <div class="card mb-0 transition-all" [class.ring-2]="hasPending()" [class.ring-yellow-300]="hasPending()">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">Depoimentos Pendentes</span>
                            <div class="font-medium text-xl" [class.text-yellow-500]="hasPending()" [class.text-surface-900]="!hasPending()">
                                {{ summary()?.totalPendingTestimonials ?? 0 }}
                            </div>
                        </div>
                        <div class="flex items-center justify-center rounded-border" [class.bg-yellow-100]="hasPending()" [class.bg-purple-100]="!hasPending()" style="width:2.5rem;height:2.5rem">
                            <i class="pi pi-clock text-xl!" [class.text-yellow-500]="hasPending()" [class.text-purple-500]="!hasPending()"></i>
                        </div>
                    </div>
                    <span class="text-muted-color text-sm">Aguardando aprovação</span>
                </div>
            </div>
        }
    `
})
export class StatsWidget {
    readonly summary = input<DashboardSummary | null>(null);
    readonly loading = input<boolean>(false);
    readonly hasPending = computed(() => (this.summary()?.totalPendingTestimonials ?? 0) > 0);
}
