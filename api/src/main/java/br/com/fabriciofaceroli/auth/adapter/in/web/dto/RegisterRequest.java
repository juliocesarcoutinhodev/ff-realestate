package br.com.fabriciofaceroli.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.")
        @Schema(description = "Nome completo do usuário", example = "Fabrício Faceroli")
        String name,

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres.")
        @Schema(description = "E-mail do usuário", example = "fabricio@email.com")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "A senha deve ter no mínimo 8 caracteres, ao menos 1 letra maiúscula e 1 número."
        )
        @Schema(description = "Senha (mín. 8 caracteres, 1 maiúscula, 1 número)", example = "Senha123")
        String password
) {}
