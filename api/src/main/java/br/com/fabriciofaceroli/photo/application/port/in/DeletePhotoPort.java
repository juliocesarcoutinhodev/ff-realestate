package br.com.fabriciofaceroli.photo.application.port.in;

import java.util.UUID;

public interface DeletePhotoPort {
    void delete(UUID propertyId, UUID photoId);
}
