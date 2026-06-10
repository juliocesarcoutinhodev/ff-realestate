package br.com.fabriciofaceroli.photo.application.port.in;

import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;

import java.util.List;
import java.util.UUID;

public interface SetCoverPhotoPort {
    List<PropertyPhoto> setCover(UUID propertyId, UUID photoId);
}
