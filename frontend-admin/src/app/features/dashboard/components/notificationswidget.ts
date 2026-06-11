import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { PendingTestimonial } from '@/app/core/models';

@Component({
    standalone: true,
    selector: 'app-notifications-widget',
    imports: [DatePipe, RouterLink, ButtonModule],
    template: `
        <div class="card">
            <div class="flex items-center justify-between mb-6">
                <span class="font-semibold text-xl">Depoimentos Pendentes</span>
                <a routerLink="/testimonials" class="text-primary text-sm font-medium hover:underline cursor-pointer">Gerenciar</a>
            </div>

            @if (testimonials().length === 0) {
                <div class="flex flex-col items-center justify-center py-8 text-muted-color gap-2">
                    <i class="pi pi-check-circle text-3xl text-green-400"></i>
                    <span class="text-sm">Nenhum depoimento aguardando aprovação.</span>
                </div>
            } @else {
                <ul class="p-0 m-0 list-none">
                    @for (t of testimonials(); track t.id) {
                        <li class="flex items-start py-3 border-b border-surface last:border-b-0">
                            <div class="w-11 h-11 flex items-center justify-center bg-yellow-100 dark:bg-yellow-400/10 rounded-full mr-4 shrink-0 mt-0.5">
                                <i class="pi pi-star-fill text-yellow-500"></i>
                            </div>
                            <div class="flex-1 min-w-0">
                                <div class="flex items-center justify-between gap-2 mb-1">
                                    <span class="font-medium text-surface-900 dark:text-surface-0 text-sm truncate">{{ t.clientName }}</span>
                                    <span class="text-muted-color text-xs shrink-0">{{ t.createdAt | date:'dd/MM/yyyy' }}</span>
                                </div>
                                <p class="text-surface-600 dark:text-surface-300 text-sm m-0 mb-1 line-clamp-2">{{ t.text }}</p>
                                <div class="flex gap-0.5">
                                    @for (s of [1, 2, 3, 4, 5]; track s) {
                                        <i
                                            class="pi text-xs"
                                            [class.pi-star-fill]="s <= t.rating"
                                            [class.pi-star]="s > t.rating"
                                            [class.text-yellow-400]="s <= t.rating"
                                            [class.text-surface-300]="s > t.rating"
                                        ></i>
                                    }
                                </div>
                            </div>
                        </li>
                    }
                </ul>
            }
        </div>
    `
})
export class NotificationsWidget {
    readonly testimonials = input<PendingTestimonial[]>([]);
}
