package br.com.fabriciofaceroli.photo.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.photo.infrastructure.persistence.entity.PropertyPhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PropertyPhotoMapper {

    PropertyPhoto toPropertyPhoto(PropertyPhotoEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    PropertyPhotoEntity toEntity(PropertyPhoto photo);
}
