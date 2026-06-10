package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.testimonial.application.port.in.ListAllTestimonialsQuery;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindAllTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertySummaryForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
class ListAllTestimonialsUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private FindAllTestimonialsPort findAllTestimonialsPort;
    private FindPropertySummaryForTestimonialPort findPropertySummaryPort;
    private ListAllTestimonialsUseCase sut;

    @BeforeEach
    void setUp() {
        findAllTestimonialsPort = mock(FindAllTestimonialsPort.class);
        findPropertySummaryPort = mock(FindPropertySummaryForTestimonialPort.class);
        sut = new ListAllTestimonialsUseCase(findAllTestimonialsPort, findPropertySummaryPort);
    }

    @Test
    void listAll_shouldReturnEmptyPage_whenNoTestimonialsExist() {
        var pageable = PageRequest.of(0, 12);
        when(findAllTestimonialsPort.findAll(null, pageable)).thenReturn(new PageImpl<>(List.of()));

        var result = sut.listAll(new ListAllTestimonialsQuery(null, pageable));

        assertThat(result.getContent()).isEmpty();
        verify(findPropertySummaryPort, never()).findById(any());
    }

    @Test
    void listAll_shouldReturnAllStatuses_whenStatusFilterIsNull() {
        var pageable = PageRequest.of(0, 12);
        var testimonials = List.of(
                testimonial(TestimonialStatus.PENDING, null),
                testimonial(TestimonialStatus.APPROVED, null),
                testimonial(TestimonialStatus.REJECTED, null)
        );
        when(findAllTestimonialsPort.findAll(null, pageable)).thenReturn(new PageImpl<>(testimonials));

        var result = sut.listAll(new ListAllTestimonialsQuery(null, pageable));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting("status")
                .containsExactly(TestimonialStatus.PENDING.name(), TestimonialStatus.APPROVED.name(), TestimonialStatus.REJECTED.name());
    }

    @Test
    void listAll_shouldPassStatusFilterToPort() {
        var pageable = PageRequest.of(0, 12);
        when(findAllTestimonialsPort.findAll(TestimonialStatus.PENDING, pageable)).thenReturn(new PageImpl<>(List.of()));

        sut.listAll(new ListAllTestimonialsQuery(TestimonialStatus.PENDING, pageable));

        verify(findAllTestimonialsPort).findAll(TestimonialStatus.PENDING, pageable);
    }

    @Test
    void listAll_shouldEnrichWithPropertyInfo_whenTestimonialHasPropertyId() {
        var pageable = PageRequest.of(0, 12);
        var t = testimonial(TestimonialStatus.APPROVED, PROPERTY_ID);
        var summary = new PropertySummary(PROPERTY_ID, "Casa Nova", "casa-nova");
        when(findAllTestimonialsPort.findAll(null, pageable)).thenReturn(new PageImpl<>(List.of(t)));
        when(findPropertySummaryPort.findById(PROPERTY_ID)).thenReturn(Optional.of(summary));

        var result = sut.listAll(new ListAllTestimonialsQuery(null, pageable));

        assertThat(result.getContent().get(0).property()).isEqualTo(summary);
    }

    @Test
    void listAll_shouldReturnNullProperty_whenTestimonialHasNoPropertyId() {
        var pageable = PageRequest.of(0, 12);
        when(findAllTestimonialsPort.findAll(null, pageable)).thenReturn(new PageImpl<>(List.of(testimonial(TestimonialStatus.PENDING, null))));

        var result = sut.listAll(new ListAllTestimonialsQuery(null, pageable));

        assertThat(result.getContent().get(0).property()).isNull();
        verify(findPropertySummaryPort, never()).findById(any());
    }

    @Test
    void listAll_shouldReturnNullProperty_whenPropertyNoLongerExists() {
        var pageable = PageRequest.of(0, 12);
        when(findAllTestimonialsPort.findAll(null, pageable)).thenReturn(new PageImpl<>(List.of(testimonial(TestimonialStatus.APPROVED, PROPERTY_ID))));
        when(findPropertySummaryPort.findById(PROPERTY_ID)).thenReturn(Optional.empty());

        var result = sut.listAll(new ListAllTestimonialsQuery(null, pageable));

        assertThat(result.getContent().get(0).property()).isNull();
    }

    private Testimonial testimonial(TestimonialStatus status, UUID propertyId) {
        return new Testimonial(UUID.randomUUID(), "Cliente", "Texto.", 5,
                status.name(), propertyId, Instant.now());
    }
}
