package br.com.fabriciofaceroli.testimonial.application.port.out;

import java.util.UUID;

public interface RemoveTestimonialPort {
    void deleteById(UUID id);
}
