package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;

import java.util.Optional;
import java.util.UUID;

public interface FindPropertyDetailByIdPort {
    Optional<PropertyDetail> findDetailById(UUID id);
}
