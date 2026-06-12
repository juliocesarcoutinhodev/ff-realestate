package br.com.fabriciofaceroli.sitesettings.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ErrorResponse;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.SiteSettingsResponse;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.UpdateSiteSettingsRequest;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.UploadSettingsImageResponse;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Site Settings", description = "Configurações do site")
public interface SiteSettingsApiDocs {

    @Operation(
            summary = "Busca configurações do site",
            description = "Retorna todas as configurações públicas do site (dados do corretor, hero, redes sociais, SEO). Endpoint público sem autenticação. Resposta cacheada por 1 hora."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Configurações carregadas com sucesso",
                    content = @Content(schema = @Schema(implementation = SiteSettingsResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Configurações não encontradas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ApiResponse<SiteSettingsResponse>> getSettings();

    @Operation(
            summary = "Atualiza configurações do site",
            description = "Substitui todas as configurações do site. Requer autenticação ADMIN. Invalida o cache Redis imediatamente.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Configurações atualizadas com sucesso",
                    content = @Content(schema = @Schema(implementation = SiteSettingsResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos (validação falhou)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Sem permissão (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ApiResponse<SiteSettingsResponse>> updateSettings(UpdateSiteSettingsRequest request);

    @Operation(
            summary = "Upload da foto do corretor",
            description = "Envia a foto de perfil do corretor (jpg, jpeg, png, webp — máx 5MB). Substitui a imagem anterior. Invalida o cache Redis. Requer autenticação ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Foto enviada com sucesso",
                    content = @Content(schema = @Schema(implementation = UploadSettingsImageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Arquivo muito grande (máx 5MB) ou formato inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Sem permissão (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ApiResponse<UploadSettingsImageResponse>> uploadBrokerPhoto(MultipartFile file);

    @Operation(
            summary = "Upload da imagem hero",
            description = "Envia a imagem principal do hero da home (jpg, jpeg, png, webp — máx 5MB). Substitui a imagem anterior. Invalida o cache Redis. Requer autenticação ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Imagem enviada com sucesso",
                    content = @Content(schema = @Schema(implementation = UploadSettingsImageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Arquivo muito grande (máx 5MB) ou formato inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Sem permissão (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ApiResponse<UploadSettingsImageResponse>> uploadHeroImage(MultipartFile file);
}
