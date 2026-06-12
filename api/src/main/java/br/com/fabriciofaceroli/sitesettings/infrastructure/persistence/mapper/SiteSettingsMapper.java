package br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.entity.SiteSettingsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SiteSettingsMapper {

    SiteSettings toDomain(SiteSettingsEntity entity);

    @Mapping(target = "updatedAt", ignore = true)
    SiteSettingsEntity toEntity(SiteSettings domain);
}
