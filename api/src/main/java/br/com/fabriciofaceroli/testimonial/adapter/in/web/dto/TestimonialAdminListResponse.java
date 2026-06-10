package br.com.fabriciofaceroli.testimonial.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record TestimonialAdminListResponse(

        @Schema(description = "ID do depoimento", example = "550e8400-e29b-41d4-a716-446655440010")
        UUID id,

        @Schema(description = "Nome do cliente", example = "João Silva")
        String clientName,

        @Schema(description = "Texto do depoimento", example = "Excelente atendimento!")
        String text,

        @Schema(description = "Avaliação de 1 a 5", example = "5")
        int rating,

        @Schema(description = "Status do depoimento", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
        String status,

        @Schema(description = "Imóvel relacionado (nulo quando depoimento é genérico)")
        PropertySummaryResponse property,

        @Schema(description = "Data de criação em UTC", example = "2025-01-01T12:00:00Z")
        Instant createdAt
) {}
