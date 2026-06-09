package br.com.fabriciofaceroli.category.adapter.in.web.controller;

import br.com.fabriciofaceroli.category.adapter.in.web.dto.CategoryResponse;
import br.com.fabriciofaceroli.category.adapter.in.web.dto.CreateCategoryRequest;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Categories", description = "Categorias de imóveis")
public interface CategoryApiDocs {

    @Operation(
            summary = "Listar categorias",
            description = "Retorna todas as categorias ordenadas por nome ASC. Público — não requer autenticação."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso (array vazio se não houver categorias)",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)))
    )
    ResponseEntity<ApiResponse<List<CategoryResponse>>> listAll();

    @Operation(
            summary = "Buscar categoria por slug",
            description = "Retorna uma categoria pelo slug. Usado pelo frontend para construir URLs SEO-friendly. Público — não requer autenticação."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Categoria encontrada",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    ResponseEntity<ApiResponse<CategoryResponse>> getBySlug(
            @Parameter(description = "Slug da categoria", example = "apartamento") String slug
    );

    @Operation(
            summary = "Criar categoria",
            description = "Cria uma nova categoria. O slug é gerado automaticamente a partir do nome. Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Categoria criada com sucesso",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Já existe uma categoria com esse nome")
    ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest request);
}
