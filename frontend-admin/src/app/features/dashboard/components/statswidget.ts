import { Component, input } from '@angular/core';
import { DashboardSummary } from '@/app/core/models';

@Component({
    standalone: true,
    selector: 'app-stats-widget',
    template: `
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
                        <i class="pi pi-building text-blue-500 text-xl!"></i>
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
                        <i class="pi pi-eye-slash text-orange-500 text-xl!"></i>
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
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">Depoimentos Pendentes</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">
                            {{ summary()?.totalPendingTestimonials ?? 0 }}
                        </div>
                    </div>
                    <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border" style="width:2.5rem;height:2.5rem">
                        <i class="pi pi-comment text-purple-500 text-xl!"></i>
                    </div>
                </div>
                <span class="text-muted-color text-sm">Aguardando aprovação</span>
            </div>
        </div>
    `
})
export class StatsWidget {
    readonly summary = input<DashboardSummary | null>(null);
}
