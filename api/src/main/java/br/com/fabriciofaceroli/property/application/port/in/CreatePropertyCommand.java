package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.DealType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePropertyCommand(
        String title,
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
        DealType dealType,
        Boolean featured,
        String externalUrl,
        UUID categoryId
) {}
