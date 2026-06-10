package br.com.fabriciofaceroli.testimonial.application.port.in;

import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.springframework.data.domain.Pageable;

public record ListAllTestimonialsQuery(TestimonialStatus status, Pageable pageable) {}
