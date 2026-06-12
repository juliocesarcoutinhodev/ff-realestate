package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;

import java.util.UUID;

public interface GetAdminPropertyByIdPort {
    PropertyDetail getById(UUID id);
}
