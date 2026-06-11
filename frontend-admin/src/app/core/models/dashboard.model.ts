export interface DashboardSummary {
    totalActiveProperties: number;
    totalInactiveProperties: number;
    totalCategories: number;
    totalPendingTestimonials: number;
    recentProperties: RecentProperty[];
    pendingTestimonials: PendingTestimonial[];
}

export interface RecentProperty {
    id: string;
    title: string;
    slug: string;
    price: number;
    status: 'ACTIVE' | 'INACTIVE';
    dealType: 'SALE' | 'RENT';
    createdAt: string;
}

export interface PendingTestimonial {
    id: string;
    clientName: string;
    text: string;
    rating: number;
    createdAt: string;
}
