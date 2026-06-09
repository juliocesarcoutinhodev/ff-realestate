package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePropertyCommand(
        UUID id,
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
        UUID categoryId,
        PropertyStatus status
) {}
