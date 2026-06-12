package br.com.fabriciofaceroli.photo.adapter.in.web.controller;

import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoOrderItemRequest;
import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoUploadResponse;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Photos", description = "Gerenciamento de fotos de imóveis")
@SecurityRequirement(name = "bearerAuth")
public interface PhotoApiDocs {

    @Operation(summary = "Lista fotos do imóvel", description = "Retorna todas as fotos de um imóvel ordenadas por capa primeiro e depois por order_index. Público, sem autenticação.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de fotos (pode ser vazia)",
                    content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> listPhotos(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId
    );

    @Operation(summary = "Upload de fotos", description = "Faz upload de múltiplas fotos para um imóvel. A primeira foto vira capa automaticamente se o imóvel ainda não tiver nenhuma foto.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Fotos enviadas com sucesso",
                    content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "413", description = "Arquivo excede o limite de 10MB",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "415", description = "Formato de arquivo não suportado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> uploadPhotos(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId,
            @Parameter(description = "Arquivos de imagem (jpg, jpeg, png, webp)", required = true) List<MultipartFile> files
    );

    @Operation(summary = "Define foto de capa", description = "Define a foto de capa do imóvel. A foto selecionada recebe cover=true e todas as demais recebem cover=false.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista atualizada de fotos do imóvel",
                    content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado ou foto não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> setCover(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId,
            @Parameter(description = "ID da foto a ser definida como capa", required = true) UUID photoId
    );

    @Operation(summary = "Reordena fotos", description = "Atualiza a ordem de exibição das fotos do imóvel em uma única transação. Envie todos os IDs das fotos com seus novos orderIndex. Requer autenticação (ADMIN).")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de fotos com a nova ordem aplicada",
                    content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payload inválido (id nulo ou orderIndex negativo)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado ou foto não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> reorderPhotos(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId,
            @Parameter(description = "Array com id e novo orderIndex de cada foto", required = true) @Valid List<PhotoOrderItemRequest> items
    );

    @Operation(summary = "Remove foto", description = "Remove uma foto específica de um imóvel do MinIO e do banco. Se a foto removida era a capa, a foto com menor order_index assume automaticamente. As fotos restantes são reordenadas sequencialmente. Requer autenticação (ADMIN).")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Foto removida com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Foto não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deletePhoto(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId,
            @Parameter(description = "ID da foto a ser removida", required = true) UUID photoId
    );
}
