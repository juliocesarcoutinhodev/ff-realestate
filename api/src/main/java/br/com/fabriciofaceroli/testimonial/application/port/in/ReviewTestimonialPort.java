package br.com.fabriciofaceroli.testimonial.application.port.in;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;

public interface ReviewTestimonialPort {
    Testimonial review(ReviewTestimonialCommand command);
}
