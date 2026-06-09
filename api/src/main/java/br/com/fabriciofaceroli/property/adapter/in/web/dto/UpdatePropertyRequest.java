package br.com.fabriciofaceroli.property.adapter.in.web.dto;

import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePropertyRequest(

        @NotBlank
        @Schema(description = "Título do imóvel", example = "Casa Nova no Jardim Mirian")
        String title,

        @Schema(description = "Descrição completa do imóvel")
        String description,

        @NotNull
        @Positive
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

        @Schema(description = "Endereço completo", example = "Rua das Flores, 123")
        String address,

        @Schema(description = "Bairro", example = "Jardim Mirian")
        String neighborhood,

        @NotBlank
        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @NotBlank
        @Size(min = 2, max = 2)
        @Schema(description = "Estado (UF)", example = "SP")
        String state,

        @Schema(description = "CEP", example = "04000-000")
        String zipCode,

        @NotNull
        @Schema(description = "Tipo de negócio: SALE ou RENT", example = "SALE")
        DealType dealType,

        @Schema(description = "Se o imóvel está em destaque", example = "false")
        Boolean featured,

        @Schema(description = "URL do anúncio no portal externo", example = "https://www.imoveisxyz.com.br/imovel/173637")
        String externalUrl,

        @NotNull
        @Schema(description = "ID da categoria", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID categoryId,

        @Schema(description = "Status do imóvel: ACTIVE ou INACTIVE. Se omitido, mantém o status atual.", example = "ACTIVE")
        PropertyStatus status
) {}
