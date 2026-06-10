package br.com.fabriciofaceroli.testimonial.application.port.out;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllTestimonialsPort {
    Page<Testimonial> findAll(TestimonialStatus status, Pageable pageable);
}
