package br.com.fabriciofaceroli.property.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.infrastructure.persistence.entity.PropertyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(source = "coverPhotoUrl", target = "coverPhoto")
    Property toProperty(PropertyEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "coverPhotoUrl", ignore = true)
    PropertyEntity toEntity(Property property);
}
