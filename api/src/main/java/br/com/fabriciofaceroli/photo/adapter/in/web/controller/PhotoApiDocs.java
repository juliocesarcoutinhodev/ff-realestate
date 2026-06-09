package br.com.fabriciofaceroli.photo.adapter.in.web.controller;

import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoUploadResponse;
import br.com.fabriciofaceroli.shared.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Photos", description = "Gerenciamento de fotos de imóveis")
@SecurityRequirement(name = "bearerAuth")
public interface PhotoApiDocs {

    @Operation(summary = "Lista fotos do imóvel", description = "Retorna todas as fotos de um imóvel ordenadas por capa primeiro e depois por order_index. Público, sem autenticação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de fotos (pode ser vazia)",
                    content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<PhotoUploadResponse>> listPhotos(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId
    );

    @Operation(summary = "Upload de fotos", description = "Faz upload de múltiplas fotos para um imóvel. A primeira foto vira capa automaticamente se o imóvel ainda não tiver nenhuma foto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fotos enviadas com sucesso",
                    content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "413", description = "Arquivo excede o limite de 10MB",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "415", description = "Formato de arquivo não suportado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<PhotoUploadResponse>> uploadPhotos(
            @Parameter(description = "ID do imóvel", required = true) UUID propertyId,
            @Parameter(description = "Arquivos de imagem (jpg, jpeg, png, webp)", required = true) List<MultipartFile> files
    );
}
