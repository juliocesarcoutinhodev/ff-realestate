package br.com.fabriciofaceroli.dashboard.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecentProperty(
        UUID id,
        String title,
        String slug,
        BigDecimal price,
        String status,
        String dealType,
        LocalDateTime createdAt
) {}
