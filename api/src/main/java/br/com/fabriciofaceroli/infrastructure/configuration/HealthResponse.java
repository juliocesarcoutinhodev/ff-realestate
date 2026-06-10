package br.com.fabriciofaceroli.infrastructure.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Schema(description = "Resposta do health check")
public record HealthResponse(

        @Schema(description = "Status da aplicação", example = "UP")
        String status,

        @Schema(description = "Timestamp ISO-8601 do momento da verificação", example = "2025-01-01T12:00:00Z")
        String timestamp
) {
    public static HealthResponse up() {
        return new HealthResponse("UP", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
    }
}
