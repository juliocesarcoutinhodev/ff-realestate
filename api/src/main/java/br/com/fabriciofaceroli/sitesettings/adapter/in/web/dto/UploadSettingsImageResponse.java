package br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UploadSettingsImageResponse(

        @Schema(description = "URL pública da imagem enviada", example = "https://storage.fabriciofaceroli.com.br/ff-realestate/settings/broker-photo.jpg")
        String url
) {}
