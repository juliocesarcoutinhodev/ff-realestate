package br.com.fabriciofaceroli.dashboard.adapter.in.web.mapper;

import br.com.fabriciofaceroli.dashboard.adapter.in.web.dto.DashboardSummaryResponse;
import br.com.fabriciofaceroli.dashboard.domain.model.DashboardSummary;
import br.com.fabriciofaceroli.dashboard.domain.model.PendingTestimonial;
import br.com.fabriciofaceroli.dashboard.domain.model.RecentProperty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DashboardWebMapper {

    DashboardSummaryResponse toResponse(DashboardSummary summary);

    DashboardSummaryResponse.RecentPropertyResponse toRecentPropertyResponse(RecentProperty property);

    DashboardSummaryResponse.PendingTestimonialResponse toPendingTestimonialResponse(PendingTestimonial testimonial);
}
