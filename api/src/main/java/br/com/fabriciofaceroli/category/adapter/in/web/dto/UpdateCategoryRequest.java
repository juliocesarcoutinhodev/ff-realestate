package br.com.fabriciofaceroli.category.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        @Schema(description = "Nome da categoria", example = "Apartamento Compacto")
        String name,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        @Schema(description = "Descrição da categoria", example = "Studios e kitinetes.")
        String description
) {}
