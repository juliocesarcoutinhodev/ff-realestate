package br.com.fabriciofaceroli.auth.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserCredentials;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = UserRole.class)
public interface UserMapper {

    @Mapping(target = "role", expression = "java(entity.getRole().name())")
    User toUser(UserEntity entity);

    @Mapping(target = "role", expression = "java(entity.getRole().name())")
    UserCredentials toUserCredentials(UserEntity entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", expression = "java(UserRole.valueOf(user.role()))")
    UserEntity toEntity(User user);
}
