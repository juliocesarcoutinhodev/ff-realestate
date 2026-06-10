package br.com.fabriciofaceroli.testimonial.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record TestimonialSubmitResponse(

        @Schema(description = "ID do depoimento criado", example = "550e8400-e29b-41d4-a716-446655440010")
        UUID id,

        @Schema(description = "Nome do cliente", example = "João Silva")
        String clientName,

        @Schema(description = "Avaliação de 1 a 5", example = "5")
        int rating,

        @Schema(description = "Status inicial do depoimento", example = "PENDING")
        String status
) {}
