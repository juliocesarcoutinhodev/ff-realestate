package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.domain.model.PhotoSummary;

import java.util.List;
import java.util.UUID;

public interface FindPropertyPhotosPort {
    List<PhotoSummary> findByPropertyId(UUID propertyId);
}
