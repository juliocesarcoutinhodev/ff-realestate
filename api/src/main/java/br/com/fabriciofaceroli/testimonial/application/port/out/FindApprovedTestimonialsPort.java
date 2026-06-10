package br.com.fabriciofaceroli.testimonial.application.port.out;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;

import java.util.List;
import java.util.UUID;

public interface FindApprovedTestimonialsPort {
    List<Testimonial> findApproved(UUID propertyId);
}
