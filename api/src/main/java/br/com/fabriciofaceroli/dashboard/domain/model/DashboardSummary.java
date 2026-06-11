package br.com.fabriciofaceroli.dashboard.domain.model;

import java.util.List;

public record DashboardSummary(
        long totalActiveProperties,
        long totalInactiveProperties,
        long totalCategories,
        long totalPendingTestimonials,
        List<RecentProperty> recentProperties,
        List<PendingTestimonial> pendingTestimonials
) {}
