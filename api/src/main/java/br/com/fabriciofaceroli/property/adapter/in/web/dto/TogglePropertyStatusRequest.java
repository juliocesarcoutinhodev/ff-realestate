package br.com.fabriciofaceroli.property.adapter.in.web.dto;

import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TogglePropertyStatusRequest(

        @NotNull
        @Schema(description = "Novo status do imóvel: ACTIVE ou INACTIVE", example = "INACTIVE")
        PropertyStatus status
) {}
