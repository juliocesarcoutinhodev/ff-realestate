package br.com.fabriciofaceroli.auth.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.auth.domain.model.RefreshToken;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    RefreshToken toRefreshToken(RefreshTokenEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    RefreshTokenEntity toEntity(RefreshToken refreshToken);
}
