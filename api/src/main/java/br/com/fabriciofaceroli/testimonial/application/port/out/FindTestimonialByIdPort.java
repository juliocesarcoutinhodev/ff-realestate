package br.com.fabriciofaceroli.testimonial.application.port.out;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;

import java.util.Optional;
import java.util.UUID;

public interface FindTestimonialByIdPort {
    Optional<Testimonial> findById(UUID id);
}
