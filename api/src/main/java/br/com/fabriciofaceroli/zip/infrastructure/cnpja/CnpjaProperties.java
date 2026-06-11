package br.com.fabriciofaceroli.zip.infrastructure.cnpja;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "cnpja")
public record CnpjaProperties(

        @NotBlank(message = "CNPJA_API_URL é obrigatório")
        String apiUrl,

        @NotBlank(message = "CNPJA_API_TOKEN é obrigatório")
        String apiToken
) {}
