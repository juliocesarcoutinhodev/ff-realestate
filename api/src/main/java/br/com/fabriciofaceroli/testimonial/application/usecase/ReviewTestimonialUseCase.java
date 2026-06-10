package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ConflictException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.in.ReviewTestimonialCommand;
import br.com.fabriciofaceroli.testimonial.application.port.in.ReviewTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindTestimonialByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.UpdateTestimonialStatusPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewTestimonialUseCase implements ReviewTestimonialPort {

    private final FindTestimonialByIdPort findTestimonialByIdPort;
    private final UpdateTestimonialStatusPort updateTestimonialStatusPort;

    public ReviewTestimonialUseCase(FindTestimonialByIdPort findTestimonialByIdPort,
                                    UpdateTestimonialStatusPort updateTestimonialStatusPort) {
        this.findTestimonialByIdPort = findTestimonialByIdPort;
        this.updateTestimonialStatusPort = updateTestimonialStatusPort;
    }

    @Override
    public Testimonial review(ReviewTestimonialCommand command) {
        var testimonial = findTestimonialByIdPort.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Depoimento não encontrado."));

        if (!TestimonialStatus.PENDING.name().equals(testimonial.status())) {
            throw new ConflictException("Este depoimento já foi revisado.");
        }

        return updateTestimonialStatusPort.updateStatus(command.id(), command.newStatus());
    }
}
