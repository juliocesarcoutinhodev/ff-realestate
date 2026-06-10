package br.com.fabriciofaceroli.category.adapter.in.web.controller;

import br.com.fabriciofaceroli.category.adapter.in.web.dto.CategoryResponse;
import br.com.fabriciofaceroli.category.adapter.in.web.dto.CreateCategoryRequest;
import br.com.fabriciofaceroli.category.adapter.in.web.dto.UpdateCategoryRequest;
import br.com.fabriciofaceroli.category.adapter.in.web.mapper.CategoryWebMapper;
import br.com.fabriciofaceroli.category.application.port.in.CreateCategoryPort;
import br.com.fabriciofaceroli.category.application.port.in.DeleteCategoryPort;
import br.com.fabriciofaceroli.category.application.port.in.GetCategoryBySlugPort;
import br.com.fabriciofaceroli.category.application.port.in.ListCategoriesPort;
import br.com.fabriciofaceroli.category.application.port.in.UpdateCategoryPort;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryApiDocs {

    private final ListCategoriesPort listCategoriesPort;
    private final GetCategoryBySlugPort getCategoryBySlugPort;
    private final CreateCategoryPort createCategoryPort;
    private final UpdateCategoryPort updateCategoryPort;
    private final DeleteCategoryPort deleteCategoryPort;
    private final CategoryWebMapper categoryWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listAll() {
        var categories = listCategoriesPort.listAll();
        return ResponseEntity.ok(ApiResponse.success("Categorias listadas com sucesso.", categoryWebMapper.toResponseList(categories)));
    }

    @Override
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getBySlug(@PathVariable String slug) {
        var category = getCategoryBySlugPort.getBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Categoria encontrada.", categoryWebMapper.toResponse(category)));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest request) {
        var category = createCategoryPort.create(categoryWebMapper.toCommand(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Categoria criada com sucesso.", categoryWebMapper.toResponse(category)));
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request) {
        var category = updateCategoryPort.update(categoryWebMapper.toUpdateCommand(id, request));
        return ResponseEntity.ok(ApiResponse.success("Categoria atualizada com sucesso.", categoryWebMapper.toResponse(category)));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCategoryPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
