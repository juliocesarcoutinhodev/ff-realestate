package br.com.fabriciofaceroli.photo.application.port.out;

import java.util.UUID;

public interface FindPhotosByPropertyPort {
    int countByPropertyId(UUID propertyId);
    boolean existsCoverByPropertyId(UUID propertyId);
}
