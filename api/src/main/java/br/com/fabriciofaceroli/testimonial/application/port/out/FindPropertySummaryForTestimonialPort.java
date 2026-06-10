package br.com.fabriciofaceroli.testimonial.application.port.out;

import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;

import java.util.Optional;
import java.util.UUID;

public interface FindPropertySummaryForTestimonialPort {
    Optional<PropertySummary> findById(UUID propertyId);
}
