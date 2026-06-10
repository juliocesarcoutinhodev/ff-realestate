package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.in.SubmitTestimonialCommand;
import br.com.fabriciofaceroli.testimonial.application.port.in.SubmitTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertyForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.SaveTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmitTestimonialUseCase implements SubmitTestimonialPort {

    private final FindPropertyForTestimonialPort findPropertyForTestimonialPort;
    private final SaveTestimonialPort saveTestimonialPort;

    public SubmitTestimonialUseCase(FindPropertyForTestimonialPort findPropertyForTestimonialPort,
                                    SaveTestimonialPort saveTestimonialPort) {
        this.findPropertyForTestimonialPort = findPropertyForTestimonialPort;
        this.saveTestimonialPort = saveTestimonialPort;
    }

    @Override
    @Transactional
    public Testimonial submit(SubmitTestimonialCommand command) {
        if (command.propertyId() != null && !findPropertyForTestimonialPort.existsById(command.propertyId())) {
            throw new ResourceNotFoundException("Imóvel não encontrado.");
        }

        var testimonial = new Testimonial(
                null,
                command.clientName(),
                command.text(),
                command.rating(),
                TestimonialStatus.PENDING.name(),
                command.propertyId(),
                null
        );

        return saveTestimonialPort.save(testimonial);
    }
}
