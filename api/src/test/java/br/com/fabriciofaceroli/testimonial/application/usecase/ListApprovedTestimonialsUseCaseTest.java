package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.testimonial.application.port.out.FindApprovedTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertySummaryForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListApprovedTestimonialsUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private FindApprovedTestimonialsPort findApprovedTestimonialsPort;
    private FindPropertySummaryForTestimonialPort findPropertySummaryPort;
    private ListApprovedTestimonialsUseCase sut;

    @BeforeEach
    void setUp() {
        findApprovedTestimonialsPort = mock(FindApprovedTestimonialsPort.class);
        findPropertySummaryPort = mock(FindPropertySummaryForTestimonialPort.class);
        sut = new ListApprovedTestimonialsUseCase(findApprovedTestimonialsPort, findPropertySummaryPort);
    }

    @Test
    void list_shouldReturnEmptyList_whenNoApprovedTestimonialsExist() {
        when(findApprovedTestimonialsPort.findApproved(null)).thenReturn(List.of());

        var result = sut.list(null);

        assertThat(result).isEmpty();
        verify(findPropertySummaryPort, never()).findById(any());
    }

    @Test
    void list_shouldReturnViewsWithPropertyInfo_whenTestimonialsHavePropertyId() {
        var testimonial = approved(PROPERTY_ID);
        var summary = new PropertySummary(PROPERTY_ID, "Casa Nova", "casa-nova");
        when(findApprovedTestimonialsPort.findApproved(null)).thenReturn(List.of(testimonial));
        when(findPropertySummaryPort.findById(PROPERTY_ID)).thenReturn(Optional.of(summary));

        var result = sut.list(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).property()).isEqualTo(summary);
        assertThat(result.get(0).clientName()).isEqualTo(testimonial.clientName());
    }

    @Test
    void list_shouldReturnNullProperty_whenTestimonialHasNoPropertyId() {
        var testimonial = approved(null);
        when(findApprovedTestimonialsPort.findApproved(null)).thenReturn(List.of(testimonial));

        var result = sut.list(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).property()).isNull();
        verify(findPropertySummaryPort, never()).findById(any());
    }

    @Test
    void list_shouldReturnNullProperty_whenPropertyNoLongerExists() {
        var testimonial = approved(PROPERTY_ID);
        when(findApprovedTestimonialsPort.findApproved(null)).thenReturn(List.of(testimonial));
        when(findPropertySummaryPort.findById(PROPERTY_ID)).thenReturn(Optional.empty());

        var result = sut.list(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).property()).isNull();
    }

    @Test
    void list_shouldPassPropertyIdFilterToPort() {
        when(findApprovedTestimonialsPort.findApproved(PROPERTY_ID)).thenReturn(List.of());

        sut.list(PROPERTY_ID);

        verify(findApprovedTestimonialsPort).findApproved(PROPERTY_ID);
    }

    @Test
    void list_shouldMapAllFieldsCorrectly() {
        var now = Instant.now();
        var id = UUID.randomUUID();
        var testimonial = new Testimonial(id, "João Silva", "Ótimo atendimento.", 5,
                TestimonialStatus.APPROVED.name(), null, now);
        when(findApprovedTestimonialsPort.findApproved(null)).thenReturn(List.of(testimonial));

        var result = sut.list(null);

        var view = result.get(0);
        assertThat(view.id()).isEqualTo(id);
        assertThat(view.clientName()).isEqualTo("João Silva");
        assertThat(view.text()).isEqualTo("Ótimo atendimento.");
        assertThat(view.rating()).isEqualTo(5);
        assertThat(view.createdAt()).isEqualTo(now);
    }

    private Testimonial approved(UUID propertyId) {
        return new Testimonial(UUID.randomUUID(), "Cliente", "Texto.", 5,
                TestimonialStatus.APPROVED.name(), propertyId, Instant.now());
    }
}
