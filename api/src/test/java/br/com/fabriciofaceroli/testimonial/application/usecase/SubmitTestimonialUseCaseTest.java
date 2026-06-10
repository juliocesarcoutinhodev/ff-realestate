package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.in.SubmitTestimonialCommand;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertyForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.SaveTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitTestimonialUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private FindPropertyForTestimonialPort findPropertyForTestimonialPort;
    private SaveTestimonialPort saveTestimonialPort;
    private SubmitTestimonialUseCase sut;

    @BeforeEach
    void setUp() {
        findPropertyForTestimonialPort = mock(FindPropertyForTestimonialPort.class);
        saveTestimonialPort = mock(SaveTestimonialPort.class);
        sut = new SubmitTestimonialUseCase(findPropertyForTestimonialPort, saveTestimonialPort);
    }

    @Test
    void submit_shouldSaveTestimonialWithPendingStatus_whenCommandIsValid() {
        when(saveTestimonialPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new SubmitTestimonialCommand("João Silva", "Ótimo atendimento.", 5, null);
        var result = sut.submit(command);

        assertThat(result.status()).isEqualTo(TestimonialStatus.PENDING.name());
        assertThat(result.clientName()).isEqualTo("João Silva");
        assertThat(result.rating()).isEqualTo(5);
    }

    @Test
    void submit_shouldSetNullId_soRepositoryGeneratesIt() {
        when(saveTestimonialPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.submit(new SubmitTestimonialCommand("Maria", "Excelente.", 4, null));

        ArgumentCaptor<Testimonial> captor = ArgumentCaptor.forClass(Testimonial.class);
        verify(saveTestimonialPort).save(captor.capture());
        assertThat(captor.getValue().id()).isNull();
    }

    @Test
    void submit_shouldValidatePropertyExists_whenPropertyIdIsProvided() {
        when(findPropertyForTestimonialPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(saveTestimonialPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new SubmitTestimonialCommand("Carlos", "Muito bom.", 5, PROPERTY_ID);
        var result = sut.submit(command);

        assertThat(result.propertyId()).isEqualTo(PROPERTY_ID);
        verify(findPropertyForTestimonialPort).existsById(PROPERTY_ID);
    }

    @Test
    void submit_shouldNotCheckProperty_whenPropertyIdIsNull() {
        when(saveTestimonialPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.submit(new SubmitTestimonialCommand("Ana", "Muito satisfeita.", 5, null));

        verify(findPropertyForTestimonialPort, never()).existsById(any());
    }

    @Test
    void submit_shouldThrowNotFound_whenPropertyIdIsInvalid() {
        when(findPropertyForTestimonialPort.existsById(PROPERTY_ID)).thenReturn(false);

        var command = new SubmitTestimonialCommand("Pedro", "Ótimo.", 4, PROPERTY_ID);

        assertThatThrownBy(() -> sut.submit(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(saveTestimonialPort, never()).save(any());
    }

    @Test
    void submit_shouldReturnTestimonialFromSavePort() {
        var saved = new Testimonial(UUID.randomUUID(), "João", "Texto.", 5,
                TestimonialStatus.PENDING.name(), null, Instant.now());
        when(saveTestimonialPort.save(any())).thenReturn(saved);

        var result = sut.submit(new SubmitTestimonialCommand("João", "Texto.", 5, null));

        assertThat(result).isEqualTo(saved);
    }
}
