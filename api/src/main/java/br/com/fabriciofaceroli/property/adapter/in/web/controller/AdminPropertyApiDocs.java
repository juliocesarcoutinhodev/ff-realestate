package br.com.fabriciofaceroli.property.adapter.in.web.controller;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertyDetailResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Admin — Properties", description = "Gerenciamento administrativo do catálogo de imóveis")
@SecurityRequirement(name = "bearerAuth")
public interface AdminPropertyApiDocs {

    @Operation(
            summary = "Listar todos os imóveis (admin)",
            description = "Retorna todos os imóveis independente do status. Suporta filtro opcional por status. Requer autenticação com role ADMIN."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PropertySummaryResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    ResponseEntity<ApiResponse<PageResponse<PropertySummaryResponse>>> listAll(
            @Parameter(description = "Filtra por ID da categoria", example = "550e8400-e29b-41d4-a716-446655440000") UUID categoryId,
            @Parameter(description = "Filtra por tipo de negócio: SALE ou RENT", example = "SALE") DealType dealType,
            @Parameter(description = "Filtra apenas imóveis em destaque", example = "true") Boolean featured,
            @Parameter(description = "Filtra por cidade (busca parcial, case-insensitive)", example = "São Paulo") String city,
            @Parameter(description = "Filtra por status: ACTIVE ou INACTIVE (sem filtro = todos)", example = "ACTIVE") PropertyStatus status,
            @Parameter(description = "Número da página (base 0)", example = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "12") int size
    );

    @Operation(summary = "Buscar imóvel por ID (admin)", description = "Retorna todos os dados do imóvel independente do status. Requer autenticação com role ADMIN.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Imóvel encontrado", content = @Content(schema = @Schema(implementation = PropertyDetailResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    ResponseEntity<ApiResponse<PropertyDetailResponse>> getById(
            @Parameter(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440000") UUID id
    );
}
