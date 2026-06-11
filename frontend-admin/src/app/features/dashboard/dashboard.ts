import { Component, inject, signal, OnInit } from '@angular/core';
import { DashboardService } from './services/dashboard.service';
import { DashboardSummary } from '@/app/core/models';
import { StatsWidget } from './components/statswidget';
import { RecentSalesWidget } from './components/recentsaleswidget';
import { NotificationsWidget } from './components/notificationswidget';

@Component({
    selector: 'app-dashboard',
    imports: [StatsWidget, RecentSalesWidget, NotificationsWidget],
    template: `
        <div class="grid grid-cols-12 gap-8">
            <app-stats-widget class="contents" [summary]="summary()" />
            <div class="col-span-12 xl:col-span-6">
                <app-recent-sales-widget [properties]="summary()?.recentProperties ?? []" />
            </div>
            <div class="col-span-12 xl:col-span-6">
                <app-notifications-widget [testimonials]="summary()?.pendingTestimonials ?? []" />
            </div>
        </div>
    `
})
export class Dashboard implements OnInit {
    private readonly dashboardService = inject(DashboardService);
    readonly summary = signal<DashboardSummary | null>(null);

    ngOnInit(): void {
        this.dashboardService.getSummary().subscribe({
            next: (response) => this.summary.set(response.data)
        });
    }
}
