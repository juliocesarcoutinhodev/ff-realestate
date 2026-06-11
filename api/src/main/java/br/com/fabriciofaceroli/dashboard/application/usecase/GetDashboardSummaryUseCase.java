package br.com.fabriciofaceroli.dashboard.application.usecase;

import br.com.fabriciofaceroli.dashboard.application.port.in.GetDashboardSummaryPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountActivePropertiesPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountCategoriesPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountInactivePropertiesPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountPendingTestimonialsPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.FindPendingTestimonialsPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.FindRecentPropertiesPort;
import br.com.fabriciofaceroli.dashboard.domain.model.DashboardSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetDashboardSummaryUseCase implements GetDashboardSummaryPort {

    private static final int RECENT_ITEMS_LIMIT = 5;

    private final CountActivePropertiesPort countActivePropertiesPort;
    private final CountInactivePropertiesPort countInactivePropertiesPort;
    private final CountCategoriesPort countCategoriesPort;
    private final CountPendingTestimonialsPort countPendingTestimonialsPort;
    private final FindRecentPropertiesPort findRecentPropertiesPort;
    private final FindPendingTestimonialsPort findPendingTestimonialsPort;

    public GetDashboardSummaryUseCase(
            CountActivePropertiesPort countActivePropertiesPort,
            CountInactivePropertiesPort countInactivePropertiesPort,
            CountCategoriesPort countCategoriesPort,
            CountPendingTestimonialsPort countPendingTestimonialsPort,
            FindRecentPropertiesPort findRecentPropertiesPort,
            FindPendingTestimonialsPort findPendingTestimonialsPort) {
        this.countActivePropertiesPort = countActivePropertiesPort;
        this.countInactivePropertiesPort = countInactivePropertiesPort;
        this.countCategoriesPort = countCategoriesPort;
        this.countPendingTestimonialsPort = countPendingTestimonialsPort;
        this.findRecentPropertiesPort = findRecentPropertiesPort;
        this.findPendingTestimonialsPort = findPendingTestimonialsPort;
    }

    @Override
    public DashboardSummary getSummary() {
        return new DashboardSummary(
                countActivePropertiesPort.countActive(),
                countInactivePropertiesPort.countInactive(),
                countCategoriesPort.countCategories(),
                countPendingTestimonialsPort.countPendingTestimonials(),
                findRecentPropertiesPort.findRecent(RECENT_ITEMS_LIMIT),
                findPendingTestimonialsPort.findPending(RECENT_ITEMS_LIMIT)
        );
    }
}
