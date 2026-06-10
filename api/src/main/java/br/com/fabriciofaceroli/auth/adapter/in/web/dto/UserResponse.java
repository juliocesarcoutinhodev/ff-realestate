package br.com.fabriciofaceroli.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserResponse(

        @Schema(description = "ID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome completo do usuário", example = "Fabrício Faceroli")
        String name,

        @Schema(description = "E-mail do usuário", example = "fabricio@email.com")
        String email,

        @Schema(description = "Papel/função do usuário", example = "ADMIN")
        String role
) {}
