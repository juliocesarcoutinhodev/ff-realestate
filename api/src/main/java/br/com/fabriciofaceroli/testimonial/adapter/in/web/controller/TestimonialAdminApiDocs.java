package br.com.fabriciofaceroli.testimonial.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.ErrorResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.ReviewTestimonialRequest;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialAdminListResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialReviewResponse;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Admin — Testimonials", description = "Gerenciamento administrativo de depoimentos")
@SecurityRequirement(name = "bearerAuth")
public interface TestimonialAdminApiDocs {

    @Operation(
            summary = "Listar todos os depoimentos (admin)",
            description = "Retorna todos os depoimentos independente do status, paginados e ordenados por data de criação (mais recentes primeiro). Suporta filtro opcional por status. Requer autenticação com role ADMIN."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = TestimonialAdminListResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN")
    })
    ResponseEntity<ApiResponse<PageResponse<TestimonialAdminListResponse>>> listAll(
            @Parameter(description = "Filtrar por status: PENDING, APPROVED ou REJECTED (sem filtro = todos)", example = "PENDING")
            TestimonialStatus status,
            @Parameter(description = "Número da página (base 0)", example = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "12") int size
    );

    @Operation(
            summary = "Revisar depoimento (aprovar ou rejeitar)",
            description = "Altera o status de um depoimento PENDING para APPROVED ou REJECTED. Depoimentos já revisados (não PENDING) retornam 409. Requer autenticação com role ADMIN."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Depoimento revisado com sucesso",
                    content = @Content(schema = @Schema(implementation = TestimonialReviewResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Status inválido (deve ser APPROVED ou REJECTED)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Depoimento não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Depoimento já foi revisado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<TestimonialReviewResponse>> review(
            @Parameter(description = "ID do depoimento", required = true) @PathVariable UUID id,
            @Valid @RequestBody ReviewTestimonialRequest request
    );

    @Operation(
            summary = "Remover depoimento (admin)",
            description = "Remove permanentemente um depoimento do banco de dados. Requer autenticação com role ADMIN."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Depoimento removido com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Depoimento não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID do depoimento", required = true) @PathVariable UUID id
    );
}
