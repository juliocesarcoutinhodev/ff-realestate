package br.com.fabriciofaceroli.testimonial.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PropertySummaryResponse(

        @Schema(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID id,

        @Schema(description = "Título do imóvel", example = "Casa Nova no Jardim Mirian")
        String title,

        @Schema(description = "Slug do imóvel", example = "casa-nova-no-jardim-mirian")
        String slug
) {}
