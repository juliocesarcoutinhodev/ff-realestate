package br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record UpdateSiteSettingsRequest(

        @NotBlank
        @Schema(description = "Nome completo do corretor", example = "Fabrício Faceroli")
        String brokerName,

        @NotBlank
        @Schema(description = "Número do CRECI", example = "000000-F")
        String brokerCreci,

        @Schema(description = "Biografia do corretor", example = "Com mais de 15 anos de experiência...")
        String brokerBio,

        @URL
        @Schema(description = "URL da foto de perfil do corretor", example = "https://storage.fabriciofaceroli.com.br/broker/foto.jpg")
        String brokerPhotoUrl,

        @URL
        @Schema(description = "URL da imagem do hero da home", example = "https://storage.fabriciofaceroli.com.br/hero/banner.jpg")
        String heroImageUrl,

        @Schema(description = "Título principal do hero", example = "Fabrício Faceroli")
        String heroTitle,

        @Schema(description = "Subtítulo do hero", example = "Corretor de Imóveis de Alto Padrão")
        String heroSubtitle,

        @Schema(description = "Telefone de contato com DDD", example = "+55 (11) 99999-9999")
        String phone,

        @NotBlank
        @Pattern(regexp = "\\d{10,}", message = "whatsapp deve conter apenas dígitos, com mínimo de 10 caracteres")
        @Schema(description = "Número do WhatsApp no formato internacional sem símbolos (mínimo 10 dígitos)", example = "5511999999999")
        String whatsapp,

        @Schema(description = "Mensagem pré-preenchida ao abrir o WhatsApp", example = "Olá Fabrício, vim pelo site...")
        String whatsappMessage,

        @Email
        @Schema(description = "E-mail de contato", example = "contato@fabriciofaceroli.com.br")
        String email,

        @URL
        @Schema(description = "URL do perfil no Instagram", example = "https://instagram.com/fabriciofaceroli")
        String instagramUrl,

        @URL
        @Schema(description = "URL da página no Facebook", example = "https://facebook.com/fabriciofaceroli")
        String facebookUrl,

        @URL
        @Schema(description = "URL do perfil no LinkedIn", example = "https://linkedin.com/in/fabriciofaceroli")
        String linkedinUrl,

        @Schema(description = "Meta description para SEO", example = "Imóveis de alto padrão em São Paulo.")
        String metaDescription
) {}
