package br.com.fabriciofaceroli.photo.application.port.in;

import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;

import java.util.List;

public interface UploadPhotosPort {
    List<PropertyPhoto> upload(UploadPhotosCommand command);
}
