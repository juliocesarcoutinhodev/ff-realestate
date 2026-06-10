package br.com.fabriciofaceroli.testimonial.application.port.out;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;

public interface SaveTestimonialPort {
    Testimonial save(Testimonial testimonial);
}
