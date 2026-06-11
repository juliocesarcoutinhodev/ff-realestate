package br.com.fabriciofaceroli.dashboard.adapter.in.web.controller;

import br.com.fabriciofaceroli.dashboard.adapter.in.web.dto.DashboardSummaryResponse;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin — Dashboard", description = "Resumo estatístico do painel administrativo")
@SecurityRequirement(name = "bearerAuth")
public interface DashboardApiDocs {

    @Operation(
            summary = "Obter resumo do dashboard",
            description = "Retorna contadores de imóveis ativos, inativos, categorias e depoimentos pendentes, além dos 5 imóveis mais recentes e 5 depoimentos aguardando aprovação. Requer autenticação com role ADMIN."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Resumo retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = DashboardSummaryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado — requer role ADMIN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary();
}
