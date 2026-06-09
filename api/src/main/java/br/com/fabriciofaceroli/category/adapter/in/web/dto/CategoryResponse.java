package br.com.fabriciofaceroli.category.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CategoryResponse(

        @Schema(description = "ID da categoria", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome da categoria", example = "Apartamento")
        String name,

        @Schema(description = "Slug da categoria", example = "apartamento")
        String slug,

        @Schema(description = "Descrição da categoria", example = "Imóveis em condomínio vertical.")
        String description
) {}
