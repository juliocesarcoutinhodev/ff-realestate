package br.com.fabriciofaceroli.property.adapter.in.web.controller;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.CreatePropertyRequest;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertyDetailResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.TogglePropertyStatusRequest;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.UpdatePropertyRequest;
import br.com.fabriciofaceroli.property.domain.model.DealType;
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

    @Operation(
            summary = "Buscar imóvel por slug",
            description = "Retorna os detalhes completos de um imóvel ativo pelo slug. Retorna 404 se o imóvel não existir ou estiver inativo. Público — não requer autenticação."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Imóvel encontrado",
            content = @Content(schema = @Schema(implementation = PropertyDetailResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado ou inativo")
    ResponseEntity<ApiResponse<PropertyDetailResponse>> getBySlug(
            @Parameter(description = "Slug do imóvel", example = "casa-nova-no-jardim-mirian") String slug
    );

    @Operation(
            summary = "Cadastrar imóvel",
            description = "Cria um novo imóvel com status ACTIVE. O slug é gerado automaticamente a partir do título. Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Imóvel cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = PropertySummaryResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    ResponseEntity<ApiResponse<PropertySummaryResponse>> create(CreatePropertyRequest request);

    @Operation(
            summary = "Atualizar imóvel",
            description = "Atualiza os dados de um imóvel existente. O slug é regenerado apenas se o título mudar. Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Imóvel atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = PropertySummaryResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado ou categoria inválida")
    ResponseEntity<ApiResponse<PropertySummaryResponse>> update(
            @Parameter(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
            UpdatePropertyRequest request
    );

    @Operation(
            summary = "Remover imóvel",
            description = "Remove permanentemente o imóvel e todas as fotos vinculadas (MinIO + banco). Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Imóvel removido com sucesso")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado")
    ResponseEntity<Void> delete(
            @Parameter(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440000") UUID id
    );

    @Operation(
            summary = "Alterar status do imóvel",
            description = "Ativa ou inativa um imóvel sem alterar nenhum outro dado. Requer autenticação com role ADMIN."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Status atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = PropertySummaryResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Status inválido")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado")
    ResponseEntity<ApiResponse<PropertySummaryResponse>> toggleStatus(
            @Parameter(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
            TogglePropertyStatusRequest request
    );
}
