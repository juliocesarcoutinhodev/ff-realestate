package br.com.fabriciofaceroli.dashboard.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Resumo do painel administrativo")
public record DashboardSummaryResponse(

        @Schema(description = "Total de imóveis ativos", example = "42")
        long totalActiveProperties,

        @Schema(description = "Total de imóveis inativos", example = "8")
        long totalInactiveProperties,

        @Schema(description = "Total de categorias cadastradas", example = "6")
        long totalCategories,

        @Schema(description = "Total de depoimentos pendentes de aprovação", example = "3")
        long totalPendingTestimonials,

        @Schema(description = "Últimos 5 imóveis cadastrados")
        List<RecentPropertyResponse> recentProperties,

        @Schema(description = "Últimos 5 depoimentos pendentes")
        List<PendingTestimonialResponse> pendingTestimonials
) {

    @Schema(description = "Resumo de imóvel recente")
    public record RecentPropertyResponse(

            @Schema(description = "ID do imóvel", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,

            @Schema(description = "Título do imóvel", example = "Casa Nova no Jardim Mirian")
            String title,

            @Schema(description = "Slug do imóvel", example = "casa-nova-jardim-mirian")
            String slug,

            @Schema(description = "Preço do imóvel", example = "450000.00")
            BigDecimal price,

            @Schema(description = "Status do imóvel", example = "ACTIVE")
            String status,

            @Schema(description = "Tipo de negócio", example = "SALE")
            String dealType,

            @Schema(description = "Data de cadastro", example = "2025-01-01T12:00:00")
            LocalDateTime createdAt
    ) {}

    @Schema(description = "Depoimento pendente de aprovação")
    public record PendingTestimonialResponse(

            @Schema(description = "ID do depoimento", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,

            @Schema(description = "Nome do cliente", example = "João Silva")
            String clientName,

            @Schema(description = "Texto do depoimento", example = "Excelente atendimento!")
            String text,

            @Schema(description = "Avaliação de 1 a 5", example = "5")
            Integer rating,

            @Schema(description = "Data de envio", example = "2025-01-01T12:00:00")
            LocalDateTime createdAt
    ) {}
}
