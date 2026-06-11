package br.com.fabriciofaceroli.dashboard.application.usecase;

import br.com.fabriciofaceroli.dashboard.domain.model.DashboardSummary;
import br.com.fabriciofaceroli.dashboard.domain.model.PendingTestimonial;
import br.com.fabriciofaceroli.dashboard.domain.model.RecentProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDashboardSummaryUseCaseTest {

    @Mock
    private br.com.fabriciofaceroli.dashboard.application.port.out.CountActivePropertiesPort countActivePropertiesPort;
    @Mock
    private br.com.fabriciofaceroli.dashboard.application.port.out.CountInactivePropertiesPort countInactivePropertiesPort;
    @Mock
    private br.com.fabriciofaceroli.dashboard.application.port.out.CountCategoriesPort countCategoriesPort;
    @Mock
    private br.com.fabriciofaceroli.dashboard.application.port.out.CountPendingTestimonialsPort countPendingTestimonialsPort;
    @Mock
    private br.com.fabriciofaceroli.dashboard.application.port.out.FindRecentPropertiesPort findRecentPropertiesPort;
    @Mock
    private br.com.fabriciofaceroli.dashboard.application.port.out.FindPendingTestimonialsPort findPendingTestimonialsPort;

    private GetDashboardSummaryUseCase sut;

    @BeforeEach
    void setUp() {
        sut = new GetDashboardSummaryUseCase(
                countActivePropertiesPort,
                countInactivePropertiesPort,
                countCategoriesPort,
                countPendingTestimonialsPort,
                findRecentPropertiesPort,
                findPendingTestimonialsPort
        );
    }

    @Test
    void getSummary_returnsAssembledDashboard_whenAllPortsReturnData() {
        var recentProperty = recentProperty();
        var pendingTestimonial = pendingTestimonial();

        when(countActivePropertiesPort.countActive()).thenReturn(10L);
        when(countInactivePropertiesPort.countInactive()).thenReturn(3L);
        when(countCategoriesPort.countCategories()).thenReturn(6L);
        when(countPendingTestimonialsPort.countPendingTestimonials()).thenReturn(2L);
        when(findRecentPropertiesPort.findRecent(eq(5))).thenReturn(List.of(recentProperty));
        when(findPendingTestimonialsPort.findPending(eq(5))).thenReturn(List.of(pendingTestimonial));

        DashboardSummary result = sut.getSummary();

        assertThat(result.totalActiveProperties()).isEqualTo(10L);
        assertThat(result.totalInactiveProperties()).isEqualTo(3L);
        assertThat(result.totalCategories()).isEqualTo(6L);
        assertThat(result.totalPendingTestimonials()).isEqualTo(2L);
        assertThat(result.recentProperties()).hasSize(1);
        assertThat(result.recentProperties().get(0).title()).isEqualTo("Casa Alvorada");
        assertThat(result.pendingTestimonials()).hasSize(1);
        assertThat(result.pendingTestimonials().get(0).clientName()).isEqualTo("Maria Souza");
    }

    @Test
    void getSummary_returnsEmptyLists_whenNoDataExists() {
        when(countActivePropertiesPort.countActive()).thenReturn(0L);
        when(countInactivePropertiesPort.countInactive()).thenReturn(0L);
        when(countCategoriesPort.countCategories()).thenReturn(0L);
        when(countPendingTestimonialsPort.countPendingTestimonials()).thenReturn(0L);
        when(findRecentPropertiesPort.findRecent(eq(5))).thenReturn(List.of());
        when(findPendingTestimonialsPort.findPending(eq(5))).thenReturn(List.of());

        DashboardSummary result = sut.getSummary();

        assertThat(result.totalActiveProperties()).isZero();
        assertThat(result.totalInactiveProperties()).isZero();
        assertThat(result.totalCategories()).isZero();
        assertThat(result.totalPendingTestimonials()).isZero();
        assertThat(result.recentProperties()).isEmpty();
        assertThat(result.pendingTestimonials()).isEmpty();
    }

    @Test
    void getSummary_passesLimitOf5_toRecentAndPendingPorts() {
        when(countActivePropertiesPort.countActive()).thenReturn(0L);
        when(countInactivePropertiesPort.countInactive()).thenReturn(0L);
        when(countCategoriesPort.countCategories()).thenReturn(0L);
        when(countPendingTestimonialsPort.countPendingTestimonials()).thenReturn(0L);
        when(findRecentPropertiesPort.findRecent(eq(5))).thenReturn(List.of());
        when(findPendingTestimonialsPort.findPending(eq(5))).thenReturn(List.of());

        sut.getSummary();

        org.mockito.Mockito.verify(findRecentPropertiesPort).findRecent(5);
        org.mockito.Mockito.verify(findPendingTestimonialsPort).findPending(5);
    }

    private RecentProperty recentProperty() {
        return new RecentProperty(
                UUID.randomUUID(),
                "Casa Alvorada",
                "casa-alvorada",
                new BigDecimal("320000.00"),
                "ACTIVE",
                "SALE",
                LocalDateTime.now()
        );
    }

    private PendingTestimonial pendingTestimonial() {
        return new PendingTestimonial(
                UUID.randomUUID(),
                "Maria Souza",
                "Ótimo atendimento!",
                5,
                LocalDateTime.now()
        );
    }
}
