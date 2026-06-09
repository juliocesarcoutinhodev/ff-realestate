package br.com.fabriciofaceroli.property.application.port.out;

import java.util.UUID;

public interface DeletePropertyPhotosPort {
    void deleteByPropertyId(UUID propertyId);
}
