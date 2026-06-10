package br.com.fabriciofaceroli.testimonial.application.port.out;

import java.util.UUID;

public interface FindPropertyForTestimonialPort {
    boolean existsById(UUID propertyId);
}
