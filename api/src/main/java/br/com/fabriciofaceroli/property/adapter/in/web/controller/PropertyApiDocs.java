package br.com.fabriciofaceroli.property.adapter.in.web.controller;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Properties", description = "Catálogo de imóveis")
public interface PropertyApiDocs {

    @Operation(
            summary = "Listar imóveis ativos",
            description = "Retorna o catálogo paginado de imóveis com status ACTIVE. Suporta filtros por categoria, tipo de negócio, destaque e cidade. Público — não requer autenticação."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PropertySummaryResponse.class))
    )
    ResponseEntity<ApiResponse<PageResponse<PropertySummaryResponse>>> listActive(
            @Parameter(description = "Filtra por ID da categoria", example = "550e8400-e29b-41d4-a716-446655440000") UUID categoryId,
            @Parameter(description = "Filtra por tipo de negócio: SALE ou RENT", example = "SALE") DealType dealType,
            @Parameter(description = "Filtra apenas imóveis em destaque", example = "true") Boolean featured,
            @Parameter(description = "Filtra por cidade (busca parcial, case-insensitive)", example = "São Paulo") String city,
            @Parameter(description = "Número da página (base 0)", example = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "12") int size
    );
}
