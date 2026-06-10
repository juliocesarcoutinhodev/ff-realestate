package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record ListAllPropertiesQuery(
        UUID categoryId,
        DealType dealType,
        Boolean featured,
        String city,
        PropertyStatus status,
        Pageable pageable
) {}
