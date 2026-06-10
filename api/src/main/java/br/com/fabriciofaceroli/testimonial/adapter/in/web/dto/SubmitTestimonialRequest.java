package br.com.fabriciofaceroli.testimonial.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SubmitTestimonialRequest(

        @NotBlank
        @Size(min = 2, max = 100)
        @Schema(description = "Nome do cliente", example = "João Silva")
        String clientName,

        @NotBlank
        @Size(min = 10, max = 500)
        @Schema(description = "Texto do depoimento", example = "Excelente atendimento! O Fabrício foi muito profissional.")
        String text,

        @Min(1)
        @Max(5)
        @Schema(description = "Avaliação de 1 a 5", example = "5")
        int rating,

        @Schema(description = "ID do imóvel relacionado (opcional)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID propertyId
) {}
