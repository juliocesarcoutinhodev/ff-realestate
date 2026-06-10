package br.com.fabriciofaceroli.testimonial.application.port.in;

import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;

import java.time.Instant;
import java.util.UUID;

public record TestimonialView(
        UUID id,
        String clientName,
        String text,
        int rating,
        String status,
        PropertySummary property,
        Instant createdAt
) {}
