package br.com.fabriciofaceroli.category.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.category.infrastructure.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CategoryEntity toEntity(Category category);
}
