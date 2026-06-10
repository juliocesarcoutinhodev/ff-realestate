package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindTestimonialByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.RemoveTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTestimonialUseCaseTest {

    private static final UUID TESTIMONIAL_ID = UUID.randomUUID();

    private FindTestimonialByIdPort findTestimonialByIdPort;
    private RemoveTestimonialPort removeTestimonialPort;
    private DeleteTestimonialUseCase sut;

    @BeforeEach
    void setUp() {
        findTestimonialByIdPort = mock(FindTestimonialByIdPort.class);
        removeTestimonialPort = mock(RemoveTestimonialPort.class);
        sut = new DeleteTestimonialUseCase(findTestimonialByIdPort, removeTestimonialPort);
    }

    @Test
    void delete_shouldRemoveTestimonial_whenIdExists() {
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.of(pendingTestimonial()));

        sut.delete(TESTIMONIAL_ID);

        verify(removeTestimonialPort).deleteById(TESTIMONIAL_ID);
    }

    @Test
    void delete_shouldThrowNotFound_whenTestimonialDoesNotExist() {
        when(findTestimonialByIdPort.findById(TESTIMONIAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.delete(TESTIMONIAL_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Depoimento não encontrado.");

        verify(removeTestimonialPort, never()).deleteById(any());
    }

    private Testimonial pendingTestimonial() {
        return new Testimonial(TESTIMONIAL_ID, "João Silva", "Ótimo atendimento.", 5,
                TestimonialStatus.PENDING.name(), null, Instant.now());
    }
}
