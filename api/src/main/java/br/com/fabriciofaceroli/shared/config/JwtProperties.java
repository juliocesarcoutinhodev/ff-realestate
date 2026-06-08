package br.com.fabriciofaceroli.shared.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

        @NotBlank(message = "JWT_SECRET é obrigatório")
        @Size(min = 32, message = "JWT_SECRET deve ter no mínimo 256 bits (32 caracteres)")
        String secret,

        @Positive
        long expirationMs,

        @Positive
        long refreshExpirationMs
) {}
