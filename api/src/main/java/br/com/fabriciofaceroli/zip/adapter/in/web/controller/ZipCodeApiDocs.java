package br.com.fabriciofaceroli.zip.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.zip.domain.model.ZipCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "ZIP Code", description = "Consulta de CEP via CNPJá (proxy — token nunca exposto ao frontend)")
public interface ZipCodeApiDocs {

    @Operation(
            summary = "Consultar CEP",
            description = "Busca endereço a partir de um CEP usando a API CNPJá como provedor. Público — não requer autenticação."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "CEP encontrado",
            content = @Content(schema = @Schema(implementation = ZipCodeResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "CEP não encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Serviço de CEP indisponível")
    ResponseEntity<ApiResponse<ZipCodeResponse>> lookup(
            @Parameter(description = "CEP sem hífen", example = "03195000") String code
    );
}
