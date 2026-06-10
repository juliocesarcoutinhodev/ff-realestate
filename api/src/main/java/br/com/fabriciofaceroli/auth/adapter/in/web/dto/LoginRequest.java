package br.com.fabriciofaceroli.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Schema(description = "E-mail do usuário administrador", example = "fabricio@email.com")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        @Schema(description = "Senha do usuário administrador", example = "senhaSegura123")
        String password
) {}
