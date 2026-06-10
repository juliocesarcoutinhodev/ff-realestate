package br.com.fabriciofaceroli.testimonial.application.port.in;

import java.util.UUID;

public interface DeleteTestimonialPort {
    void delete(UUID id);
}
