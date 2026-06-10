package br.com.fabriciofaceroli.testimonial.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Testimonial(
        UUID id,
        String clientName,
        String text,
        int rating,
        String status,
        UUID propertyId,
        Instant createdAt
) {}
