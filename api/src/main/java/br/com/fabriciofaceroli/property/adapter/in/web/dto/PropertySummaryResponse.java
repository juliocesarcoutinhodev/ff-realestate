package br.com.fabriciofaceroli.property.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertySummaryResponse(

        @Schema(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Título do imóvel", example = "Casa Nova no Jardim Mirian")
        String title,

        @Schema(description = "Slug SEO-friendly", example = "casa-nova-no-jardim-mirian")
        String slug,

        @Schema(description = "Preço do imóvel", example = "450000.00")
        BigDecimal price,

        @Schema(description = "Área em m²", example = "125.00")
        BigDecimal area,

        @Schema(description = "Número de quartos", example = "3")
        Integer bedrooms,

        @Schema(description = "Número de suítes", example = "1")
        Integer suites,

        @Schema(description = "Número de banheiros", example = "2")
        Integer bathrooms,

        @Schema(description = "Vagas de garagem", example = "2")
        Integer parkingSpots,

        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @Schema(description = "Bairro", example = "Jardim Mirian")
        String neighborhood,

        @Schema(description = "Tipo de negócio: SALE ou RENT", example = "SALE")
        String dealType,

        @Schema(description = "Imóvel em destaque", example = "false")
        boolean featured,

        @Schema(description = "Status do imóvel: ACTIVE ou INACTIVE", example = "ACTIVE")
        String status,

        @Schema(description = "ID da categoria", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID categoryId,

        @Schema(description = "URL externa do anúncio original", example = "https://www.imoveisxyz.com.br/imovel/173637")
        String externalUrl,

        @Schema(description = "URL da foto de capa (disponível após upload via MinIO)", example = "https://storage.fabriciofaceroli.com.br/properties/uuid/capa.jpg", nullable = true)
        String coverPhoto
) {}
