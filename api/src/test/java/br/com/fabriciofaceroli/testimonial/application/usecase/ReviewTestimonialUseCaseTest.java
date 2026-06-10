package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ConflictException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.in.ReviewTestimonialCommand;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindTestimonialByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.UpdateTestimonialStatusPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewTestimonialUseCaseTest {

    private static final UUID TESTIMONIAL_ID = UUID.randomUUID();

    private FindTestimonialByIdPort findTestimonialByIdPort;
    private UpdateTestimonialStatusPort updateTestimonialStatusPort;
    private ReviewTestimonialUseCase sut;

    @BeforeEach
    void setUp() {
        findTestimonialByIdPort = mock(FindTestimonialByIdPort.class);
        updateTestimonialStatusPort = mock(UpdateTestimonialStatusPort.class);
        sut = new ReviewTestimonialUseCase(findTestimonialByIdPort, updateTestimonialStatusPort);
    }

    @Test
    void review_shouldApproveTestimonial_whenStatusIsPending() {
        var pending = testimonial(TestimonialStatus.PENDING);
        var approved = testimonial(TestimonialStatus.APPROVED);
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.of(pending));
        when(updateTestimonialStatusPort.updateStatus(TESTIMONIAL_ID, TestimonialStatus.APPROVED.name())).thenReturn(approved);

        var result = sut.review(new ReviewTestimonialCommand(TESTIMONIAL_ID, TestimonialStatus.APPROVED.name()));

        assertThat(result.status()).isEqualTo(TestimonialStatus.APPROVED.name());
        verify(updateTestimonialStatusPort).updateStatus(TESTIMONIAL_ID, TestimonialStatus.APPROVED.name());
    }

    @Test
    void review_shouldRejectTestimonial_whenStatusIsPending() {
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.of(testimonial(TestimonialStatus.PENDING)));
        when(updateTestimonialStatusPort.updateStatus(eq(TESTIMONIAL_ID), eq(TestimonialStatus.REJECTED.name())))
                .thenReturn(testimonial(TestimonialStatus.REJECTED));

        var result = sut.review(new ReviewTestimonialCommand(TESTIMONIAL_ID, TestimonialStatus.REJECTED.name()));

        assertThat(result.status()).isEqualTo(TestimonialStatus.REJECTED.name());
    }

    @Test
    void review_shouldThrowNotFound_whenTestimonialDoesNotExist() {
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.review(new ReviewTestimonialCommand(TESTIMONIAL_ID, TestimonialStatus.APPROVED.name())))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Depoimento não encontrado.");

        verify(updateTestimonialStatusPort, never()).updateStatus(any(), any());
    }

    @Test
    void review_shouldThrowConflict_whenTestimonialIsAlreadyApproved() {
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.of(testimonial(TestimonialStatus.APPROVED)));

        assertThatThrownBy(() -> sut.review(new ReviewTestimonialCommand(TESTIMONIAL_ID, TestimonialStatus.REJECTED.name())))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Este depoimento já foi revisado.");

        verify(updateTestimonialStatusPort, never()).updateStatus(any(), any());
    }

    @Test
    void review_shouldThrowConflict_whenTestimonialIsAlreadyRejected() {
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.of(testimonial(TestimonialStatus.REJECTED)));

        assertThatThrownBy(() -> sut.review(new ReviewTestimonialCommand(TESTIMONIAL_ID, TestimonialStatus.APPROVED.name())))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Este depoimento já foi revisado.");

        verify(updateTestimonialStatusPort, never()).updateStatus(any(), any());
    }

    @Test
    void review_shouldReturnResultFromUpdatePort() {
        var updated = testimonial(TestimonialStatus.APPROVED);
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.of(testimonial(TestimonialStatus.PENDING)));
        when(updateTestimonialStatusPort.updateStatus(any(), any())).thenReturn(updated);

        var result = sut.review(new ReviewTestimonialCommand(TESTIMONIAL_ID, TestimonialStatus.APPROVED.name()));

        assertThat(result).isEqualTo(updated);
    }

    private Testimonial testimonial(TestimonialStatus status) {
        return new Testimonial(TESTIMONIAL_ID, "João Silva", "Ótimo atendimento.", 5,
                status.name(), null, Instant.now());
    }
}
