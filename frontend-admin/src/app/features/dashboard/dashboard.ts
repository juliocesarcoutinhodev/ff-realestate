import { Component, inject, signal, OnInit } from '@angular/core';
import { map } from 'rxjs';
import { DashboardService } from './services/dashboard.service';
import { DashboardSummary } from '@/app/core/models';
import { StatsWidget } from './components/statswidget';
import { RecentSalesWidget } from './components/recentsaleswidget';
import { NotificationsWidget, ReviewEvent } from './components/notificationswidget';

@Component({
    selector: 'app-dashboard',
    imports: [StatsWidget, RecentSalesWidget, NotificationsWidget],
    template: `
        <div class="grid grid-cols-12 gap-8">
            <app-stats-widget class="contents" [summary]="summary()" [loading]="loading()" />
            <div class="col-span-12 xl:col-span-6">
                <app-recent-sales-widget [properties]="summary()?.recentProperties ?? []" />
            </div>
            <div class="col-span-12 xl:col-span-6">
                <app-notifications-widget
                    [testimonials]="summary()?.pendingTestimonials ?? []"
                    (review)="onReview($event)"
                />
            </div>
        </div>
    `
})
export class Dashboard implements OnInit {
    private readonly dashboardService = inject(DashboardService);

    readonly loading = signal(true);
    readonly summary = signal<DashboardSummary | null>(null);

    ngOnInit(): void {
        this.dashboardService.getSummary()
            .pipe(map((response) => response.data))
            .subscribe({
                next: (data) => {
                    this.summary.set(data);
                    this.loading.set(false);
                }
            });
    }

    onReview(event: ReviewEvent): void {
        this.dashboardService.reviewTestimonial(event.id, event.status).subscribe({
            next: () => this.summary.update((s) => {
                if (!s) return s;
                const filtered = s.pendingTestimonials.filter((t) => t.id !== event.id);
                return { ...s, pendingTestimonials: filtered, totalPendingTestimonials: filtered.length };
            })
        });
    }
}
