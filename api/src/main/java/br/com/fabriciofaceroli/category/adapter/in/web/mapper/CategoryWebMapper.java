package br.com.fabriciofaceroli.category.adapter.in.web.mapper;

import br.com.fabriciofaceroli.category.adapter.in.web.dto.CategoryResponse;
import br.com.fabriciofaceroli.category.adapter.in.web.dto.CreateCategoryRequest;
import br.com.fabriciofaceroli.category.application.port.in.CreateCategoryCommand;
import br.com.fabriciofaceroli.category.domain.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryWebMapper {

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);

    CreateCategoryCommand toCommand(CreateCategoryRequest request);
}
