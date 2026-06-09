package br.com.fabriciofaceroli.photo.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PhotoUploadResponse(

        @Schema(description = "ID da foto", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID id,

        @Schema(description = "URL pública da foto no MinIO", example = "https://storage.example.com/ffrealestate/properties/uuid/uuid-foto.jpg")
        String url,

        @Schema(description = "Ordem de exibição na galeria", example = "0")
        int orderIndex,

        @Schema(description = "Se é a foto de capa do imóvel", example = "true")
        boolean cover
) {}
