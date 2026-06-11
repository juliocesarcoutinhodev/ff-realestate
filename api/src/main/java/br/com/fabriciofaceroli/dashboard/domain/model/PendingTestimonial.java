package br.com.fabriciofaceroli.dashboard.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PendingTestimonial(
        UUID id,
        String clientName,
        String text,
        Integer rating,
        LocalDateTime createdAt
) {}
