package br.com.fabriciofaceroli.photo.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PhotoOrderItemRequest(

        @NotNull
        @Schema(description = "ID da foto", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID id,

        @Min(0)
        @Schema(description = "Nova posição na galeria (base 0)", example = "0")
        int orderIndex
) {}
