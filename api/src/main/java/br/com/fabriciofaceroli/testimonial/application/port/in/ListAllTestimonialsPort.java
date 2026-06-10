package br.com.fabriciofaceroli.testimonial.application.port.in;

import org.springframework.data.domain.Page;

public interface ListAllTestimonialsPort {
    Page<TestimonialView> listAll(ListAllTestimonialsQuery query);
}
