package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.DealType;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record ListPropertiesQuery(
        UUID categoryId,
        DealType dealType,
        Boolean featured,
        String city,
        Pageable pageable
) {}
