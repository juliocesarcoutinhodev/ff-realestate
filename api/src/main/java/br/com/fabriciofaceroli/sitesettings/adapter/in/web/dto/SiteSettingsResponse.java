package br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SiteSettingsResponse(

        @Schema(description = "Nome completo do corretor", example = "Fabrício Faceroli")
        String brokerName,

        @Schema(description = "Número do CRECI", example = "000000-F")
        String brokerCreci,

        @Schema(description = "Biografia do corretor", example = "Com mais de 15 anos de experiência...")
        String brokerBio,

        @Schema(description = "URL da foto de perfil do corretor", example = "https://storage.fabriciofaceroli.com.br/broker/foto.jpg")
        String brokerPhotoUrl,

        @Schema(description = "URL da imagem do hero da home", example = "https://storage.fabriciofaceroli.com.br/hero/banner.jpg")
        String heroImageUrl,

        @Schema(description = "Título principal do hero", example = "Fabrício Faceroli")
        String heroTitle,

        @Schema(description = "Subtítulo do hero", example = "Corretor de Imóveis de Alto Padrão")
        String heroSubtitle,

        @Schema(description = "Telefone de contato com DDD", example = "+55 (11) 99999-9999")
        String phone,

        @Schema(description = "Número do WhatsApp no formato internacional sem símbolos", example = "5511999999999")
        String whatsapp,

        @Schema(description = "Mensagem pré-preenchida ao abrir o WhatsApp", example = "Olá Fabrício, vim pelo site...")
        String whatsappMessage,

        @Schema(description = "E-mail de contato", example = "contato@fabriciofaceroli.com.br")
        String email,

        @Schema(description = "URL do perfil no Instagram", example = "https://instagram.com/fabriciofaceroli")
        String instagramUrl,

        @Schema(description = "URL da página no Facebook", example = "https://facebook.com/fabriciofaceroli")
        String facebookUrl,

        @Schema(description = "URL do perfil no LinkedIn", example = "https://linkedin.com/in/fabriciofaceroli")
        String linkedinUrl,

        @Schema(description = "Meta description para SEO", example = "Imóveis de alto padrão em São Paulo.")
        String metaDescription
) {}
