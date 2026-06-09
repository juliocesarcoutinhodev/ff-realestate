package br.com.fabriciofaceroli.category.adapter.in.web.mapper;

import br.com.fabriciofaceroli.category.adapter.in.web.dto.CategoryResponse;
import br.com.fabriciofaceroli.category.adapter.in.web.dto.CreateCategoryRequest;
import br.com.fabriciofaceroli.category.adapter.in.web.dto.UpdateCategoryRequest;
import br.com.fabriciofaceroli.category.application.port.in.CreateCategoryCommand;
import br.com.fabriciofaceroli.category.application.port.in.UpdateCategoryCommand;
import br.com.fabriciofaceroli.category.domain.model.Category;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryWebMapper {

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);

    CreateCategoryCommand toCommand(CreateCategoryRequest request);

    default UpdateCategoryCommand toUpdateCommand(UUID id, UpdateCategoryRequest request) {
        return new UpdateCategoryCommand(id, request.name(), request.description());
    }
}
