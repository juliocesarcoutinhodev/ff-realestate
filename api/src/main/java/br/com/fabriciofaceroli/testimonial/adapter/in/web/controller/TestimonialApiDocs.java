package br.com.fabriciofaceroli.testimonial.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.ErrorResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.SubmitTestimonialRequest;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialListResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialSubmitResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Testimonials", description = "Depoimentos de clientes")
public interface TestimonialApiDocs {

    @Operation(
            summary = "Listar depoimentos aprovados",
            description = "Retorna todos os depoimentos com status APPROVED, ordenados por data de criação (mais recentes primeiro). Não requer autenticação. Suporta filtro opcional por imóvel."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de depoimentos aprovados (pode ser vazia)",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TestimonialListResponse.class))))
    })
    ResponseEntity<ApiResponse<List<TestimonialListResponse>>> list(
            @Parameter(description = "Filtrar por ID do imóvel (opcional)")
            @RequestParam(required = false) UUID propertyId);

    @Operation(
            summary = "Enviar depoimento",
            description = "Permite que um cliente envie um depoimento sobre sua experiência. Não requer autenticação. O depoimento é criado com status PENDING e aguarda aprovação do administrador."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Depoimento enviado com sucesso",
                    content = @Content(schema = @Schema(implementation = TestimonialSubmitResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos (campos obrigatórios ausentes ou fora dos limites)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imóvel não encontrado (quando propertyId informado)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ApiResponse<TestimonialSubmitResponse>> submit(@Valid @RequestBody SubmitTestimonialRequest request);
}
