package br.com.fabriciofaceroli.dashboard.application.port.in;

import br.com.fabriciofaceroli.dashboard.domain.model.DashboardSummary;

public interface GetDashboardSummaryPort {
    DashboardSummary getSummary();
}
