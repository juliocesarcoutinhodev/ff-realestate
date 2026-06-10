package br.com.fabriciofaceroli.testimonial.application.port.in;

import java.util.UUID;

public record SubmitTestimonialCommand(
        String clientName,
        String text,
        int rating,
        UUID propertyId
) {}
