package br.com.fabriciofaceroli.photo.application.port.out;

import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;

import java.util.List;
import java.util.UUID;

public interface FindPhotosByPropertyPort {
    int countByPropertyId(UUID propertyId);
    boolean existsCoverByPropertyId(UUID propertyId);
    List<PropertyPhoto> findByPropertyId(UUID propertyId);
}
