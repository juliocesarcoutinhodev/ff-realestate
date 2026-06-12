package br.com.fabriciofaceroli.property.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Property(
        UUID id,
        String title,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal area,
        Integer bedrooms,
        Integer suites,
        Integer bathrooms,
        Integer parkingSpots,
        String address,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String dealType,
        boolean featured,
        String status,
        String externalUrl,
        UUID categoryId,
        String coverPhoto
) {}
