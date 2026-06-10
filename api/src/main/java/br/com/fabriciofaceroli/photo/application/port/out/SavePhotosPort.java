package br.com.fabriciofaceroli.photo.application.port.out;

import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;

import java.util.List;

public interface SavePhotosPort {
    List<PropertyPhoto> saveAll(List<PropertyPhoto> photos);
}
