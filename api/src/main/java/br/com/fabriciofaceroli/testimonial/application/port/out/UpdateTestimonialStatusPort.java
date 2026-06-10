package br.com.fabriciofaceroli.testimonial.application.port.out;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;

import java.util.UUID;

public interface UpdateTestimonialStatusPort {
    Testimonial updateStatus(UUID id, String newStatus);
}
