package br.com.fabriciofaceroli.testimonial.application.port.in;

import java.util.List;
import java.util.UUID;

public interface ListApprovedTestimonialsPort {
    List<TestimonialView> list(UUID propertyId);
}
