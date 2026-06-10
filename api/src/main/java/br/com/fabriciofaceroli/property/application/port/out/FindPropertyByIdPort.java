package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.domain.model.Property;

import java.util.Optional;
import java.util.UUID;

public interface FindPropertyByIdPort {
    Optional<Property> findById(UUID id);
}
