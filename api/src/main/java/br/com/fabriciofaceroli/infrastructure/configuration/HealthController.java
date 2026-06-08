package br.com.fabriciofaceroli.infrastructure.configuration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "System", description = "Endpoints operacionais da API")
public class HealthController {

    @GetMapping
    @Operation(
            summary = "Health check",
            description = "Verifica se a API está em execução. Não requer autenticação."
    )
    @ApiResponse(
            responseCode = "200",
            description = "API em execução",
            content = @Content(schema = @Schema(implementation = HealthResponse.class))
    )
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(HealthResponse.up());
    }
}
