package br.com.fabriciofaceroli.shared.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.minio")
public record MinioProperties(

        @NotBlank(message = "MINIO_ENDPOINT é obrigatório")
        String endpoint,

        @NotBlank(message = "MINIO_ACCESS_KEY é obrigatório")
        String accessKey,

        @NotBlank(message = "MINIO_SECRET_KEY é obrigatório")
        String secretKey,

        @NotBlank(message = "MINIO_BUCKET é obrigatório")
        String bucket,

        @NotBlank(message = "MINIO_PUBLIC_URL é obrigatório")
        String publicUrl
) {}
